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

    tf_idf_output = open(constants.OUTPUT_DIRECTORY + 'tf-idf_results.txt', 'w')
    avg_doc_len = helper.get_doc_stats(constants.AVERAGE_DOC_LENGTH_IDENTIFIER, start)
    dic = helper.load_length_in_dic(start)

    with open(constants.QUERIES_TEXT_FILE) as queries:
        for line in iter(queries):
            if line.__contains__("."):
                query = helper.get_query(line.lower())
                query_no = line.lower().split(".   ")[0]

                all_terms = helper.get_all_terms(query, start)
                terms = helper.query_custom_stem(all_terms)

                dict_tf_idf = {}

                for term in terms:
                    doc_tf = 0
                    result = helper.get_term_freq_all(es, term, doc_count, 0.009)
                    for doc in result['hits']['hits']:
                        doc_id = str(doc['_id'])
                        tf = int(doc['_score'])
                        doc_tf = (tf / (tf + 0.5 + (1.5 * (int(dic[doc_id])/avg_doc_len))))
                        doc_tf = float(doc_tf * (log(float(doc_count)/float(result['hits']['total']))))

                        if doc_id in dict_tf_idf:
                            dict_tf_idf[doc_id] += doc_tf
                        else:
                            dict_tf_idf[doc_id] = doc_tf

                helper.write_result_to_file(dict_tf_idf, query_no, tf_idf_output)

    tf_idf_output.close()
    print datetime.now() - start