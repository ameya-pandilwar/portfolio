__author__ = 'Ameya'


if __name__ == '__main__':
    inlinks1_path = "E:\\IR-HW3\\inlinks"
    inlinks2_path = "C:\\Users\\Ameya\\Desktop\\pm_inlinks"
    inlinks3_path = "C:\\Users\\Ameya\\Desktop\\sm_inlinks"

    inlinks_dic = {}

    with open(inlinks1_path, "r") as inlinks1:
        for line in iter(inlinks1):
            key = ""
            val = []
            if line.__contains__(" "):
                key = line.split(" ")[0]
                val = list(line.split(" ")[1:])
            else:
                key = line
                val = []
            inlinks_dic[key] = val
    print len(inlinks_dic)

    with open(inlinks2_path, "r") as inlinks2:
        for line in iter(inlinks2):
            key = ""
            val = []
            if line.__contains__(" "):
                key = line.split(" ")[0]
                val = list(line.split(" ")[1:])
            else:
                key = line
                val = []
            if key in inlinks_dic:
                if inlinks_dic[key] is not None:
                    inlinks_dic[key].extend(val)
                else:
                    inlinks_dic[key] = val
            else:
                inlinks_dic[key] = val
    print len(inlinks_dic)

    with open(inlinks3_path, "r") as inlinks3:
        for line in iter(inlinks3):
            key = ""
            val = []
            if line.__contains__(" "):
                key = line.split(" ")[0]
                val = list(line.split(" ")[1:])
            else:
                key = line
                val = []
            if key in inlinks_dic:
                if inlinks_dic[key] is not None:
                    inlinks_dic[key].extend(val)
                else:
                    inlinks_dic[key] = val
            else:
                inlinks_dic[key] = val

    print len(inlinks_dic)
    output_file = open("E:\\IR-HW3\\outfile", "w")

    count = 0
    for url in inlinks_dic.keys():
        count += 1
        if inlinks_dic[url] is None:
            output_file.write(url)
        else:
            output_file.write(url)
            write_line = ""
            for l in inlinks_dic[url]:
                write_line = write_line + " " + l
            output_file.write(write_line)

    output_file.close()
    print count