#!/usr/bin/python3

import os
from os import listdir
from os.path import isfile, join
import sys
import re

path = "/home/aleague/public_html/csc344"

os.chdir(path)

for i in range(5):
	os.chdir(path + "/a" + str(i + 1))
	
	mypath = os.getcwd()
	
	files = [f for f in listdir(mypath) if isfile(join(mypath, f))]

	identSet = set()
	
	summ = open("../summary_a" + str(i + 1) + ".html","w+")
	summ.write("<!DOCTYPE html>\n<html>\n<head>\n")
	summ.write("<title>Project " + str(i + 1) + " Summary</title>\n</head>\n")
	summ.write("<body>\n<h2>Project " + str(i + 1) + " documents:</h2>\n")
	summ.write("<h3>words in file:</h3>\n")

	break_re = re.compile('//.*|;;.*|%%.*|#.*|\'.*\'|\".*\"|\.|_\W|\?|;|!|,|~|\^|\*|\n|\s|%|\[|\]|\{|\}|\(|\)|<|>|-\W|-\d|\+|:|&|\||=|\'|[0-9+]')
	for f in files:
		summ.write("<a href=\"a" + str(i + 1) + "/" + f + "\"><br>\n")
		summ.close()
		os.system('wc -l ' + f + ' >> ../summary_a' + str(i + 1) + '.html')
		summ = open("../summary_a" + str(i + 1) + ".html","a+")
		summ.write("</a>\n")
		with open(f,"r") as r:
			for line in r:
				for word in re.split(break_re, line):
					identSet.add(word)
					
	summ.write("<h3>list of identifiers:</h3>\n")
	for x in identSet:
		if x: summ.write(x + "<br>\n")
	summ.write("</body>\n</html>")
	summ.close()

os.chdir(path)

index = open("./index.html","w+")
index.write("<!DOCTYPE html>\n<html>\n<head>\n")
index.write("<title>Class Summary</title>\n</head>\n")
index.write("<body>\n<h2>Links to summary documents:</h2>\n")
for i in range(5):
	index.write("<a href='summary_a%d.html'>Assignment %d</a><br>\n" % ((i + 1), (i + 1)))
index.write("<h3>A. League, CSC344, Spring 2019</h3></body>\n</html>")
index.close()

address = input("Enter email: ")

os.system('tar --exclude=\'./uuencode\' --exclude=\'./aleague_python.tar.gz\' -czvf aleague_python.tar.gz .')
os.system('./uuencode aleague_python.tar.gz aleague_python.tar.gz | mailx -s \"A. League Course Summary\" %s' % address)

