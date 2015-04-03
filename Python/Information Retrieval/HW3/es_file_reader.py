__author__ = 'Ameya'

import os
import crawler_utils
import crawler_config
from datetime import datetime
from elasticsearch import Elasticsearch

if __name__ == '__main__':

    inlinks_map = {}
    es = Elasticsearch()
    path = crawler_config.WEB_CRAWLER_DIRECTORY

    start_time = datetime.now()

    with open(crawler_config.INLINKS_FILE, crawler_config.FILE_READ_MODE) as inlinks_file:
        for line in iter(inlinks_file):
            key = ""
            val = []
            if line.__contains__(" "):
                key = line.split(" ")[0]
                val = list(line.split(" ")[1:])
            else:
                key = line
                val = []
            inlinks_map[key] = val
    print "inlinks loaded successfully into memory in -- " + str(datetime.now() - start_time)

    for f in os.listdir(path):
        crawler_utils.count += 1
        filename = path + "\\" + f
        print "adding document # " + str(crawler_utils.count) + " into memory"

        with open(filename) as current_file:
            for line in iter(current_file):
                if line.__contains__("<DOCNO>"):
                    url = line.split("<DOCNO>")[1].split("<")[0]
                if line.__contains__("<HTTP>"):
                    crawler_utils.http_flag = True
                if crawler_utils.http_flag and not line.__contains__("<HTTP>") and not line.__contains__("</HTTP>"):
                    crawler_utils.http_header += line
                if line.__contains__("</HTTP>"):
                    crawler_utils.http_flag = False
                if line.__contains__("<HEAD>") and line.__contains__("</HEAD>"):
                    doc_title = line.split(">")[1].split("<")[0]
                if line.__contains__("<TEXT>"):
                    crawler_utils.text_flag = True
                if crawler_utils.text_flag and not line.__contains__("<TEXT>") and not line.__contains__("</TEXT>"):
                    crawler_utils.unicode_text += line
                if line.__contains__("</TEXT>"):
                    crawler_utils.text_flag = False
                if line.__contains__("<RAW>"):
                    crawler_utils.raw_flag = True
                if crawler_utils.raw_flag and not line.__contains__("<RAW>") and not line.__contains__("<\RAW>"):
                    crawler_utils.raw_html += line
                if line.__contains__("<\RAW>"):
                    crawler_utils.raw_flag = False
                if line.__contains__("<LINKS>"):
                    crawler_utils.link_flag = True
                if crawler_utils.link_flag and not line.__contains__("<LINKS>") and not line.__contains__("</LINKS>"):
                    crawler_utils.outlinks += line
                if line.__contains__("</LINKS>"):
                    crawler_utils.link_flag = False
                    crawler_utils.outlinks = crawler_utils.outlinks.split("\n")
                if line.__contains__("</DOC>"):
                    if url in inlinks_map:
                        crawler_utils.inlinks = crawler_utils.inlinks_map[url]

                    es.index(index="crawler",
                             doc_type="document",
                             id=url,
                             body={"url": crawler_utils.url,
                                   "head": crawler_utils.doc_title,
                                   "htttpResponseHeader": crawler_utils.http_header,
                                   "text": crawler_utils.unicode_text,
                                   "outLinks": crawler_utils.outlinks,
                                   "inLinks": crawler_utils.inlinks,
                                   "rawHTML": crawler_utils.raw_html
                                   }
                             )

                    crawler_utils.unicode_text = ""
                    crawler_utils.http_header = ""
                    crawler_utils.doc_title = ""
                    crawler_utils.raw_html = ""
                    crawler_utils.outlinks = ""
                    crawler_utils.inlinks = []

    print str(datetime.now() - start_time)