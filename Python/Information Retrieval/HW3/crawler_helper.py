__author__ = 'Ameya'

import time
import crawler_utils
import crawler_config
from urlparse import urljoin


def canonicalize(str, url):
    link_str = canonicalize_url(str, url)

    if link_str != "":
        if link_str in crawler_utils.inlink_dict:
            crawler_utils.inlink_dict[link_str].append(url)
        else:
            crawler_utils.inlink_dict[link_str]

        if link_str not in crawler_utils.discovered:
            if link_str in crawler_utils.frontier:
                crawler_utils.frontier[link_str] += 1
            else:
                crawler_utils.frontier[link_str] = 0
                crawler_utils.priority[link_str] = 0

    return link_str


def canonicalize_url(link_str, url):
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


def encode_header(http_header):
    return str(http_header).encode('utf-8')


def clean_html(soup):
    for script in soup(["script", "style"]):
        script.extract()

    text = soup.get_text()
    lines = (line.strip() for line in text.splitlines())
    chunks = (phrase.strip() for line in lines for phrase in line.split("  "))
    text = '\n'.join(chunk for chunk in chunks if chunk)

    return text.encode('utf-8')


def store_inlinks():
    with open(crawler_config.INLINKS_WRITE_FILE_PATH, "wb") as f:
        for (url, url_inlinks) in crawler_utils.inlink_dict.iteritems():
            f.write(url)
            for l in url_inlinks:
                f.write(" " + l)
            f.write("\n")


def useful_url(url_link):
    if url_link.endswith('.JPG') or url_link.endswith('.jpg') or url_link.endswith('.svg'):
        return False
    if url_link.__contains__('action=edit'):
        return False
    if url_link.__contains__('Wikipedia:') or url_link.__contains__('File:') or url_link.__contains__('Talk:'):
        return False
    if url_link.__contains__('wikipedia.org') and not url_link.__contains__('en'):
        return False
    return True


def politeness_policy(period):
    time.sleep(period)