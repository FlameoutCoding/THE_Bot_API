package org.flameout.bots.tcp

import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.stream.Collectors

@Component
open class MessageProcessor{
    private val logger = LoggerFactory.getLogger("MessageProcessor")

    //This... thing actually works
    @Autowired
    private lateinit var api : BotAPI

    @Bean
    private fun generateAPI() : BotAPI{
        return BotAPI()
    }

    fun process(message : String){
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
            else -> {
                logger.warn("Invalid command received: '$messageType', ignoring message")
            }
        }

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