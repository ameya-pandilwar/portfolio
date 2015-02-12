__author__ = 'Ameya'

import constants
from datetime import datetime
from elasticsearch import Elasticsearch
from helper import get_term_freq_all, get_all_terms, get_query, get_doc_stats, load_length_in_dic


def stem_query_default_stemmer(all_qry_terms):
    set_of_terms = []

    term_match = {}
    for term in all_qry_terms:
        term_match[term] = 1

    with open(constants.STEM_CLASSES_FILE) as dictionary:
        for ln in dictionary:
            ln = ln.lower()
            if ln.__contains__(" |  "):
                if ln.split(" |  ")[1]:
                    if ln.split(" |  ")[1].__contains__(" "):
                        for one_term in ln.split(" |  ")[1].split(" "):
                            one_term = str(one_term)
                            for term in all_qry_terms:
                                term = str(term)
                                if one_term == term and term_match[term] == 1:
                                    set_of_terms.append(ln.split(" |  ")[0])
                                    term_match[term] = 0
                                    break
                        else:
                            if ln.split(" |  ")[1] == term:
                                set_of_terms.append(ln.split(" |  ")[0])
                                term_match[term] = 0

    for term in term_match:
        if term_match[term] == 1:
            set_of_terms.append(term)

    return set_of_terms


if __name__ == '__main__':
    es = Elasticsearch(constants.CLIENT, timeout=600, max_retries=10, revival_delay=0)
    start = datetime.now()
    doc_count = es.cat.indices(index=constants.INDEX_NAME).split()[5]

    okapi_tf_output = open(constants.OUTPUT_DIRECTORY + 'okapi-tf_results.txt', 'w')
    avg_doc_len = get_doc_stats(constants.AVERAGE_DOC_LENGTH_IDENTIFIER, start)
    dic = load_length_in_dic(start)

    with open(constants.QUERIES_TEXT_FILE) as queries:
        for line in iter(queries):
            if line.__contains__("."):
                query = get_query(line.lower())
                query_no = line.lower().split(".   ")[0]

                all_terms = get_all_terms(query, start)
                terms = stem_query_default_stemmer(all_terms)

                dict_okapi_tf = {}
                dict_okapi_tf_sorted = {}

                for stem in terms:
                    results = get_term_freq_all(es, stem, doc_count, 0.009)
                    for doc in results['hits']['hits']:
                        doc_tf = 0
                        doc_id = str(doc['_id'])
                        tf = int(doc['_score'])
                        doc_length = int(dic[doc_id])
                        doc_tf += (tf / (tf + 0.5 + (1.5 * (doc_length/avg_doc_len))))

                        if doc_tf > 0.0:
                            if doc_id in dict_okapi_tf:
                                dict_okapi_tf[doc_id] += doc_tf
                            else:
                                dict_okapi_tf[doc_id] = doc_tf
                dict_okapi_tf_sorted = sorted(dict_okapi_tf.items(), key=lambda x: x[1], reverse=True)[:1000]

                rank = 1
                lines = []
                for doc in dict_okapi_tf_sorted:
                    lines.append(str(query_no) + ' Q0 ' + doc[0] + ' ' + str(rank) + ' ' + str(doc[1]) + ' Exp\n')
                    rank += 1
                okapi_tf_output.writelines(lines)

    okapi_tf_output.close()
    print datetime.now() - start