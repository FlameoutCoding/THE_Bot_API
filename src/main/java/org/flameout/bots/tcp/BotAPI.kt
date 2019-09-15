package org.flameout.bots.tcp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

open class BotApiSender(val sendAsLine : (String) -> Unit)

@Component
open class BotAPI(){
    @Autowired
    lateinit var outbound : BotApiSender

    var onSubgameStart : ((Int) -> Unit)? = null
    //Note: onBlindClaim(bigBlindPlayer,smallBlindPlayer)
    var onBlindClaim : ((Int,Int) -> Unit)? = null
}