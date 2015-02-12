__author__ = 'Ameya'

import constants
from math import log
from datetime import datetime
from elasticsearch import Elasticsearch
from helper import get_term_freq_all, get_all_terms, get_query, get_doc_stats, load_length_in_dic, query_custom_stem


if __name__ == '__main__':
    es = Elasticsearch(constants.CLIENT, timeout=600, max_retries=10, revival_delay=0)
    start = datetime.now()
    doc_count = es.cat.indices(index=constants.INDEX_NAME).split()[5]

    okapi_bm25_output = open(constants.OUTPUT_DIRECTORY + 'okapi-bm25_results.txt', 'w')
    avg_doc_len = get_doc_stats(constants.AVERAGE_DOC_LENGTH_IDENTIFIER, start)
    dic = load_length_in_dic(start)

    with open(constants.QUERIES_TEXT_FILE) as queries:
        for line in iter(queries):
            if line.__contains__("."):
                query = get_query(line.lower())
                query_no = line.lower().split(".   ")[0]

                all_terms = get_all_terms(query, start)
                terms = query_custom_stem(all_terms)

                dict_okapi_bm25 = {}
                dict_okapi_bm25_sorted = {}

                tf_query = {}
                for stem in terms:
                    if tf_query.__contains__(stem):
                            tf_query[stem] += 1
                    else:
                        tf_query[stem] = 1

                for stem in terms:
                    dfw = 0
                    t_bm25 = 0
                    tf_q = tf_query[stem]
                    result = get_term_freq_all(es, stem, doc_count, 0.009)
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

                if dict_okapi_bm25.__len__() > 1000:
                    dict_okapi_bm25_sorted = sorted(dict_okapi_bm25.items(), key=lambda x: x[1], reverse=True)[:1000]
                else:
                    dict_okapi_bm25_sorted = sorted(dict_okapi_bm25.items(), key=lambda x: x[1], reverse=True)

                rank = 1
                lines = []
                for doc in dict_okapi_bm25_sorted:
                    lines.append(str(query_no) + " Q0 " + doc[0] + " " + str(rank) + " " + str(doc[1]) + " Exp\n")
                    rank += 1
                okapi_bm25_output.writelines(lines)

    okapi_bm25_output.close()
    print datetime.now() - start