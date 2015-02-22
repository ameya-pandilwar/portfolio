__author__ = 'ameyapandilwar'

import constants
from tokenizer import generate_tuples
from os import listdir
from datetime import datetime


def store_tuples_as_file(file_path):
    doc_no = ''
    doc_text = ''
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
                    print generate_tuples(doc_no, doc_text)
                    doc_text = ''


if __name__ == '__main__':
    start = datetime.now()

    store_tuples_as_file(constants.FILES_DIRECTORY)

    time_taken = datetime.now() - start
    print "The time taken to index is - " + str(time_taken)