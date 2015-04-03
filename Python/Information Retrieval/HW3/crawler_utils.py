__author__ = 'Ameya'

from collections import defaultdict

frontier = {}
discovered = set()
inlinks_map = defaultdict(list)

seed1 = 'http://en.wikipedia.org/wiki/Costa_Concordia_disaster'
seed2 = 'http://www.telegraph.co.uk/news/worldnews/europe/italy/10312026/Costa-Concordia-recovery-timeline-of-cruise-ship-disaster.html'
seed3 = 'http://en.wikipedia.org/wiki/Costa_Concordia'

raw_html = ""
unicode_text = ""
doc_title = ""
outlinks = ""
http_header = ""
http_flag = False
text_flag = False
raw_flag = False
link_flag = False
inlinks = []
count = 0;
found = {}

INDEX_NAME = 'assgn_03'