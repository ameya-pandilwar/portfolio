__author__ = 'Ameya'

import os
from datetime import datetime
from elasticsearch import Elasticsearch

if __name__ == '__main__':

    index_name = "assgn_03"
    es = Elasticsearch(timeout=30)
    folderpaths = ["E:\\IR-HW3\\Web Crawler Documents"]
    inlinks1_path = "E:\\IR-HW3\\outfile"

    inlinks_dic = {}

    start = datetime.now()

    with open (inlinks1_path,"r") as inlinks1:
        for line in iter(inlinks1):
            key=""
            val=[]
            if line.__contains__(" "):
                key= line.split(" ")[0]
                val = list(line.split(" ")[1:])
            else:
                key=line
                val= []
            inlinks_dic[key] = val
    print "loaded all inlinks"
    end = datetime.now()
    print end - start

    text=""
    http_header=""
    title = ""
    raw = ""
    out_links = ""
    http_flag = 0
    text_flag = 0
    raw_flag = 0
    link_flag = 0
    in_links = []
    iter_count = 0;
    found = {}
    for folderpath in folderpaths:
        for f in os.listdir(folderpath):
            iter_count += 1
            print "inserting doc : " + str(iter_count)
            filename = folderpath + "\\" + f

            with open(filename) as cur_file:

                for line in iter(cur_file):

                    if line.__contains__("<DOCNO>"):
                        url = line.split("<DOCNO>")[1]
                        url = url.split("<")[0]

                    if line.__contains__("<HTTP>"):
                        http_flag = 1

                    if http_flag == 1 and not line.__contains__("<HTTP>") and not line.__contains__("</HTTP>"):
                        http_header += line

                    if line.__contains__("</HTTP>"):
                        http_flag = 0

                    if line.__contains__("<HEAD>") and line.__contains__("</HEAD>"):
                        title = line.split(">")[1]
                        title = title.split("<")[0]

                    if line.__contains__("<TEXT>"):
                        text_flag = 1

                    if text_flag == 1 and not line.__contains__("<TEXT>") and not line.__contains__("</TEXT>"):
                        text += line

                    if line.__contains__("</TEXT>"):
                        text_flag = 0

                    if line.__contains__("<RAW>"):
                        raw_flag = 1

                    if raw_flag == 1 and not line.__contains__("<RAW>") and not line.__contains__("<\RAW>"):
                        raw += line

                    if line.__contains__("<\RAW>"):
                        raw_flag = 0

                    if line.__contains__("<LINKS>"):
                        link_flag = 1

                    if link_flag == 1 and not line.__contains__("<LINKS>") and not line.__contains__("</LINKS>"):
                        out_links += line

                    if line.__contains__("</LINKS>"):
                        link_flag = 0
                        out_links = out_links.split("\n")

                    if(line.__contains__("</DOC>")):
                        if inlinks_dic.has_key(url) and not found.has_key(url):
                            in_links = inlinks_dic[url]
                            found[url] = 1

                        es.index(index=index_name, doc_type="document", id=url, body={"url": url, "head": title, "htttpResponseHeader": http_header, "text": text, "outLinks" : out_links, "inLinks": in_links, "rawHTML": raw})

                        text = ""
                        http_header = ""
                        title = ""
                        raw = ""
                        out_links = ""
                        in_links = []



    end = datetime.now()
    print end - start