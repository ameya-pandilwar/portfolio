__author__ = 'Ameya'

import crawler_utils
from elasticsearch import Elasticsearch

es = Elasticsearch()
es.indices.create(index=crawler_utils.INDEX_NAME,
                  body={
                      'settings': {
                          'index': {
                              'store': {
                                  'type': "default"
                              },
                              'number_of_shards': 1,
                              'number_of_replicas': 0
                          },
                          'analysis': {
                              "filter": {
                                  "my_filter": {
                                      "type": "shingle",
                                      "min_shingle_size": 2,
                                      "max_shingle_size": 2,
                                      "output_unigrams":  False
                                  }
                              },
                              'analyzer': {
                                  'def_english': {
                                      'type': 'english',
                                      'stopwords_path': "..\\AP_DATA\\stoplist.txt"
                                  },
                                  "my_analyzer": {
                                      "type": "custom",
                                      "tokenizer": "standard",
                                      "filter": [
                                          "lowercase",
                                          "my_filter"
                                      ]
                                  }
                              }
                          }
                      },
                      "mappings": {
                          "document": {
                              "properties": {
                                  "url": {
                                      "type": "string",
                                      "store": True
                                  },
                                  "head": {
                                      "type": "string"
                                  },
                                  "httpResponseHeader": {
                                      "type": "string"
                                  },
                                  "text": {
                                      "type": "string",
                                      "store": True,
                                      "index": "analyzed",
                                      "term_vector": "with_positions_offsets_payloads",
                                      "analyzer": "def_english",
                                      "fields": {
                                          "shingles": {
                                              "type": "string",
                                              "analyzer": "my_analyzer"
                                          }
                                      }
                                  },
                                  "outLinks": {
                                      "type": "string"
                                  },
                                  "inLinks": {
                                      "type": "string"
                                  },
                                  "rawHTML": {
                                      "type": "string"
                                  }
                              }
                          }
                      }
                  }
                  )

es.indices.refresh(index=crawler_utils.INDEX_NAME)
print es.cat.indices(index=crawler_utils.INDEX_NAME)