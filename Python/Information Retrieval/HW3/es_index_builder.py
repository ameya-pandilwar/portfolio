__author__ = 'Ameya'

import crawler_config

if __name__ == '__main__':
    inlinks_map = {}

    with open(crawler_config.INLINKS_WRITE_FILE_PATH, crawler_config.FILE_READ_MODE) as inlinks_file:
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
    print len(inlinks_map)

    outlinks_output = open(crawler_config.INLINKS_FILE, crawler_config.FILE_WRITE_MODE)

    count = 0
    for url in inlinks_map.keys():
        count += 1
        if inlinks_map[url] is None:
            outlinks_output.write(url)
        else:
            outlinks_output.write(url)
            write_line = ""
            for l in inlinks_map[url]:
                write_line = write_line + " " + l
            outlinks_output.write(write_line)

    outlinks_output.close()
    print count