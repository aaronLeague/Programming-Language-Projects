#!/usr/bin/env python

import os
from os import listdir
from os.path import isfile, join
import sys

if len(sys.argv) > 1:
        DIRECT = sys.argv[1]
        os.chdir(DIRECT)

mypath = os.getcwd()

files = [f for f in listdir(mypath) if isfile(join(mypath, f))]

for file in files:
        os.system('wc -l ' + file)
