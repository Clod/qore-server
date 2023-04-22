package com.jetbrains.handson.httpapi

import io.ktor.application.Application
import io.ktor.features.*

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.serialization.*
import org.slf4j.event.Level
import routes.patientRouting

// fun main(args: Array<String>): Unit = io.ktor.server.tomcat.EngineMain.main(args)

fun main(args: Array<String>) {

    println()
    println()
    println()
    println()
    println ("**********************************************************************")
    println ("******************* Ejecutando Q-ORE server V1.0.0 *******************")
    println ("**********************************************************************")
    println()
    println()

    if (System.getProperty("DEBUG") != null) {
       println("Ejecutando en modo DEBUG")
    } else {
        println("Ejecutando en modo PROD")
    }

    io.ktor.server.tomcat.EngineMain.main(args)
}
// La referencia en application.conf
fun Application.module() {

    val env = System.getenv("JAVA_HOME")
    println (env)
    val google = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
    println(google)

    install(CallLogging) {
        level = Level.DEBUG
    }

    install(ContentNegotiation) {
        json()
    }

    // Sin esto, me puedo conectar desde un programa de consola y desde Android
    // pero no desde la Web
    // https://stackoverflow.com/questions/60191683/xmlhttprequest-error-in-flutter-web-enabling-cors-aws-api-gateway
    // CORS bloquea porque la app viene de http://localhost:56674/ y desde adentro hace una
    // invocación a vcsinc.com.ar:8443
    install(CORS) {
        anyHost()
//        host("localhost:56674")
        header(HttpHeaders.ContentType)
        header(HttpHeaders.Authorization)
        method(HttpMethod.Put)
    }

    registerPatientRoutes()
}

fun Application.registerPatientRoutes() {
    routing {
        patientRouting()
    }
}