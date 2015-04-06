__author__ = 'Ameya'

import time
import pickle
import urllib2
from bs4 import BeautifulSoup
from urlparse import urljoin
import robotparser
from collections import defaultdict
from datetime import datetime


def canonicalize(link_str, url):
    if link_str.__contains__('#'):
        link_str = link_str.split('#')[0]
    if link_str.startswith('http:'):
        link_str = link_str.rstrip(':80')
    elif link_str.startswith('https:'):
        link_str = link_str.rstrip(':443')
    if link_str.startswith('..'):
        link_str = urljoin(url, link_str)
    if link_str.startswith('//'):
        link_str = link_str.split('//')[1]
    elif link_str.startswith('/'):
        link_str = urljoin(url, link_str)

    if link_str.startswith('www.'):
        link_str = 'http://' + link_str
    elif not link_str.__contains__('http://') and not link_str.__contains__('https://') and link_str != "":
        link_str = 'http://' + link_str

    if link_str != "":
        if inlink_dict.has_key(link_str):
            inlink_dict[link_str].append(url)
        else:
            inlink_dict[link_str]

        if link_str not in discovered:
            if frontier.has_key(link_str):
                frontier[link_str] += 1
            else:
                frontier[link_str] = 0
                priority[link_str] = 0

    return link_str


def seed_cannon(link_str):
    if link_str.__contains__('#'):
        link_str = link_str.split('#')[0]
    if link_str.startswith('http:'):
        link_str = link_str.rstrip(':80')
    elif link_str.startswith('https:'):
        link_str = link_str.rstrip(':443')
    if link_str.startswith('..'):
        link_str = urljoin(url, link_str)
    if link_str.startswith('//'):
        link_str = link_str.split('//')[1]
    elif link_str.startswith('/'):
        link_str = urljoin(url, link_str)

    if link_str.startswith('www.'):
        link_str = 'http://' + link_str
    elif not link_str.__contains__('http://') and not link_str.__contains__('https://') and link_str != "":
        link_str = 'http://' + link_str

    return link_str


def clean_header(http_header):
    return str(http_header).encode('utf-8')


def clean_html(soup):
    for script in soup(["script", "style"]):
        script.extract()

    text = soup.get_text()
    lines = (line.strip() for line in text.splitlines())
    chunks = (phrase.strip() for line in lines for phrase in line.split("  "))
    text = '\n'.join(chunk for chunk in chunks if chunk)

    return text.encode('utf-8')


def useful_url(url_link):
    if url_link.endswith('.JPG') or url_link.endswith('.jpg') or url_link.endswith('.svg'):
        return False
    if url_link.__contains__('action=edit'):
        return False
    if url_link.__contains__('Wikipedia:') or url_link.__contains__('File:') or url_link.__contains__(':'):
        return False
    if url_link.__contains__('wikipedia.org') and not url_link.__contains__('en'):
        return False
    if url_link.endswith('.pdf'):
        return False
    if url_link.__contains__('facebook') or url_link.__contains__('twitter') or url_link.__contains__('linkedin') or url_link.__contains__('plus.google') or url_link.__contains__('amazon') :
        return False
    return True


def start_crawl_single_url(url):
    start = datetime.now()
    f_no = 0

    while len(frontier) > 0:
        try:
            if url in frontier and url in priority:
                frontier.pop(url)
                priority.pop(url)
                discovered.add(url)
            else:
                max_value = max(frontier.itervalues())
                max_keys = [k for k in frontier if frontier[k] == max_value]

                base_priority = -1

                for max1 in max_keys:
                    if base_priority < priority[max1]:
                        base_priority = priority[max1]
                        url = max1

                for url in frontier:
                    priority[url] += 1

            rp = robotparser.RobotFileParser()
            robot_url = urljoin(url, '/robots.txt')
            rp.set_url(robot_url)
            rp.read()
            flag = rp.can_fetch("*", url)

            print (str(flag) + " " + url)


            if flag:
                response = urllib2.urlopen(url)
                print ("response")
                html = response.read()
                print ("read")
                soup = BeautifulSoup(html)
                print ("soup")
                http_headers = response.info()
                print ("headers")

                unique_urls = set()
                for link in soup.find_all('a', href=True):
                    link_str = link.get('href').encode('utf-8')
                    if not useful_url(link_str):
                        continue
                    if link_str not in unique_urls:
                        l = canonicalize(link_str, url)
                        unique_urls.add(str(l))

                if soup.title:
                    title = soup.title.string.encode('utf-8')
                else:
                    title = "Default Title"

                f_no += 1
                with open("E:\\IR-HW3\\Web Crawler Documents\\" + str(f_no), "wb") as cleaned_text:
                    cleaned_text.write("<DOC>\n")
                    cleaned_text.write("<DOCNO>" + url + "</DOCNO>\n")
                    cleaned_text.write("<HTTP>\n" + clean_header(http_headers) + "</HTTP>\n")
                    cleaned_text.write("<HEAD>" + title + "</HEAD>\n")
                    cleaned_text.write("<TEXT>\n" + clean_html(soup) + "\n</TEXT>\n")
                    cleaned_text.write("<RAW>\n" + soup.encode('utf-8') + "\n<\RAW>\n")
                    cleaned_text.write("<LINKS>")
                    for l in unique_urls:
                        cleaned_text.write(l + "\n")
                    cleaned_text.write("</LINKS>\n</DOC>")

                    time.sleep(1)

                    try:
                        max_value = max(frontier.itervalues())
                        max_keys = [k for k in frontier if frontier[k] == max_value]

                        base_priority = -1
                        crawl_url = ""
                        for max1 in max_keys:
                            if base_priority < priority[max1]:
                                base_priority = priority[max1]
                                crawl_url = max1

                        for url in frontier:
                            priority[url] += 1

                        if f_no < 12001:
                            print f_no
                            print (datetime.now() - start)
                            time.sleep(1)
                            url = crawl_url
                        else:
                            print ("END!!!! Finally @_@")
                            break

                    except Exception as e:
                        print ("ERROR:" + url)
                        print (e)
            else:
                print ("Not allowed to crawl " + url)

        except Exception as e:
                        print ("ERROR:" + url)
                        print (e)

if __name__ == '__main__':

    priority = {}
    frontier = {}
    crawl_count = 1

    text = ""
    url = 'http://en.wikipedia.org/wiki/Costa_Concordia_disaster'
    url1 = 'http://www.telegraph.co.uk/news/worldnews/europe/italy/10312026/Costa-Concordia-recovery-timeline-of-cruise-ship-disaster.html'
    url2 = 'http://en.wikipedia.org/wiki/Costa_Concordia'

    frontier[url] = 0
    priority[url] = 0
    frontier[url1] = 10000
    priority[url1] = 3
    frontier[url2] = 10000
    priority[url2] = 2

    discovered = set()

    inlink_dict = defaultdict(list)

    inlink_dict[url]
    inlink_dict[url1]
    inlink_dict[url2]

    start_crawl_single_url(url)

    with open("E:\\IR-HW3\\inlinks", "wb") as f:
        for (url, url_inlinks) in inlink_dict.iteritems():
            f.write(url)
            for l in url_inlinks:
                f.write(" " + l)
            f.write("\n")