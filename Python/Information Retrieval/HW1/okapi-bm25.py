__author__ = 'Ameya'

import helper
import constants
from math import log
from datetime import datetime
from elasticsearch import Elasticsearch

if __name__ == '__main__':
    es = Elasticsearch(constants.CLIENT, timeout=600, max_retries=10, revival_delay=0)
    start = datetime.now()
    doc_count = es.cat.indices(index=constants.INDEX_NAME).split()[5]

    okapi_bm25_output = open(constants.OUTPUT_DIRECTORY + 'okapi-bm25_results.txt', 'w')
    avg_doc_len = helper.get_doc_stats(constants.AVERAGE_DOC_LENGTH_IDENTIFIER, start)
    dic = helper.load_length_in_dic(start)

    with open(constants.QUERIES_TEXT_FILE) as queries:
        for line in iter(queries):
            if line.__contains__("."):
                query = helper.get_query(line.lower())
                query_no = line.lower().split(".   ")[0]

                all_terms = helper.get_all_terms(query, start)
                terms = helper.query_custom_stem(all_terms)

                dict_okapi_bm25 = {}

                tf_query = {}
                for term in terms:
                    if tf_query.__contains__(term):
                        tf_query[term] += 1
                    else:
                        tf_query[term] = 1

                for term in terms:
                    dfw = 0
                    t_bm25 = 0
                    tf_q = tf_query[term]
                    result = helper.get_term_freq_all(es, term, doc_count, 0.009)
                    dfw = result['hits']['total']
                    for doc in result['hits']['hits']:
                        doc_id = str(doc['_id'])
                        tf = int(doc['_score'])
                        doc_length = int(dic[doc_id])

                        t_bm25 = float(log(float(float(doc_count) + 0.5)/(dfw + 0.5)))
                        t_bm25 = float(t_bm25 * float(float(tf * float(1 + 1.2))/float(tf + float(1.2 * (float(1 - 0.75) + float(0.75 * (doc_length/avg_doc_len)))))))
                        t_bm25 = float(t_bm25 * (float(tf_q * (1 + 100))/float(tf_q + 100)))

                        if t_bm25 > 0.0:
                            if doc_id in dict_okapi_bm25:
                                dict_okapi_bm25[doc_id] += t_bm25
                            else:
                                dict_okapi_bm25[doc_id] = t_bm25

                helper.write_result_to_file(dict_okapi_bm25, query_no, okapi_bm25_output)

    okapi_bm25_output.close()
    print datetime.now() - start