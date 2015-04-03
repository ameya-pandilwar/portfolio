__author__ = 'Ameya'

import urllib2
import robotparser
import crawler_utils
import crawler_helper
from urlparse import urljoin
from datetime import datetime
from bs4 import BeautifulSoup


def crawl_seeds(url):
    start = datetime.now()
    file_number = 0

    while len(crawler_utils.frontier) > 0:
        try:
            if url in crawler_utils.frontier and url in priority:
                crawler_utils.frontier.pop(url)
                priority.pop(url)
                crawler_utils.discovered.add(url)
            else:
                maximum_value = max(crawler_utils.frontier.itervalues())
                maximum_keys = [k for k in crawler_utils.frontier if crawler_utils.frontier[k] == maximum_value]
                initial_priority = -1
                for maximum in maximum_keys:
                    if initial_priority < priority[maximum]:
                        initial_priority = priority[maximum]
                        url = maximum

                for url in crawler_utils.frontier:
                    priority[url] += 1

            rp = robotparser.RobotFileParser()
            robot_url = urljoin(url, '/robots.txt')
            rp.set_url(robot_url)
            rp.read()
            flag = rp.can_fetch("*", url)

            print url + " -- is allowed to crawl -- " + str(flag)

            if flag:
                response = urllib2.urlopen(url)
                html_response = response.read()
                soup = BeautifulSoup(html_response)
                http_headers = response.info()

                unique_urls = set()
                for link in soup.find_all('a', href=True):
                    link_str = link.get('href').encode('utf-8')
                    if not crawler_helper.useful_url(link_str):
                        continue
                    if link_str not in unique_urls:
                        l = crawler_helper.canonicalize(link_str, url)
                        unique_urls.add(str(l))

                if soup.title:
                    title = soup.title.string.encode('utf-8')
                else:
                    title = 'Default Title'

                file_number += 1
                with open('E:\\IR-HW3\\Web_Crawler\\' + str(file_number), 'wb') as cleaned_text:
                    cleaned_text.write("<DOC>\n")
                    cleaned_text.write("<DOCNO>" + url + "</DOCNO>\n")
                    cleaned_text.write("<HTTP>\n" + crawler_helper.encode_header(http_headers) + "</HTTP>\n")
                    cleaned_text.write("<HEAD>" + title + "</HEAD>\n")
                    cleaned_text.write("<TEXT>\n" + crawler_helper.clean_html(soup) + "\n</TEXT>\n")
                    cleaned_text.write("<RAW>\n" + soup.encode('utf-8') + "\n<\RAW>\n")
                    cleaned_text.write("<LINKS>")
                    for l in unique_urls:
                        cleaned_text.write(l + "\n")
                    cleaned_text.write("</LINKS>\n</DOC>")

                    crawler_helper.politeness_policy(1)

                    try:
                        maximum_value = max(crawler_utils.frontier.itervalues())
                        maximum_keys = [k for k in crawler_utils.frontier if crawler_utils.frontier[k] == maximum_value]
                        initial_priority = -1
                        crawl_url = ""
                        for maximum in maximum_keys:
                            if initial_priority < priority[maximum]:
                                initial_priority = priority[maximum]
                                crawl_url = maximum

                        for url in crawler_utils.frontier:
                            priority[url] += 1

                        if file_number < 12000:
                            print "processing webpage number " + str(file_number) + " at " + str(datetime.now() - start)
                            crawler_helper.politeness_policy(1)
                            url = crawl_url
                        else:
                            print 'end of webpage crawling'
                            break

                    except Exception, e:
                        print "an error occurred for " + url + " -- " + e
            else:
                print url + " is not eligible for crawling"

        except Exception, e:
            print "an error occurred for " + url + " -- " + e


if __name__ == '__main__':
    text = ""
    priority = {}
    crawl_count = 1

    crawler_utils.frontier[crawler_utils.seed1] = 0
    priority[crawler_utils.seed1] = 1
    crawler_utils.frontier[crawler_utils.seed2] = 5000
    priority[crawler_utils.seed2] = 2
    crawler_utils.frontier[crawler_utils.seed3] = 10000
    priority[crawler_utils.seed3] = 3

    crawler_utils.inlinks_map[crawler_utils.seed1]
    crawler_utils.inlinks_map[crawler_utils.seed2]
    crawler_utils.inlinks_map[crawler_utils.seed3]

    crawl_seeds(crawler_utils.seed1)

    crawler_helper.store_inlinks()