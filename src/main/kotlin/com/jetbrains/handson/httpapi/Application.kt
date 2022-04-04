package com.jetbrains.handson.httpapi

import io.ktor.application.Application
import io.ktor.features.*

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.serialization.*
import routes.customerRouting

fun main(args: Array<String>): Unit = io.ktor.server.tomcat.EngineMain.main(args)

// La referencia en application.conf
fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    registerCustomerRoutes()
}

fun Application.registerCustomerRoutes() {
    routing {
        customerRouting()
    }
}