package org.flameout.bots.tcp

import org.flameout.bots.APIConfiguration
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import javax.annotation.PostConstruct

@Component
open class TCPChannel(){
    private val logger = LoggerFactory.getLogger("TCPChannel")
    private var MY_GAME_SLOT = -1
    private lateinit var reader : BufferedReader
    private lateinit var writer : PrintWriter
    private lateinit var socket : Socket
    private lateinit var token : String

    @Autowired
    private lateinit var configuration : APIConfiguration

    @Autowired
    private lateinit var messageProcessor : MessageProcessor



    @PostConstruct
    private fun setup(){
        val host = readConfigEntry("Host")
        val port = readConfigEntry("Port").toInt()
        token = readConfigEntry("Token")

        logger.info("Setting up TCP connection")
        socket = Socket(host,port)
        reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        writer = PrintWriter(socket.getOutputStream())
        logger.info("Doing authentication")
        MY_GAME_SLOT = register()
        Thread{listen()}.start()
        logger.info("Everything set up successfully")
    }

    private fun readConfigEntry(key : String) : String{
        return try{
            configuration.data[key]!!
        }catch(ex : Exception){
            logger.error("$key not configured")
            shutdown()
            throw Exception("I wanna make the compiler happy")
        }    }

    private fun listen(){
        while(true){
            val line = reader.readLine() ?: break
            logger.debug("Incoming message: $line")
            try {
                messageProcessor.process(line)
            }catch(ex : Exception){
                logger.warn("Exception in message parsing catched to keep listen thread alive")
                logger.warn("Exception: $ex")
                ex.printStackTrace()
            }
        }
        logger.info("Got null line from reader")
        shutdown()
    }

    private fun register() : Int{
        val authMessage = JSONObject()
        authMessage.put("identifier",token)

        send(jsonToUsefulString(authMessage))
        val response = reader.readLine()!!

        try{
            val jsonified = JSONObject(response)
            if(jsonified.getString("response") != "ok"){
                throw Exception("Response code not 'ok'")
            }
            val slotId = jsonified.getInt("identified")
            logger.info("Authenticated for slot $slotId")
            return slotId
        }catch(ex : Exception){
            logger.error("Server authentication failed, exiting. $ex")
            ex.printStackTrace()
            shutdown()
            return -1   //Irrelevant but makes the compiler happy!
        }
    }

    private fun jsonToUsefulString(msg : JSONObject) : String{
        return msg.toString().replace("\t","").replace("\n","")
    }

    private fun send(message : String){
        logger.debug("Sending message: $message")
        writer.write("$message\n")
        writer.flush()
    }

    private fun shutdown(){
        logger.info("Shutting down")
        try {
            reader.close()
            writer.close()
            socket.close()
        }catch(ex : Exception){}
        System.exit(0)
    }
}