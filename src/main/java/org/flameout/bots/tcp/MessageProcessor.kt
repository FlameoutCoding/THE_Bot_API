package org.flameout.bots.tcp

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
open class BotAPI(
        var onSubgameStart : ((Int) -> Unit)? = null
)

@Component
open class MessageProcessor{
    private val logger = LoggerFactory.getLogger("MessageProcessor")

    @Autowired
    private lateinit var api : BotAPI

    fun process(message : String){

    }
}