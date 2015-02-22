from collections import OrderedDict

__author__ = 'Ameya'

import os
import re
from datetime import datetime


def get_files_by_month():
    files =""
    files_by_month = {}
    months = ["01","02","03","04","05","06","07","08","09","10","11","12"]

    for f in os.listdir(filespath):
        files = files + " "+ str(f)
    #print files

    for month in months:
        strr = "ap89"+month+"\d?\d?"
        file_list = []
        patt = re.compile(strr)
        result = patt.finditer(files)
        for iter_term in result:
            file_list.append(iter_term.group())
        files_by_month[month] = file_list

    return files_by_month

def stem_words(text):
    raw_pos = 0
    all_terms = {}
    all_text_tokens = []
    patt = re.compile("\w+(\.?\w+)*")
    result = patt.finditer(text)


    for iter_term in result:
        all_text_tokens.append(iter_term.group())

    for term in all_text_tokens:
        raw_pos = raw_pos + 1

        if stops.has_key(term):
            continue
        else:
            all_terms[raw_pos]=term

    term_match = {}
    for pos in all_terms.keys():
        term_match[pos] = 1

    terms = {}

    for term_pos in all_terms.keys():
        term = str(all_terms[term_pos])
        if stems.has_key(term) and term_match[term_pos] == 1:
            terms[term_pos] = str(stems[term])
            term_match[term_pos] = 0

    for term_pos in term_match.keys():
        if term_match[term_pos] == 1:
            terms[term_pos] = all_terms[term_pos]

    return terms


def parse_file(filename):
    docno = ""
    flag = "stop"
    text = ""

    ret_val = {}

    with open(filename) as curr_file:
        for line in iter(curr_file):
            if line.__contains__("<DOCNO>"):
                docno = line.split(" ")[1]

            if (line.__contains__("<TEXT>") or flag.__eq__("start")):
                flag = "start"

                if (not line.__contains__("</TEXT>")):
                    if (not line.__contains__("<TEXT>")):
                        text += line
                else:
                    flag = "stop"

            if (line.__contains__("</DOC>")):
                text = text.lower()
                stemed_text = stem_words(text)
                ret_val[docno] = stemed_text
                text = ""

    return ret_val


def create_index(terms, pass_no):
    aux_index_filepath = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\aux_pass" + str(pass_no)
    index_filepath = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\index_pass" + str(pass_no)
    aux_index = open(aux_index_filepath, "wb")
    index_file = open(index_filepath, "wb")

    for key in terms.keys():
        start_index = index_file.tell()
        db = terms[key]
        _str = str(key) + " "
        for dbl in db.keys():
            _str = _str + ":" + str(dbl) + "{" + str(terms[key][dbl].__len__())
            for pos in terms[key][dbl]:
                _str = _str + "," + str(pos)
            _str = _str + "}"
        _str = _str + "\n"

        index_file.write(_str)
        end_index = index_file.tell()

        aux_index.write(str(key)+" "+str(start_index)+ " "+ str(end_index)+"\n")

    index_file.close()
    aux_index.close()

def get_dblock_with_path(find_term, index_filepath, start_pos, end_pos):
    index_file = open(index_filepath, "rb")

    index_file.seek(long(start_pos),0)
    db = index_file.read(long(end_pos) - long(start_pos) - 1)
    return  db.split()[1]


def get_dblock(term_map, find_term_str,pass_no):
    aux_index_filepath = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\aux_pass" + str(pass_no)
    index_filepath = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\index_pass" + str(pass_no)

    with open("E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\term_map", "rb") as term_map:
        for line in iter(term_map):
            if line.split()[0] == find_term_str:
                find_term = int(line.split()[1])

    term_offset ={}

    with open(aux_index_filepath, "rb") as aux_index :
        for line in iter(aux_index):
            term = int(line.split()[0])
            start_off = int(line.split()[1])
            end_off = int(line.split()[2])
            off=[start_off,end_off]
            term_offset[term] = off

    index_file = open(index_filepath, "rb")
    if term_offset.has_key(find_term):
        index_file.seek(term_offset[find_term][0],0)
        db = index_file.read(int(term_offset[find_term][1] - term_offset[find_term][0] - 1))
        #print("start : " +str(term_offset[find_term][0]) +" End : "+ str(term_offset[find_term][1])+"\n")
        print(db)

    index_file.close()

def write_term_map(sorted_terms):
    file_term_map = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\term_map_new"
    term_map = open(file_term_map, "wb")

    for key in sorted_terms.keys():
        _line = str(key) + " " + str(sorted_terms[key]) + "\n"
        term_map.write(_line)

    term_map.close()


