__author__ = 'Ameya'

import helper
import constants
from datetime import datetime
from elasticsearch import Elasticsearch


def stem_query_default_stemmer(all_qry_terms):
    set_of_terms = []

    term_match = {}
    for stem_term in all_qry_terms:
        term_match[stem_term] = 1

    with open(constants.STEM_CLASSES_FILE) as dictionary:
        for ln in dictionary:
            ln = ln.lower()
            if ln.__contains__(" |  ") and ln.split(" |  ")[1]:
                if ln.split(" |  ")[1].__contains__(" "):
                    for one_term in ln.split(" |  ")[1].split(" "):
                        for stem_term in all_qry_terms:
                            if one_term == stem_term and term_match[stem_term] == 1:
                                set_of_terms.append(ln.split(" |  ")[0])
                                term_match[stem_term] = 0
                                break
                else:
                    if ln.split(" |  ")[1] == stem_term:
                        set_of_terms.append(ln.split(" |  ")[0])
                        term_match[stem_term] = 0

    for stem_term in term_match:
        if term_match[stem_term] == 1:
            set_of_terms.append(stem_term)

    return set_of_terms


if __name__ == '__main__':
    es = Elasticsearch(constants.CLIENT, timeout=600, max_retries=10, revival_delay=0)
    start = datetime.now()
    doc_count = es.cat.indices(index=constants.INDEX_NAME).split()[5]

    okapi_tf_output = open(constants.OUTPUT_DIRECTORY + 'okapi-tf_results.txt', 'w')
    avg_doc_len = helper.get_doc_stats(constants.AVERAGE_DOC_LENGTH_IDENTIFIER, start)
    dic = helper.load_length_in_dic(start)

    with open(constants.QUERIES_TEXT_FILE) as queries:
        for line in iter(queries):
            if line.__contains__("."):
                query = helper.get_query(line.lower())

                all_terms = helper.get_all_terms(query, start)
                terms = stem_query_default_stemmer(all_terms)

                okapi_tf_dictionary = {}

                for term in terms:
                    results = helper.get_term_freq_all(es, term, doc_count, 0.009)
                    for doc in results['hits']['hits']:
                        doc_tf = 0
                        doc_id = str(doc['_id'])
                        tf = int(doc['_score'])
                        doc_tf += (tf / (tf + 0.5 + (1.5 * (int(dic[doc_id])/avg_doc_len))))

                        if doc_id in okapi_tf_dictionary:
                            okapi_tf_dictionary[doc_id] += doc_tf
                        else:
                            okapi_tf_dictionary[doc_id] = doc_tf

                helper.write_result_to_file(okapi_tf_dictionary, line.lower().split(".   ")[0], okapi_tf_output)

    okapi_tf_output.close()
    print datetime.now() - start