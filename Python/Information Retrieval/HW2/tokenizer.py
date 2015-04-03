__author__ = 'Ameya'

import os
import helper
import constants
from datetime import datetime


def create_index(all_terms, index_no):
    catalog_file = open(constants.TEMP_CATALOG_FILE + index_no, constants.WRITE_BINARY)
    index_file = open(constants.TEMP_INDEX_FILE + index_no, constants.WRITE_BINARY)

    for key in all_terms.keys():
        start_index = index_file.tell()
        _str = str(key) + ' '

        for dbl in sorted(all_terms[key].keys()):
            no_of_terms = len(all_terms[key][dbl])
            _str = _str + ':' + str(dbl) + '{' + str(no_of_terms)
            constants.ctf_map[key] = constants.ctf_map[key] + no_of_terms if key in constants.ctf_map else no_of_terms

            for pos in all_terms[key][dbl]:
                _str = _str + ',' + str(pos)

        index_file.write(_str + '\n')
        catalog_file.write(str(key) + ' ' + str(start_index) + ' ' + str(index_file.tell()) + '\n')

    index_file.close()
    catalog_file.close()


def retrieve_dblock(index_filepath, start_pos, end_pos):
    index_file = open(index_filepath, constants.READ_BINARY)
    index_file.seek(long(start_pos), 0)
    db = index_file.read(long(end_pos) - long(start_pos) - 1)
    return db.split()[1]


def merge_indices(iteration):
    _catalog_one = {}
    _catalog_two = {}

    _catalog_one_path = constants.TEMP_CATALOG_FILE + str(iteration - 1)
    _index_one_path = constants.TEMP_INDEX_FILE + str(iteration - 1)
    _catalog_two_path = constants.TEMP_CATALOG_FILE + str(iteration)
    _index_two_path = constants.TEMP_INDEX_FILE + str(iteration)

    _catalog_out = open(constants.CATALOG_FILE, constants.WRITE_BINARY)
    _index_out = open(constants.INDEX_FILE, constants.WRITE_BINARY)

    possible_terms = {}

    with open(_catalog_one_path, constants.READ_BINARY) as first_aux:
        for _line in iter(first_aux):
            trip = _line.rstrip().split()
            _catalog_one[trip[0]] = [trip[1], trip[2]]
            possible_terms[int(trip[0])] = 0

    with open(_catalog_two_path, constants.READ_BINARY) as sec_aux:
        for _line in iter(sec_aux):
            trip = _line.rstrip().split()
            _catalog_two[trip[0]] = [trip[1], trip[2]]
            possible_terms[int(trip[0])] = 0

    possible_terms_list = sorted(possible_terms.keys())

    for key in possible_terms_list:
        key = str(key)
        _write_line = ''
        if key in _catalog_one:
            db_1 = retrieve_dblock(_index_one_path, _catalog_one[key][0], _catalog_one[key][1]).split('\n')[0]
            if key in _catalog_two:
                db_2 = retrieve_dblock(_index_two_path, _catalog_two[key][0], _catalog_two[key][1]).split('\n')[0]
                _write_line = key + ' ' + db_1 + db_2 + '\n'
            else:
                _write_line = key + ' ' + db_1 + '\n'
        else:
            if key in _catalog_two:
                db_2 = retrieve_dblock(_index_two_path, _catalog_two[key][0], _catalog_two[key][1]).split('\n')[0]
                _write_line = key + ' ' + db_2 + '\n'

        begin_offset = _index_out.tell()
        _index_out.write(_write_line)
        end_offset = _index_out.tell()
        _catalog_out.write(key + ' ' + str(begin_offset) + ' ' + str(end_offset) + '\n')

    _catalog_out.close()
    _index_out.close()

    os.remove(_catalog_one_path)
    os.remove(_index_one_path)
    os.remove(_catalog_two_path)
    os.remove(_index_two_path)

    os.rename(constants.CATALOG_FILE, constants.TEMP_CATALOG_FILE + str(iteration))
    os.rename(constants.INDEX_FILE, constants.TEMP_INDEX_FILE + str(iteration))


if __name__ == '__main__':

    terms = {}
    doc_mappings = {}
    term_mappings = {}
    doc_map_id = 0
    term_id = 0
    max_id = 0
    iteration_no = 0

    start = datetime.now()

    with open(constants.CUSTOM_STEMMER_FILE) as dictionary:
        for line in dictionary:
            line = line.lower().rstrip()
            if line.__contains__(' '):
                line_term = line.split(' ')[0]
                constants.stems[line_term] = line.split(' ')[1]

    with open(constants.STOPLIST_TEXT_FILE) as stop_list:
        for line in iter(stop_list):
            constants.stops[line.lower().rstrip()] = 1

    file_list = os.listdir(constants.FILES_DIRECTORY)
    no_of_files = len(file_list)

    for index in range(no_of_files):
        parsed_docs = helper.parse_file(constants.FILES_DIRECTORY + file_list[index], True, True)

        for doc_id in parsed_docs.keys():
            if len(parsed_docs[doc_id]) > 0:
                doc_map_id += 1
                doc_mappings[doc_id] = doc_map_id
                term_pos_dict = parsed_docs[doc_id]

                for term_pos in term_pos_dict.keys():
                    term = term_pos_dict[term_pos]
                    if term in term_mappings:
                        term_id = term_mappings[term]
                    else:
                        max_id += 1
                        term_id = max_id
                        term_mappings[term] = term_id

                    if term_id in terms:
                        dblocks = terms[term_id]
                        if doc_map_id in dblocks:
                            dblocks[doc_map_id].append(term_pos)
                        else:
                            terms[term_id][doc_map_id] = [term_pos]
                    else:
                        terms[term_id] = {doc_map_id: [term_pos]}

        if index % constants.FILES_PER_BATCH == 0 or index == no_of_files - 1:
            iteration_no += 1
            create_index(terms, str(iteration_no))
            terms = {}
            print 'Index #' + str(iteration_no) + ' created at ' + str(datetime.now() - start)

            if iteration_no > 1:
                merge_indices(iteration_no)
                print 'Index #' + str(iteration_no) + ' merged at ' + str(datetime.now() - start)

    os.rename(constants.TEMP_CATALOG_FILE + str(iteration_no), constants.CATALOG_FILE)
    os.rename(constants.TEMP_INDEX_FILE + str(iteration_no), constants.INDEX_FILE)

    helper.write_dict_into_file(constants.TERM_MAP_FILE, term_mappings, 0)
    helper.write_dict_into_file(constants.DOC_MAP_FILE, doc_mappings, 1)