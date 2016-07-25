#!/usr/bin/env python
# -*- coding: UTF-8 -*-
import requests
from java.util import Random
class FundB(object):
    def __init__(self):
        print 'init...'
    def process(self, millis):
        rng = Random()
        if rng.nextBoolean():
            print "Came up heads"
        else:
            print "Came up tails"
        fundBPath = 'http://www.jisilu.cn/data/sfnew/fundb_list/?___t=' + millis
        r=requests.get(fundBPath)
        return r.text