package com.oxyggen.c4k.old.group

import com.oxyggen.c4k.target.HttpTarget

class HttpGroup(domain: String) : CrawlGroup(targetClass = HttpTarget::class, id = domain)