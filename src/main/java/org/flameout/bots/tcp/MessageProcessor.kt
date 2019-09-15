package org.flameout.bots.tcp

import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.stream.Collectors

@Component
open class MessageProcessor(){
    private val logger = LoggerFactory.getLogger("MessageProcessor")

    //This... thing actually works
    @Autowired
    private lateinit var api : BotAPI

    @Bean
    private fun generateAPI() : BotAPI{
        return BotAPI()
    }

    fun process(message : String,mySlot : Int){
        val message = try{
            JSONObject(message)
        }catch(ex : Exception){
            logger.warn("Invalid JSON message, ignoring it")
            null
        } ?: return

        if(!messageSanityCheck(message,listOf("type"))){
            logger.warn("Message sanity check failed, no type provided")
            return
        }

        val messageType = message.getString("type")
        when(messageType){
            "GameStart" -> processGameStartMessage(message)
            "Blind" -> processBlindMessage(message)
            "ActionRequest" -> processActionRequest(message,mySlot)
            else -> {
                logger.warn("Invalid command received: '$messageType', ignoring message")
            }
        }

    }

    private fun processActionRequest(message : JSONObject,mySlot : Int){
        if(!messageSanityCheck(message,listOf("activePlayer","currentBet","highestBet"))){return}

        val activePlayer_str = message.getString("activePlayer")
        val currentAmt_str = message.getString("currentBet")
        val highestAmt_str = message.getString("highestBet")

        val activePlayer = try{
            activePlayer_str.toInt()
        }catch(ex : Exception){
            logger.warn("Cannot parse parameter 'activePlayer'")
            return
        }

        if(activePlayer != mySlot){
            logger.debug("Ignoring action request, my slot: $mySlot, requested slot: $activePlayer")
            return
        }

        val currentAmt = try{
            currentAmt_str.toInt()
        }catch(ex : Exception){
            logger.warn("Cannot parse parameter 'currentBet'")
            return
        }

        val highestAmt = try{
            highestAmt_str.toInt()
        }catch(ex : Exception){
            logger.warn("Cannot parse parameter 'highestBet'")
            return
        }

        api.onMyActionRequired?.let{it(currentAmt,highestAmt)}
    }

    private fun processBlindMessage(message : JSONObject){
        if(!messageSanityCheck(message,listOf("bigBlindPlayer","smallBlindPlayer"))){return}

        val smallBlind_str = message.getString("smallBlindPlayer")
        val bigBlind_str = message.getString("bigBlindPlayer")

        val smallBlind = try{
            smallBlind_str.toInt()
        }catch(ex : Exception){
            logger.warn("Cannot parse parameter 'smallBlindPlayer'")
            return
        }
        val bigBlind = try{
            bigBlind_str.toInt()
        }catch(ex : Exception){
            logger.warn("Cannot parse parameter 'bigBlindPlayer'")
            return
        }

        logger.debug("Calling API onSubgameStart with ($bigBlind,$smallBlind)")
        api.onBlindClaim?.let{it(bigBlind,smallBlind)}
    }

    private fun processGameStartMessage(message : JSONObject){
        if(!messageSanityCheck(message,listOf("gameId"))){return}

        val gameId_str = message.getString("gameId")

        val gameId = try{
            gameId_str.toInt()
        }catch(ex : Exception){
            logger.warn("Cannot parse parameter 'gameId'")
            return
        }

        logger.debug("Calling API onSubgameStart with ($gameId)")
        api.onSubgameStart?.let{it(gameId)}
    }

    private fun messageSanityCheck(message : JSONObject,keys : List<String>) : Boolean{
        val invalidKeys = keys.stream()
                .filter{!message.has(it)}
                .filter{try{message.getString(it); false} catch(ex : Exception){true}}
                .collect(Collectors.toList())

        if(invalidKeys.isEmpty()){
            return true
        }

        if(invalidKeys.contains("type")){
            return false
        }

        //Should technically not be here but...
        logger.warn("Sanity check for message of type '"+message.getString("type")+"' failed, following keys are missing or have an invalid type:")
        for(key in invalidKeys){
            logger.warn("\t- $key")
        }

        return false
    }
}