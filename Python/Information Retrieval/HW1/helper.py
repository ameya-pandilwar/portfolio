__author__ = 'Ameya'

import constants
from datetime import datetime


def get_term_freq_all(es, query, count, min_score):
    results = es.search(
        index=constants.INDEX_NAME,
        doc_type=constants.DOCUMENT_TYPE,
        body=(
            {
                "query": {
                    "function_score": {
                        "query": {
                            "match": {
                                "text": query
                            }
                        },
                        "functions": [
                            {
                                "script_score": {
                                    "script_id": "getTF",
                                    "lang": "groovy",
                                    "params": {
                                        "term": query,
                                        "field": "text",
                                        }
                                }
                            }
                        ],
                        "boost_mode": "replace"
                    }
                },
                "size": count,
                "fields": ["_id"],
                "min_score": min_score
            }
        )
    )
    return results


def get_all_terms(query, start):
    all_terms = []
    ignore_terms = ["document", "discuss", "report", "include", "describe", "identify", "predict", "cite"]

    for term in query.split(" "):
        ignore_term = False

        with open(constants.STOPLIST_TEXT_FILE) as stop_list:
            for line in iter(stop_list):
                if line.lower().__contains__(term) or ignore_terms.__contains__(term):
                    ignore_term = True
                    break
        if not ignore_term:
            all_terms.append(term)

    print str(datetime.now() - start) + " parsing query terms - " + str(all_terms)
    return all_terms


def get_query(line):
    return line.split(".   ")[1].replace("-", " ").replace(",", "").replace("(", "").replace(")", "")


def get_doc_stats(req, start):
    res = 0
    with open(constants.CORPUS_STATISTICS) as doc_stats:
        for line in iter(doc_stats):
            if line.__contains__(req):
                res = float(line.split()[1])
    print str(datetime.now() - start) + " retrieved : " + req
    return res


def load_length_in_dic(start):
    dic = {}
    with open(constants.CORPUS_DOCUMENTS_LIST) as doc_list_length:
        for line in iter(doc_list_length):
            dic[line.split()[0]] = line.split()[1]
    print str(datetime.now() - start) + " loaded dictionary containing documents length"
    return dic


def query_custom_stem(all_terms):
    term_match = {}
    for term in all_terms:
        term_match[term] = 1

    terms = []
    with open(constants.CUSTOM_STEMMER_FILE) as dictionary:
        for line in dictionary:
            line = line.lower()
            if line.__contains__(" "):
                line_term = line.split(" ")[0]
                for term in all_terms:
                    term = str(term)
                    if line_term == term and term_match[term] == 1:
                        terms.append(line.split(" ")[1].split("\n")[0])
                        term_match[term] = 0
                        break

    for term in term_match:
        if term_match[term] == 1:
            terms.append(term)

    return terms