def merge_indices(iteration_no):
    path_aux_index_1=""
    path_aux_index_2=""
    path_index_1= ""
    path_index_2 = ""

    aux_1 = {}
    aux_2 = {}
    if iteration_no >  2:
        if os.path.isfile("E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\aux_index_intermediate"):
            os.remove("E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\aux_index_intermediate")
        if os.path.isfile("E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\index_intermediate"):
            os.remove("E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\index_intermediate")
        os.rename("E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\aux_index", "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\aux_index_intermediate")
        os.rename("E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\index", "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\index_intermediate")
        path_aux_index_1 = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\aux_index_intermediate"
        path_index_1 = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\index_intermediate"
        path_aux_index_2 = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\aux_pass" + str(iteration_no)
        path_index_2 = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\index_pass" + str(iteration_no)

    if iteration_no == 2:
        path_aux_index_1 = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\aux_pass" + str(iteration_no - 1)
        path_index_1 = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\index_pass" + str(iteration_no - 1)
        path_aux_index_2 = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\aux_pass" + str(iteration_no)
        path_index_2 = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\index_pass" + str(iteration_no)

    path_out_aux_index = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\aux_index"
    path_out_index = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ASGN2\\index"

    out_aux_index  = open(path_out_aux_index,"wb")
    out_index = open(path_out_index,"wb")

    possible_terms = {}

    with open (path_aux_index_1,"rb") as first_aux:
        for _line in iter(first_aux):
            trip = _line.rstrip().split()
            aux_1[trip[0]] = [trip[1],trip[2]]
            possible_terms[int(trip[0])] = 0

    with open (path_aux_index_2,"rb") as sec_aux:
        for _line in iter(sec_aux):
            trip = _line.rstrip().split()
            aux_2[trip[0]] = [trip[1],trip[2]]
            possible_terms[int(trip[0])] = 0

    possible_terms_list = sorted(possible_terms.keys())

    for key in possible_terms_list:
        key = str(key)
        _write_line =""
        if aux_1.has_key(str(key)):
            db_1 = get_dblock_with_path(key,path_index_1,aux_1[key][0],aux_1[key][1]).split("\n")[0]
            if aux_2.has_key(str(key)):
                db_2 = get_dblock_with_path(key,path_index_2,aux_2[key][0],aux_2[key][1]).split("\n")[0]
                _write_line = str(key)+ " "+ db_1 + db_2 + "\n"
            else:
                _write_line = str(key)+ " "+ db_1 + "\n"
        else:
            if aux_2.has_key(str(key)):
                db_2 = get_dblock_with_path(key,path_index_2,aux_2[key][0],aux_2[key][1]).split("\n")[0]
                _write_line = str(key)+ " " + db_2 + "\n"

        start_off = out_index.tell()
        out_index.write(_write_line)
        end_off = out_index.tell()
        out_aux_index.write(str(key) + " " + str(start_off) +" "+ str(end_off)+ "\n")

    out_aux_index.close()
    out_index.close()

    os.remove(path_aux_index_1)
    os.remove(path_aux_index_2)
    os.remove(path_index_1)
    os.remove(path_index_2)


if __name__ == '__main__':

    term_mappings = {}
    term_mappings_sorted = {}
    term_id = 0
    max_id = 0
    iteration_no = 0
    stems={}

    start = datetime.now()

    filespath = "E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\ap89_collection"

    month_files = get_files_by_month()

    with open("C:\Users\Swapnil\Desktop\split_1.txt") as dictonary:
        for line in dictonary:
            line = line.lower().rstrip()
            if line.__contains__(" "):
                line_term = line.split(" ")[0]
                stems[line_term] = line.split(" ")[1]

    stops = {}

    with open("E:\\!SEM II\\IR\\AP89_DATA\\AP_DATA\\stoplist.txt") as stop_list:
            for line in iter(stop_list):
                stops[line.lower().rstrip()] = 1


    months = ["01","02","03","04","05","06","07","08","09","10","11","12"]
    #months = ["01","02"]

    for month in months:
        terms = {}
        iteration_no = iteration_no + 1
        for f in month_files[month]:
            filename = filespath + "\\" + f
            #print filename

            parsed_docs = parse_file(filename)

            for doc_id in parsed_docs.keys():
                term_pos_dict = parsed_docs[doc_id]

                for term_pos in term_pos_dict.keys():
                    term  = term_pos_dict[term_pos]
                    if term_mappings.has_key(term):
                        term_id = term_mappings[term]
                    else:

                        max_id = max_id + 1
                        term_id = max_id
                        term_mappings[term] = term_id

                    if terms.has_key(term_id):
                        dblocks = terms[term_id]
                        if dblocks.has_key(doc_id):
                            dblocks[doc_id].append(term_pos)
                        else:
                            terms[term_id][doc_id] = [term_pos]
                    else:
                        dblock = {}
                        dblock[doc_id] = [term_pos]
                        terms[term_id] = dblock

        create_index(terms, iteration_no)
        print  "time for index creation "+ str(iteration_no) +" " + str(datetime.now() - start)

        if iteration_no > 1:
           merge_indices(iteration_no)
           print  "time for index merge "+ str(iteration_no) +" "  + str(datetime.now() - start)


    term_mappings_sorted = OrderedDict(sorted(term_mappings.items(), key=lambda x:x[1], reverse=False))
    write_term_map(term_mappings_sorted)