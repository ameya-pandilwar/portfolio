__author__ = 'Ameya'

import constants
from os import listdir
from datetime import datetime
from elasticsearch import helpers
from elasticsearch import Elasticsearch

put_settings_body = {
    "settings": {
        "number_of_shards": 1,
        "number_of_replicas": 0,
        "analysis": {
            "analyzer": {
                "ameya_analyzer": {
                    "type": "english",
                    "stopwords_path": "../../AP89_DATA/AP_DATA/stoplist.txt"
                }
            }
        }
    }
}

put_mapping_body = {
    "text-type": {
        "properties": {
            "text": {
                "type": "string",
                "store": True,
                "index": "analyzed",
                "term_vector": "with_positions_offsets_payloads",
                "analyzer": "ameya_analyzer"
            }
        }
    }
}


def map_create_index():
    es.indices.create(index=constants.INDEX_NAME, body=put_settings_body, ignore=400)
    es.indices.put_mapping(index=constants.INDEX_NAME, doc_type=constants.DOCUMENT_TYPE, body=put_mapping_body)


def bulk_index_files_in_directory(file_path):

    doc_no = ''
    doc_text = ''
    index_files = []
    append_text = False

    for single_file in listdir(file_path):
        with open(file_path + single_file) as current_file:
            for line in iter(current_file):
                if line.__contains__(constants.DOCNO_BEGIN_TAG):
                    doc_no = line.split(" ")[1]

                if line.__contains__(constants.TEXT_BEGIN_TAG) or append_text:
                    append_text = True
                    if not line.__contains__(constants.TEXT_END_TAG):
                        if not line.__contains__(constants.TEXT_BEGIN_TAG):
                            doc_text += line
                    else:
                        append_text = False

                if line.__contains__(constants.DOC_END_TAG):
                    index_file = {
                        "_index": constants.INDEX_NAME,
                        "_type": constants.DOCUMENT_TYPE,
                        "_id": doc_no,
                        "_source": {
                            "text": doc_text
                        }
                    }
                    index_files.append(index_file)
                    doc_text = ''

    helpers.bulk(es, index_files)


if __name__ == '__main__':
    es = Elasticsearch(constants.CLIENT, timeout=600, max_retries=10, revival_delay=0)
    start = datetime.now()

    if not es.indices.exists(constants.INDEX_NAME):
        map_create_index()
        bulk_index_files_in_directory(constants.FILES_DIRECTORY)
    else:
        print "The index already exists - " + constants.INDEX_NAME

    time_taken = datetime.now() - start
    print "The time taken to index is - " + str(time_taken)