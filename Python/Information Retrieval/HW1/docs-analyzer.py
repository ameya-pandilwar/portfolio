__author__ = 'Ameya'

import os
import constants
from datetime import datetime
from elasticsearch import Elasticsearch

total_docs_length = 0


def prepare_documents_list(filepath, documents):
    if not os.path.exists(filepath):
        output_file = open(filepath, 'w')
        global total_docs_length
        lines = []
        for document in documents:
            doc_length = 0
            term_vec = es.termvector(index=constants.INDEX_NAME,
                                     doc_type=constants.DOCUMENT_TYPE,
                                     id=document,
                                     term_statistics='true')
            if term_vec['term_vectors']:
                for stem in term_vec['term_vectors']['text']['terms']:
                    doc_length += term_vec['term_vectors']['text']['terms'][stem]['term_freq']
            lines.append(document + ' ' + str(doc_length) + '\n')
            total_docs_length += doc_length
            if total_docs_length % 5000 == 0:
                output_file.writelines(lines)
                lines = []
        output_file.writelines(lines)
        output_file.close()


if __name__ == '__main__':
    es = Elasticsearch(constants.CLIENT, timeout=600, max_retries=10, revival_delay=0)
    start = datetime.now()

    no_of_docs = es.cat.indices(index=constants.INDEX_NAME).split()[5]
    results = es.search(index=constants.INDEX_NAME,
                        doc_type=constants.DOCUMENT_TYPE,
                        body={"query": {"match_all": {}}},
                        size=no_of_docs)
    docs = [doc['_id'] for doc in results['hits']['hits']]

    prepare_documents_list(constants.CORPUS_DOCUMENTS_LIST, docs)

    if not os.path.exists(constants.CORPUS_STATISTICS):
        docs_stats = open(constants.CORPUS_STATISTICS, 'w')
        line = constants.TOTAL_DOCUMENTS_IDENTIFIER + str(no_of_docs) + "\n"
        docs_stats.write(line)
        line = constants.TOTAL_DOCUMENT_LENGTH_IDENTIFIER + str(total_docs_length) + "\n"
        docs_stats.write(line)
        line = constants.AVERAGE_DOC_LENGTH_IDENTIFIER + str(total_docs_length/int(no_of_docs))
        docs_stats.write(line)
        docs_stats.close()

    print datetime.now() - start