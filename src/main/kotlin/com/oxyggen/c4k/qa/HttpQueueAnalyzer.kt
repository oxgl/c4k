package com.oxyggen.c4k.qa

import com.oxyggen.c4k.config.Config
import com.oxyggen.c4k.event.EngineEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel

open class HttpQueueAnalyzer(
    coroutineScope: CoroutineScope,
    queueId: String,
    config: Config,
    engineChannel: SendChannel<EngineEvent>
) : QueueAnalyzer(coroutineScope, queueId, config, engineChannel) {


}