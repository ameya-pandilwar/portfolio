__author__ = 'Ameya'

ROOT_DIRECTORY = 'E:\\Dropbox\\NEU - Spring 2015\\Information Retrieval\\'
FILES_DIRECTORY = ROOT_DIRECTORY + 'AP_DATA\\ap89_collection\\'
STOPLIST_TEXT_FILE = ROOT_DIRECTORY + 'AP_DATA\\stoplist.txt'

# tags to identify different parts of document while parsing the documents in the corpus
DOCNO_BEGIN_TAG = '<DOCNO>'
TEXT_BEGIN_TAG = '<TEXT>'
TEXT_END_TAG = '</TEXT>'
DOC_END_TAG = '</DOC>'

# regular expression pattern
REGULAR_EXPRESSION_PATTERN = '\w+(\.?\w+)*'