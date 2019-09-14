package org.flameout.bots

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.io.BufferedReader
import java.io.InputStreamReader

fun main(args : Array<String>){
    SpringApplication.run(BotAPI::class.java,*args)
}

@SpringBootApplication
open class BotAPI{}