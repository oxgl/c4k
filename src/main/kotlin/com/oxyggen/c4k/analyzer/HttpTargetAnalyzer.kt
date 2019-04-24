package com.oxyggen.c4k.analyzer

import com.oxyggen.c4k.target.CrawlTarget
import com.oxyggen.c4k.target.HttpTarget

class HttpTargetAnalyzer : AbstractTargetAnalyzer<HttpTarget>() {
    override suspend fun analyze(target: HttpTarget): Set<CrawlTarget> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}