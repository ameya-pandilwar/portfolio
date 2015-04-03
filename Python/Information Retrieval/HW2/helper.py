__author__ = 'Ameya'

import re
import constants
from datetime import datetime
from collections import OrderedDict


def parse_file(filename, stopwords_flag, stem_flag):
    doc_no = ''
    ret_val = {}
    doc_text = ''
    append_text = False

    with open(filename) as current_file:
        for line in iter(current_file):
            if line.__contains__(constants.DOCNO_BEGIN_TAG):
                doc_no = line.split(" ")[1]

            if line.__contains__(constants.TEXT_BEGIN_TAG) or append_text:
                append_text = True
                if not line.__contains__(constants.TEXT_END_TAG):
                    if not line.__contains__(constants.TEXT_BEGIN_TAG):
                        doc_text += line
                else:
                    append_text = False

            if line.__contains__(constants.DOC_END_TAG):
                ret_val[doc_no] = stem_words(doc_text.lower(), doc_no, stopwords_flag, stem_flag)
                doc_text = ""

    return ret_val


def stem_words(_text, _docno, stopwords_flag, stem_flag):
    raw_pos = 0
    terms = {}

    for iter_term in re.finditer(constants.REGULAR_EXPRESSION_PATTERN, _text):
        term = iter_term.group()
        raw_pos += 1

        if stopwords_flag:
            if term not in constants.stops:
                terms[raw_pos] = term
            else:
                continue

        if stem_flag:
            terms[raw_pos] = constants.stems[term] if term in constants.stems else term

    constants.doc_lengths[_docno] = raw_pos
    return terms


def write_dict_into_file(path, dictionary, flag):
    ordered_dict = OrderedDict(sorted(dictionary.items(), key=lambda x: x[1]))
    term_map = open(path, constants.WRITE_BINARY)

    if flag is 1:
        doc_count = 0
        total_doc_length = 0

    for key in ordered_dict.keys():
        term_id = ordered_dict[key]
        _line = str(key) + ' ' + str(term_id)

        if flag is 1:
            doc_count += 1
            total_doc_length += constants.doc_lengths[key]

        _line += ' ' + str(constants.ctf_map[term_id]) if flag is 0 else ' ' + str(constants.doc_lengths[key])
        term_map.write(_line + '\n')

    term_map.close()

    if flag is 1:
        doc_stats = open(constants.CORPUS_STATISTICS, constants.WRITE_BINARY)
        _str = constants.TOTAL_DOCUMENTS_IDENTIFIER + str(doc_count) + '\n'
        doc_stats.write(_str)
        _str = constants.TOTAL_DOCUMENT_LENGTH_IDENTIFIER + str(total_doc_length) + '\n'
        doc_stats.write(_str)
        _str = constants.AVERAGE_DOC_LENGTH_IDENTIFIER + str(total_doc_length / doc_count) + '\n'
        doc_stats.write(_str)
        doc_stats.close()


def fetch_doc_freq(begin, end, flag):
    term_freq = {}

    index = open(constants.INDEX_FILE, constants.READ_BINARY)
    index.seek(begin, 0)
    if flag is 0:
        dblocks = index.read(end - begin - 1).split(" ")[1].lstrip(":").split(":")
    elif flag is 1:
        dblocks = index.readline().split(" ")[1].lstrip(":").split(":")

    for dblock in dblocks:
        block = dblock.split("{")
        l_d_id = block[0]

        if flag is 0:
            l_tf = block[1].split(",")[0]
            term_freq[l_d_id] = int(l_tf)
        elif flag is 1:
            l_pos_list = block[1].rstrip().split(",")
            l_pos_list_int = []
            for i in iter(l_pos_list):
                l_pos_list_int.append(int(i))
            term_freq[l_d_id] = l_pos_list_int

    return term_freq


def load_corpus_term_frequency_map():
    with open(constants.TERM_MAP_FILE, constants.READ_BINARY) as file_terms:
        for line in iter(file_terms):
            if line.__contains__(" "):
                t = line.split(" ")
                constants.term_id_map[t[0]] = t[1]
                constants.corp_term_freq_map[t[0]] = int(t[2])


def load_documents_id_length_map():
    with open(constants.DOC_MAP_FILE, constants.READ_BINARY) as doc_list_length:
        for line in iter(doc_list_length):
            if line.__contains__(" "):
                doc_info = line.split(" ")
                constants.doc_length_map[doc_info[0]] = int(doc_info[2])
                constants.doc_id_map[doc_info[1]] = doc_info[0]


def load_corpus_statistics():
    with open(constants.CORPUS_STATISTICS, constants.READ_BINARY) as doc_stats:
        for line in iter(doc_stats):
            if line.__contains__(constants.AVERAGE_DOC_LENGTH_IDENTIFIER):
                constants.avg_doc_len = int(line.split()[1])
            if line.__contains__(constants.TOTAL_DOCUMENTS_IDENTIFIER):
                constants.doc_count = int(line.split()[1])


def load_catalog_file():
    with open(constants.CATALOG_FILE, constants.READ_BINARY) as catalog:
        for line in iter(catalog):
            if line.__contains__(" "):
                term = line.split()[0]
                start_off = int(line.split()[1])
                end_off = int(line.split()[2])
                off = [start_off, end_off]
                constants.term_offset_map[term] = off


def get_query(line):
    return line.split(".   ")[1].replace("-", " ").replace(",", "").replace("(", "").replace(")", "")


def get_all_terms(query, start):
    all_terms = []
    ignore_terms = ["document", "discuss", "report", "include", "describe", "identify", "predict", "cite"]

    for term in query.split(" "):
        ignore_term = False

        with open(constants.STOPLIST_TEXT_FILE) as stop_list:
            for line in iter(stop_list):
                if line.lower().__contains__(term) or ignore_terms.__contains__(term):
                    ignore_term = True
                    break
        if not ignore_term:
            all_terms.append(term)

    print str(datetime.now() - start) + " parsing query terms - " + str(all_terms)
    return all_terms


def query_custom_stem(all_terms):
    term_match = {}
    for term in all_terms:
        term_match[term] = 1

    terms = []
    with open(constants.CUSTOM_STEMMER_FILE) as dictionary:
        for line in dictionary:
            line = line.lower()
            if line.__contains__(" "):
                line_term = line.split(" ")[0]
                for term in all_terms:
                    if line_term == term and term_match[term] == 1:
                        terms.append(line.split(" ")[1].split("\n")[0])
                        term_match[term] = 0
                        break

    for term in term_match:
        if term_match[term] == 1:
            terms.append(term)

    return terms


def write_result_to_file(dictionary, query_no, output_file):
    rank = 1
    lines = []
    for doc in sorted(dictionary.items(), key=lambda x: x[1], reverse=True)[:1000]:
        lines.append(str(query_no) + ' Q0 ' + doc[0] + ' ' + str(rank) + ' ' + str(doc[1]) + ' Exp\n')
        rank += 1
    output_file.writelines(lines)