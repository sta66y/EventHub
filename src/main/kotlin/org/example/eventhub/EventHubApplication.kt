package org.example.eventhub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class EventHubApplication

fun main(args: Array<String>) {
    runApplication<EventHubApplication>(*args)
}
