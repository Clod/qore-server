package routes

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.jetbrains.handson.httpapi.PacientesDAO
import com.jetbrains.handson.httpapi.PacientesDB
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import models.PacienteSerial
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


// Si Clod, es una extension function.
fun Route.patientRouting() {

    // No entiendo por qué devlopmentMode no está visible dentro de las rutas
    val modoDesarrollo = developmentMode

    // Pongo esta ruta antes de la validación contra Firebase como para poder probar fácilmente si el servidor está vivo.
    route("/isalive") {
        get {
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            call.respondText("Yes buddy, I´m alive ($currentDate)", status = HttpStatusCode.OK)
        }
    }

    PacientesDB.connectToDB()

    // https://firebase.google.com/docs/admin/setup
    // val serviceAccount = FileInputStream("D:\\home\\Kotlin\\ktor-http-api-sample-main\\src\\main\\resources\\cardio-gut-firebase-adminsdk-q7jz3-6c2cf52658.json")
    println("Absolute path: " + File(".").absolutePath)
    // val serviceAccount = FileInputStream("./build/resources/main/Firestore/cardio-gut-firebase-adminsdk-q7jz3-6c2cf52658.json")

    if (modoDesarrollo) {
        println("Imprimiendo el contenido del archivo")
        val inputStream =
            this::class.java.classLoader.getResourceAsStream("cardio-gut-firebase-adminsdk-q7jz3-6c2cf52658.json")
        inputStream.bufferedReader().useLines { lines ->
            lines.forEach {
                println(it)
            }
        }
    }

    // Ahora intento para dentro del JAR en Linux (que debería también andar en Windows...)
    // En el VPS uno de los problemas que tenía era que el servidor tenía mal la hora y por eso no validaba los
    // tokens correctamente: "firebase id token is not yet valid"
    println("Intento abrir el archivo de Firebase dentro del jar")
    val serviceAccount =
        this::class.java.classLoader.getResourceAsStream("cardio-gut-firebase-adminsdk-q7jz3-6c2cf52658.json")
    println("Luego de intentar abrir el archivo de Firebase dentro del jar")
    //    val options: FirebaseOptions = FirebaseOptions.builder()
    //        .setCredentials(GoogleCredentials.getApplicationDefault())
    //        .build()

    val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()

    FirebaseApp.initializeApp(options)

    route("/patients") {
        val pacientesDAO = PacientesDAO()

        get {

            pacientesDAO.getAllPatients()

            // Acá valido el token que me manda el cliente
            val token = call.request.headers["authorization"]?.substring(7)
            //https://firebase.google.com/docs/auth/admin/verify-id-tokens#java
            val decodedToken = FirebaseAuth.getInstance().verifyIdToken(token)
            val uid = decodedToken.uid
            //customerStorage.add(customer)
            println("TODOS LOS PACIENTES")
            println("ENVIADO POR: $uid")

            // call.respond(customerStorage) // Con esto anda...
            call.respond(pacientesDAO.getAllPatients())

        }

        get("{token}") {

            // Si puedo recuperar el token lo asigno. Si no, devuelvo error
            val token = call.parameters["token"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
//            val customer =
//                customerStorage.find { it.id == id } ?: return@get call.respondText(
//                    "No customer with id $id",
//                    status = HttpStatusCode.NotFound
//                )

            call.respond(pacientesDAO.getPatientsByLastName("%${token}%"))
        }

        get("{dato}/{valor}") {

            try {
                val token = call.request.headers["authorization"]?.substring(7)
                //https://firebase.google.com/docs/auth/admin/verify-id-tokens#java
                if (modoDesarrollo) println("Token: $token")
                val decodedToken = FirebaseAuth.getInstance().verifyIdToken(token)
                println("Token decodificado")
                val uid = decodedToken.email
                println("EJECUTANDO COMANDO")
                println("ENVIADO POR EL USUARIO: $uid")

                val dato = call.parameters["dato"] ?: return@get call.respondText(
                    "Tiene que ser Documento o Apellido",
                    status = HttpStatusCode.BadRequest
                )
                val valor = call.parameters["valor"] ?: return@get call.respondText(
                    "Missing or malformed id",
                    status = HttpStatusCode.BadRequest
                )

                println("Buscando por: $dato con valor $valor ")

                if (dato == "Apellido")
                    call.respond(pacientesDAO.getPatientsByLastName("%${valor}%"))
                else
                    call.respond(pacientesDAO.getPatientsById("%${valor}%"))
            } catch (e: Exception) {
                println("Usuario no autorizado a hacer consultas")
                call.respondText("Usuario no autorizado a hacer consultas", status = HttpStatusCode.Forbidden)
                return@get
            }

        }
        // Alta de paciente en la BD
        post {
            try {// call.receive integrates with the Content Negotiation plugin we configured one
                // of the previous sections. Calling it with the generic parameter Customer
                // automatically deserializes the JSON request body into a Kotlin Customer object.
                val patient = call.receive<PacienteSerial>()
                val token = call.request.headers["authorization"]?.substring(7)
                //https://firebase.google.com/docs/auth/admin/verify-id-tokens#java
                if (modoDesarrollo) println("Token: $token")
                val decodedToken = FirebaseAuth.getInstance().verifyIdToken(token)
                println("Token decodificado")
                val uid = decodedToken.email
                println("PACIENTE ALTA: $patient solicitada por $uid")

                // Antes de dar el ALTA me fijo que no exista otro paciente del mismo país con el mismo documento
                // Miro país por si se da la casualidad de dos pacientes de distintos países y mismo id
                val existe = pacientesDAO.checkIfPatientByCountryAndId(patient.nacionalidad, patient.documento)
                if (existe) {
                    println("Intentado dar de alta un paciente existente")
                    call.respondText("Paciente ya existente en la base", status = HttpStatusCode.NotAcceptable)
                    return@post
                }

                val idPPac = pacientesDAO.storePatient(patient)
                call.respondText("Patient stored correctly {$idPPac}", status = HttpStatusCode.Created)
            } catch (e: Exception) {

                println("Usuario no autorizado a dar altas")
                call.respondText("Usuario no autorizado a dar altas", status = HttpStatusCode.Forbidden)
                return@post

            }
        }

        // Modificación de paciente en la BD
        put {
            try {// call.receive integrates with the Content Negotiation plugin we configured one
                // of the previous sections. Calling it with the generic parameter Customer
                // automatically deserializes the JSON request body into a Kotlin Customer object.
                val patient = call.receive<PacienteSerial>()
                val token = call.request.headers["authorization"]?.substring(7)
                //https://firebase.google.com/docs/auth/admin/verify-id-tokens#java
                if (modoDesarrollo) println("Token: $token")
                val decodedToken = FirebaseAuth.getInstance().verifyIdToken(token)
                println("Token decodificado")
                val uid = decodedToken.email
                print("PACIENTE MODIFICAR: $patient solicitada por $uid")
                val idPPac = pacientesDAO.updatePatient(patient)
                call.respondText("Patient updated correctly {$idPPac}", status = HttpStatusCode.OK)
            } catch (e: Exception) {
                println("Usuario no autorizado a modificar datos")
                call.respondText("Usuario no autorizado a modificar datos", status = HttpStatusCode.Forbidden)
                return@put
            }
        }

    }
}
