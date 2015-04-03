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

    okapi_bm25_output = open(constants.OUTPUT_RESULTS_PATH + 'okapi-bm25_results.txt', constants.WRITE)

    with open(constants.QUERIES_TEXT_FILE) as queries:
        for line in iter(queries):
            if line.__contains__("."):
                query = helper.get_query(line.lower())
                all_terms = helper.get_all_terms(query, start)
                terms = helper.query_custom_stem(all_terms)
                okapi_bm25_dictionary = {}

                tf_query = {}
                for stem in terms:
                    tf_query[stem] = tf_query[stem] + 1 if tf_query.__contains__(stem) else 1

                for stem in terms:
                    if stem in constants.term_id_map:
                        term_id = constants.term_id_map[stem]
                        start_fetch = constants.term_offset_map[term_id][0]
                        end_fetch = constants.term_offset_map[term_id][0]

                    result = helper.fetch_doc_freq(start_fetch, end_fetch, 0)
                    dfw = len(result)
                    tf_q = tf_query[stem]

                    for doc in result.keys():
                        doc_id = str(constants.doc_id_map[doc])
                        tf = result[doc]
                        doc_length = int(constants.doc_length_map[doc_id])

                        bm25 = float(log(float(float(constants.doc_count) + 0.5) / float(dfw + 0.5)))
                        bm25 = float(bm25 * float(float(tf * float(1 + 1.2)) / float(tf + float(1.2 * (float(1 - 0.75) + float(0.75 * float(doc_length / float(constants.avg_doc_len))))))))
                        bm25 = float(bm25 * (float(tf_q * (1 + 100)) / float(tf_q + 100)))

                        if bm25 > 0.0000:
                            if doc_id in okapi_bm25_dictionary:
                                okapi_bm25_dictionary[doc_id] += bm25
                            else:
                                okapi_bm25_dictionary[doc_id] = bm25

                helper.write_result_to_file(okapi_bm25_dictionary, line.split(".   ")[0], okapi_bm25_output)

    okapi_bm25_output.close()
    print "completed in - " + str(datetime.now() - start)