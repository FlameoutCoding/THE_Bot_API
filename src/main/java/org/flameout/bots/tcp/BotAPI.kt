package org.flameout.bots.tcp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

open class BotOutboundAPI(val sendAsLine : (String) -> Unit)


@Component
open class BotInboundAPI(){
    var onSubgameStart : ((Int) -> Unit)? = null
    //Note: onBlindClaim(bigBlindPlayer,smallBlindPlayer)
    var onBlindClaim : ((Int,Int) -> Unit)? = null
    //first parameter: Current amout set, second parameter: amount to call
    var onMyActionRequired : ((Int,Int) -> Unit)? = null
    var onMyPrivateCards : ((Int,Int) -> Unit)? = null
    var onSubgameResult : ((Map<Int,Float>) -> Unit)? = null
}
