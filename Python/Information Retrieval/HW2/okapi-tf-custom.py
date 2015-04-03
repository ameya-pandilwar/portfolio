__author__ = 'Ameya'

import helper
import constants
from datetime import datetime

if __name__ == '__main__':
    start = datetime.now()

    helper.load_corpus_term_frequency_map()
    helper.load_documents_id_length_map()
    helper.load_corpus_statistics()
    helper.load_catalog_file()

    okapi_tf_output = open(constants.OUTPUT_RESULTS_PATH + 'okapi-tf_results.txt', constants.WRITE)

    with open(constants.QUERIES_TEXT_FILE) as queries:
        for line in iter(queries):
            if line.__contains__("."):
                query = helper.get_query(line.lower())
                all_terms = helper.get_all_terms(query, start)
                terms = helper.query_custom_stem(all_terms)
                okapi_tf_dictionary = {}

                for stem in terms:
                    if stem in constants.term_id_map:
                        term_id = constants.term_id_map[stem]
                        start_fetch = constants.term_offset_map[term_id][0]
                        end_fetch = constants.term_offset_map[term_id][0]

                        all_docs_for_term = helper.fetch_doc_freq(start_fetch, end_fetch, 0)
                        for doc in all_docs_for_term.keys():
                            doc_tf = 0
                            doc_id = str(constants.doc_id_map[doc])
                            tf = all_docs_for_term[doc]
                            doc_length = int(constants.doc_length_map[doc_id])
                            denominator = float(tf + 0.5 + float(1.5 * (doc_length / float(constants.avg_doc_len))))
                            doc_tf = float(tf / denominator)

                            if doc_tf > 0.0000:
                                if doc_id in okapi_tf_dictionary:
                                    okapi_tf_dictionary[doc_id] += doc_tf
                                else:
                                    okapi_tf_dictionary[doc_id] = doc_tf

                helper.write_result_to_file(okapi_tf_dictionary, line.split(".   ")[0], okapi_tf_output)

    okapi_tf_output.close()
    print "completed in - " + str(datetime.now() - start)