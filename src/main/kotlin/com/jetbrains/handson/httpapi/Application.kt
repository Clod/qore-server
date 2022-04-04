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

    // Sin esto, me puedo conectar desde un programa de consola y desde Anroid
    // pero no desde la Web
    // https://stackoverflow.com/questions/60191683/xmlhttprequest-error-in-flutter-web-enabling-cors-aws-api-gateway
    install (CORS) {
        anyHost()
    }

    registerCustomerRoutes()
}

fun Application.registerCustomerRoutes() {
    routing {
        customerRouting()
    }
}