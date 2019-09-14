package org.flameout.bots.tcp

import org.flameout.bots.APIConfiguration
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.logging.Logger

@Component
open class TCPChannel(){
    private val logger = Logger.getLogger("TCPChannel")
    private val MY_GAME_SLOT : Int
    private val reader : BufferedReader
    private val writer : PrintWriter
    private val socket : Socket
    private val token : String

    @Autowired
    private lateinit var configuration : APIConfiguration

    init{
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
            logger.severe("key not configured")
            shutdown()
            throw Exception("I wanna make the compiler happy")
        }    }

    private fun listen(){
        while(true){
            val line = reader.readLine() ?: break
            logger.fine("Incoming message: $line")
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
            return jsonified.getInt("identified")
        }catch(ex : Exception){
            logger.severe("Server authentication failed, exiting. $ex")
            ex.printStackTrace()
            shutdown()
            return -1   //Irrelevant but makes the compiler happy!
        }
    }

    private fun jsonToUsefulString(msg : JSONObject) : String{
        return msg.toString().replace("\t","").replace("\n","")
    }

    private fun send(message : String){
        logger.fine("Sending message: $message")
        writer.write("$message\n")
        writer.flush()
    }

    private fun shutdown(){
        logger.info("Shutting down")
        reader.close()
        writer.close()
        socket.close()
        System.exit(0)
    }
}