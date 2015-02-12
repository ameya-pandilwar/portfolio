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

    unigram_lm_jm_output = open(constants.OUTPUT_DIRECTORY + 'unigram-lm-jelinek-mercer_results.txt', 'w')
    avg_doc_len = helper.get_doc_stats(constants.AVERAGE_DOC_LENGTH_IDENTIFIER, start)
    all_doc_length = helper.get_doc_stats(constants.TOTAL_DOCUMENT_LENGTH_IDENTIFIER, start)
    dic = helper.load_length_in_dic(start)

    with open(constants.QUERIES_TEXT_FILE) as queries:
        for line in iter(queries):
            if line.__contains__("."):
                query = helper.get_query(line.lower())
                query_no = line.lower().split(".   ")[0]

                all_terms = helper.get_all_terms(query, start)
                terms = helper.query_custom_stem(all_terms)

                dict_result = {}
                dict_term_freq = {}
                search_docs = []
                cf_word = {}
                for term in terms:
                    term_freq = {}
                    cf_w = 0
                    results = helper.get_term_freq_all(es, term, doc_count, 0.009)
                    for doc in results['hits']['hits']:
                        doc_id = str(doc['_id'])
                        tf = int(doc['_score'])
                        cf_w += tf
                        if not search_docs.__contains__(doc_id):
                            search_docs.append(doc_id)
                        term_freq[doc_id] = tf
                    cf_word[term] = cf_w

                    dict_term_freq[term] = term_freq

                for each_doc in search_docs:
                    doc_score = 0
                    doc_length = int(dic[each_doc])
                    d_dash_length = all_doc_length - doc_length

                    for term in terms:
                        tf = 0
                        term_freq = dict_term_freq[term]
                        lambda_value = float(doc_length/(doc_length + avg_doc_len))
                        if each_doc in term_freq:
                            tf = term_freq[each_doc]
                            tf_dash = doc_length - tf

                        doc_score = float(doc_score + float(log(float((lambda_value * tf) / float(doc_length)) + float(float(1 - lambda_value) * float(tf_dash)/float(d_dash_length)))))
                    dict_result[each_doc] = doc_score

                helper.write_result_to_file(dict_result, query_no, unigram_lm_jm_output)

    unigram_lm_jm_output.close()
    print datetime.now() - start