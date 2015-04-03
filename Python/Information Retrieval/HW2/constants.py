__author__ = 'Ameya'

ROOT_DIRECTORY = 'E:\\Dropbox\\NEU - Spring 2015\\Information Retrieval\\'
FILES_DIRECTORY = ROOT_DIRECTORY + 'AP_DATA\\ap89_collection\\'
STOPLIST_TEXT_FILE = ROOT_DIRECTORY + 'AP_DATA\\stoplist.txt'
QUERIES_TEXT_FILE = ROOT_DIRECTORY + 'AP_DATA\\query_desc.51-100.short.txt'
CUSTOM_STEMMER_FILE = ROOT_DIRECTORY + 'HW2\\custom-stemmer.txt'
CORPUS_STATISTICS = ROOT_DIRECTORY + 'HW2\\docs_stats'
TEMP_CATALOG_FILE = ROOT_DIRECTORY + 'HW2\\_temp_catalog_'
TEMP_INDEX_FILE = ROOT_DIRECTORY + 'HW2\\_temp_index_'
CATALOG_FILE = ROOT_DIRECTORY + 'HW2\\catalog'
INDEX_FILE = ROOT_DIRECTORY + 'HW2\\index'
FILES_PER_BATCH = 10
INITIAL_MINIMUM_VALUE = 9999

TERM_MAP_FILE = ROOT_DIRECTORY + 'HW2\\term_map'
DOC_MAP_FILE = ROOT_DIRECTORY + 'HW2\\doc_map'

# tags for maintaining corpus statistics
TOTAL_DOCUMENTS_IDENTIFIER = 'TOTAL_DOCUMENTS '
TOTAL_DOCUMENT_LENGTH_IDENTIFIER = 'TOTAL_DOCUMENT_LENGTH '
AVERAGE_DOC_LENGTH_IDENTIFIER = 'AVERAGE_DOC_LENGTH '

# tags to identify different parts of document while parsing the documents in the corpus
DOCNO_BEGIN_TAG = '<DOCNO>'
TEXT_BEGIN_TAG = '<TEXT>'
TEXT_END_TAG = '</TEXT>'
DOC_END_TAG = '</DOC>'

# regular expression pattern
REGULAR_EXPRESSION_PATTERN = '\w+(\.?\w+)*'

# file modes
READ = 'r'
WRITE = 'w'
READ_BINARY = 'rb'
WRITE_BINARY = 'wb'

# global
stems = {}
stops = {}
ctf_map = {}
doc_lengths = {}

# evaluating retrieval models
doc_id_map = {}
term_id_map = {}
doc_length_map = {}
term_offset_map = {}
corp_term_freq_map = {}

# corpus statistics variables
doc_count = 0
avg_doc_len = 0

# result file path
OUTPUT_RESULTS_PATH = ROOT_DIRECTORY + 'HW2\\output-results\\'