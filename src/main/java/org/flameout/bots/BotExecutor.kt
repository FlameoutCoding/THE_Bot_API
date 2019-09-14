package org.flameout.bots

import java.io.BufferedReader
import java.io.InputStreamReader

fun main(){
    val configFileData = ConfigReader.readConfig()

    val host = when(configFileData.containsKey("Host")){
        true -> configFileData["Host"]!!
        false -> {
            System.out.println("Host IP?")
            readLine()
        }
    }

    val port = when(configFileData.containsKey("Port")){
        true -> configFileData["Port"]!!.toInt()
        false -> {
            System.out.println("Port?")
            readLine()!!.toInt()
        }
    }

    System.out.println("Connection token?")
    val token = readLine()!!

    
}