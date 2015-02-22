__author__ = 'Ameya'

CLIENT = 'localhost:9200'
INDEX_NAME = 'ameya-index'
DOCUMENT_TYPE = 'text-type'
ROOT_DIRECTORY = 'E:\\Dropbox\\NEU - Spring 2015\\Information Retrieval\\'
FILES_DIRECTORY = ROOT_DIRECTORY + 'AP_DATA\\ap89_collection\\'
STOPLIST_TEXT_FILE = ROOT_DIRECTORY + 'AP_DATA\\stoplist.txt'
QUERIES_TEXT_FILE = ROOT_DIRECTORY + 'AP_DATA\\query_desc.51-100.short.txt'
STEM_CLASSES_FILE = ROOT_DIRECTORY + 'AP_DATA\\stem-classes.lst'
CORPUS_STATISTICS = ROOT_DIRECTORY + 'HW1\\corpus-information\\docs_stats.txt'
CORPUS_DOCUMENTS_LIST = ROOT_DIRECTORY + 'HW1\\corpus-information\\docs_list.txt'
OUTPUT_DIRECTORY = ROOT_DIRECTORY + 'HW1\\output-results\\'
CUSTOM_STEMMER_FILE = ROOT_DIRECTORY + "HW1\\custom-stemmer.txt"

# tags for maintaining corpus statistics
TOTAL_DOCUMENTS_IDENTIFIER = 'TOTAL_DOCUMENTS '
TOTAL_DOCUMENT_LENGTH_IDENTIFIER = 'TOTAL_DOCUMENT_LENGTH '
AVERAGE_DOC_LENGTH_IDENTIFIER = 'AVERAGE_DOC_LENGTH '

# tags to identify different parts of document while parsing the documents in the corpus
DOCNO_BEGIN_TAG = '<DOCNO>'
TEXT_BEGIN_TAG = '<TEXT>'
TEXT_END_TAG = '</TEXT>'
DOC_END_TAG = '</DOC>'