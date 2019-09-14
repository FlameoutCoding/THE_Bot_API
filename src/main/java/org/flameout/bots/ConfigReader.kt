package org.flameout.bots

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
        val result = HashMap<String,String>()
        val reader = BufferedReader(FileReader(File(".config")))
        while(true){
            val line = reader.readLine() ?: break
            if(line.contains("=")){
                result[line.split("=")[0]] = line.split("=")[1]
            }
        }

        return APIConfiguration(result)
    }
}