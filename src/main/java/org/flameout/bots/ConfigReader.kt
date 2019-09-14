package org.flameout.bots

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

//I really do NOT want to autowire a HashMap...
data class APIConfiguration(val data : HashMap<String,String>)

@Component
class ConfigReader{
    @Bean
    fun readConfig() : APIConfiguration{
        val logger = LoggerFactory.getLogger("APIConfiguration")
        val result = HashMap<String,String>()
        val reader = BufferedReader(FileReader(File(".config")))
        logger.debug("Reading configuration")
        while(true){
            val line = reader.readLine() ?: break
            if(line.contains("=")){
                val parts = line.split("=")
                result[parts[0]] = parts[1]
                logger.debug("Config entry read: "+parts[0]+" -> "+parts[1])
            }
        }

        return APIConfiguration(result)
    }
}