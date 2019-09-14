package org.flameout.bots.tcp

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
open class BotAPI(
        var onSubgameStart : ((Int) -> Unit)? = null)