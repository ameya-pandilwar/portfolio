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

    tf_idf_output = open(constants.OUTPUT_DIRECTORY + 'tf-idf_results.txt', 'w')
    avg_doc_len = get_doc_stats(constants.AVERAGE_DOC_LENGTH_IDENTIFIER, start)
    dic = load_length_in_dic(start)

    with open(constants.QUERIES_TEXT_FILE) as queries:
        for line in iter(queries):
            if line.__contains__("."):
                query = get_query(line.lower())
                query_no = line.lower().split(".   ")[0]

                all_terms = get_all_terms(query, start)
                terms = query_custom_stem(all_terms)

                dict_tf_idf = {}
                dict_tf_idf_sorted = {}

                for stem in terms:
                    dfw = 0
                    doc_tf = 0
                    result = get_term_freq_all(es, stem, doc_count, 0.009)
                    dfw = result['hits']['total']
                    docs = result['hits']['hits']
                    for doc in docs:
                        doc_id = str(doc['_id'])
                        tf = int(doc['_score'])
                        doc_length = int(dic[doc_id])
                        doc_tf = (tf / (tf + 0.5 + (1.5 * (doc_length/avg_doc_len))))

                        doc_tf = float(doc_tf * (log(float(doc_count)/float(dfw))))

                        if doc_tf > 0.0:
                            if doc_id in dict_tf_idf:
                                dict_tf_idf[doc_id] += doc_tf
                            else:
                                dict_tf_idf[doc_id] = doc_tf

                if dict_tf_idf.__len__() > 1000:
                    dict_tf_idf_sorted = sorted(dict_tf_idf.items(), key=lambda x: x[1], reverse=True)[:1000]
                else:
                    dict_tf_idf_sorted = sorted(dict_tf_idf.items(), key=lambda x: x[1], reverse=True)

                rank = 1
                lines = []
                for doc in dict_tf_idf_sorted:
                    lines.append(str(query_no) + " Q0 " + doc[0] + " " + str(rank) + " " + str(doc[1]) + " Exp\n")
                    rank += 1
                tf_idf_output.writelines(lines)

    tf_idf_output.close()
    print datetime.now() - start