package org.flameout.bots

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class ConfigReader{
    companion object {
        fun readConfig() : HashMap<String,String>{
            val result = HashMap<String,String>()
            val reader = BufferedReader(FileReader(File(".config")))
            while(true){
                val line = reader.readLine() ?: break
                if(line.contains("=")){
                    result[line.split("=")[0]] = line.split("=")[1]
                }
            }

            return result
        }
    }
}