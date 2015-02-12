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

    unigram_lm_laplace_output = open(constants.OUTPUT_DIRECTORY + 'unigram-lm-laplace_results.txt', 'w')
    avg_doc_len = get_doc_stats(constants.AVERAGE_DOC_LENGTH_IDENTIFIER, start)
    dic = load_length_in_dic(start)

    with open(constants.QUERIES_TEXT_FILE) as queries:
        for line in iter(queries):
            if line.__contains__("."):
                query = get_query(line.lower())
                query_no = line.lower().split(".   ")[0]

                all_terms = get_all_terms(query, start)
                terms = query_custom_stem(all_terms)

                dict_result = {}

                dict_term_freq = {}
                search_docs = []
                for stem in terms:
                    term_freq = {}
                    results = get_term_freq_all(es, stem, doc_count, 0.009)
                    for doc in results['hits']['hits']:
                        doc_id = str(doc['_id'])
                        tf = int(doc['_score'])
                        if not search_docs.__contains__(doc_id):
                            search_docs.append(doc_id)
                        term_freq[doc_id] = tf

                    dict_term_freq[stem] = term_freq

                for each_doc in search_docs:
                    doc_score = 0
                    doc_length = int(dic[each_doc])
                    for stem in terms:
                        tf = 0
                        term_freq = dict_term_freq[stem]
                        if each_doc in term_freq:
                            tf = term_freq[each_doc]

                        doc_score = float(doc_score + float(log(float((tf + 1) / float(doc_length + 20000)))))

                    dict_result[each_doc] = doc_score

                if dict_result.__len__() > 1000:
                    dict_unigram_lm_laplace_sorted = sorted(dict_result.items(), key=lambda x: x[1], reverse=True)[:1000]
                else:
                    dict_unigram_lm_laplace_sorted = sorted(dict_result.items(), key=lambda x: x[1], reverse=True)

                rank = 1
                files = []
                for doc in dict_unigram_lm_laplace_sorted:
                    files.append(str(query_no) + " Q0 " + doc[0] + " " + str(rank) + " " + str(doc[1]) + " Exp\n")
                    rank += 1
                unigram_lm_laplace_output.writelines(files)

    unigram_lm_laplace_output.close()
    print datetime.now() - start