__author__ = 'ameyapandilwar'

import re
import constants


def generate_tuples(doc_id, text):

    words = []

    term_list = []
    term_map = {}
    _id = 1
    for term in re.finditer(constants.REGULAR_EXPRESSION_PATTERN, text.lower()):
        term = term.group()
        if term not in term_list:
            term_list.append(term)
            term_map[_id] = term
            _id += 1
        words.append(term)

    tuples = ''
    n = 1
    for word in words:
        for _id, term in term_map.items():
            if term == word.lower():
                tuples += '(' + str(_id) + ', ' + str(doc_id) + ', ' + str(n) + '), '
        n += 1

    if tuples.endswith(', '):
        tuples = tuples[:-2]

    for _id, term in term_map.items():
        print str(_id) + ': ' + term

    return tuples


if __name__ == '__main__':
    print generate_tuples(20, 'The car was in the car wash.')