__author__ = 'Ameya'

import re
import helper
import constants
from datetime import datetime
from collections import OrderedDict


def compute_min_span(term_pos):
    min_span = constants.INITIAL_MINIMUM_VALUE
    min_pos = {}

    for var in term_pos:
        min_pos[var] = 0

    next_span = True
    while next_span:
        min_pos_of_term = {}
        for var in min_pos.keys():
            min_pos_of_term[term_pos[var][min_pos[var]]] = str(var)

        cur_min = min(a for a in min_pos_of_term.keys())
        cur_max = max(a for a in min_pos_of_term.keys())

        if min_span > cur_max - cur_min:
            min_span = cur_max - cur_min

        min_pos_of_term_temp = OrderedDict(sorted(min_pos_of_term.items(), key=lambda x: x[0]))

        next_min_flag = True

        for key in min_pos_of_term_temp.keys():
            if len(term_pos[min_pos_of_term[key]]) - 1 > min_pos[min_pos_of_term[key]]:
                min_pos[min_pos_of_term[key]] += 1
                next_min_flag = False
                break
        if next_min_flag:
            next_span = False

    return min_span + 1


if __name__ == '__main__':
    start = datetime.now()

    helper.load_corpus_term_frequency_map()
    helper.load_documents_id_length_map()
    helper.load_corpus_statistics()
    helper.load_catalog_file()

    proximity_search_output = open(constants.OUTPUT_RESULTS_PATH + 'proximity-search_results.txt', "w")

    with open(constants.QUERIES_TEXT_FILE) as queries:
        patt = re.compile(constants.REGULAR_EXPRESSION_PATTERN)

        for line in iter(queries):
            if line.__contains__("."):
                query = line.lower()
                query_no = query.split(".   ")[0]
                query = query.split(".   ")[1]
                all_text_tokens = ""

                for iter_term in patt.finditer(query):
                    all_text_tokens += iter_term.group() + " "

                all_terms = helper.get_all_terms(all_text_tokens, start)
                terms = helper.query_custom_stem(all_terms)
                dict_result = {}
                query_pos = {}

                for stem in terms:
                    if constants.term_id_map.has_key(stem):
                        term_id = constants.term_id_map[stem]
                        start_fetch = constants.term_offset_map[term_id][0]
                        result = helper.fetch_doc_freq(start_fetch, 0, 1)
                        if len(result) > 0:
                            query_pos[stem] = result

                for doc in constants.doc_id_map.keys():
                    term_pos_for_query = {}
                    found_terms = 0

                    for stem in terms:
                        if doc in query_pos[stem]:
                            term_pos_for_query[stem] = query_pos[stem][doc]
                    found_terms = len(term_pos_for_query)

                    if found_terms > 1:
                        doc_id = str(constants.doc_id_map[doc])
                        s = compute_min_span(term_pos_for_query)
                        doc_length = int(constants.doc_length_map[doc_id])
                        doc_score = float(((1500 - s) * found_terms) / float(doc_length + int(len(constants.term_id_map))))
                        dict_result[doc_id] = doc_score

                helper.write_result_to_file(dict_result, query_no, proximity_search_output)

    proximity_search_output.close()
    print "completed in - " + str(datetime.now() - start)