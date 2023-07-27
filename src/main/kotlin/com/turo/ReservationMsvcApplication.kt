package com.turo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReservationMsvcApplication

fun main(args: Array<String>) {
    runApplication<ReservationMsvcApplication>(*args)
}
