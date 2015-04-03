__author__ = 'Ameya'

import helper
import constants
from math import log
from datetime import datetime

if __name__ == '__main__':
    start = datetime.now()

    helper.load_corpus_term_frequency_map()
    helper.load_documents_id_length_map()
    helper.load_corpus_statistics()
    helper.load_catalog_file()

    okapi_out = open(constants.OUTPUT_RESULTS_PATH + 'unigram-lm-laplace_results.txt', constants.WRITE)

    with open(constants.QUERIES_TEXT_FILE) as queries:
        for line in iter(queries):
            if line.__contains__("."):
                query = helper.get_query(line.lower())
                all_terms = helper.get_all_terms(query, start)
                terms = helper.query_custom_stem(all_terms)
                dict_result = {}
                dict_term_freq = {}
                search_docs = []
                for stem in terms:
                    term_freq = {}
                    if stem in constants.term_id_map:
                        term_id = constants.term_id_map[stem]
                        start_fetch = constants.term_offset_map[term_id][0]
                        end_fetch = constants.term_offset_map[term_id][0]

                        all_docs_for_term = helper.fetch_doc_freq(start_fetch, end_fetch, 0)
                        for doc in all_docs_for_term.keys():
                            doc_id = str(constants.doc_id_map[doc])
                            tf = all_docs_for_term[doc]
                            if not search_docs.__contains__(doc_id):
                                search_docs.append(doc_id)
                            term_freq[doc_id] = tf

                    dict_term_freq[stem] = term_freq

                for search_doc in search_docs:
                    doc_score = 0
                    doc_length = int(constants.doc_length_map[search_doc])
                    for stem in terms:
                        tf = 0
                        term_freq = dict_term_freq[stem]
                        if search_doc in term_freq:
                            tf = term_freq[search_doc]

                        doc_score = float(doc_score + float(log(float((tf + 1) / float(doc_length + 20000)))))

                    dict_result[search_doc] = doc_score

                helper.write_result_to_file(dict_result, line.split(".   ")[0], okapi_out)

    okapi_out.close()
    print "completed in - " + str(datetime.now() - start)