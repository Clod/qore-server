package routes

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
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
//    val serviceAccount = FileInputStream("D:\\home\\Kotlin\\ktor-http-api-sample-main\\src\\main\\resources\\cardio-gut-firebase-adminsdk-q7jz3-6c2cf52658.json")
    println("Absolute path: " + File(".").getAbsolutePath())
    // val serviceAccount = FileInputStream("./build/resources/main/Firestore/cardio-gut-firebase-adminsdk-q7jz3-6c2cf52658.json")

//    val options: FirebaseOptions = FirebaseOptions.Builder()
//        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//        .build()
//    System.out.println("Absolute path: " + File("cardio-gut-firebase-adminsdk-q7jz3-6c2cf52658.json").getAbsolutePath());
//
//    val fileContent = File("cardio-gut-firebase-adminsdk-q7jz3-6c2cf52658.json").readText(Charsets.UTF_8)
//
//    println("File content: $fileContent")

    val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.getApplicationDefault())
        .build()

    FirebaseApp.initializeApp(options)

    route("/patients") {
        val pacientesDAO = PacientesDAO()

        get {

            pacientesDAO.getAllPatients()

            // Acá valido el token que me manda el cliente
//           val token = call.request.headers["authorization"]?.substring(7)
//            //https://firebase.google.com/docs/auth/admin/verify-id-tokens#java
//            val decodedToken = FirebaseAuth.getInstance().verifyIdToken(token)
//            val uid = decodedToken.uid
            //customerStorage.add(customer)
            println("TODOS LOS PACIENTES")
//            println("ENVIADO POR: $uid")

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

            val dato = call.parameters["dato"] ?: return@get call.respondText(
                "Tiene que ser Documento o Apellido",
                status = HttpStatusCode.BadRequest
            )
            val valor = call.parameters["valor"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )

            println("Buscando por: $dato con valor $valor ")

//            val token = call.parameters["token"] ?: return@get call.respondText(
//                "Missing or malformed id",
//                status = HttpStatusCode.BadRequest
//            )

            if (dato == "Apellido")
                call.respond(pacientesDAO.getPatientsByLastName("%${valor}%"))
            else
                call.respond(pacientesDAO.getPatientsById("%${valor}%"))

        }
        // Alta de paciente en la BD
        post() {
            // call.receive integrates with the Content Negotiation plugin we configured one
            // of the previous sections. Calling it with the generic parameter Customer
            // automatically deserializes the JSON request body into a Kotlin Customer object.
            val patient = call.receive<PacienteSerial>()
            val token = call.request.headers["authorization"]?.substring(7)
            //customerStorage.add(customer)
            print("PACIENTE ALTA: $patient")
            print("\nENVIADO POR: $token")

            // Antes de dar el ALTA me fijo que no exista otro paciente del mismo país con el mismo documento
            // Miro país por si se da la casualidad de dos pacientes de distintos países y mismo id
            val existe = pacientesDAO.checkIfPatientByCountryAndId(patient.nacionalidad, patient.documento)
            if (existe) {
                println("Intentado dar de alta un paciente existente")
                call.respondText("Paciente ya existente en la base", status = HttpStatusCode.NotAcceptable)
                return@post
            }

            val idPPac = pacientesDAO.storePatient(patient)
            call.respondText("Patient stored correctly {${idPPac.toString()}}", status = HttpStatusCode.Created)
        }

        // Modificación de paciente en la BD
        put() {
            // call.receive integrates with the Content Negotiation plugin we configured one
            // of the previous sections. Calling it with the generic parameter Customer
            // automatically deserializes the JSON request body into a Kotlin Customer object.
            val patient = call.receive<PacienteSerial>()
            val token = call.request.headers["authorization"]?.substring(7)
            //customerStorage.add(customer)
            print("PACIENTE MODIFICAR: $patient")
            print("ENVIADO POR: $token")
            val idPPac = pacientesDAO.updatePatient(patient)
            call.respondText("Patient updated correctly {${idPPac.toString()}}", status = HttpStatusCode.OK)
        }

    }

//    route("/customer") {
//        get {
//            if (customerStorage.isNotEmpty()) {
//                call.respond(customerStorage)
//            } else {
//                call.respondText("No customers found", status = HttpStatusCode.NotFound)
//            }
//        }
//        get("{id}") {
//
//            val id = call.parameters["id"] ?: return@get call.respondText(
//                "Missing or malformed id",
//                status = HttpStatusCode.BadRequest
//            )
//            val customer =
//                customerStorage.find { it.id == id } ?: return@get call.respondText(
//                    "No customer with id $id",
//                    status = HttpStatusCode.NotFound
//                )
//            call.respond(customer)
//        }
//        post {
//            // call.receive integrates with the Content Negotiation plugin we configured one
//            // of the previous sections. Calling it with the generic parameter Customer
//            // automatically deserializes the JSON request body into a Kotlin Customer object.
//            val customer = call.receive<Customer>()
//            customerStorage.add(customer)
//            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
//        }
//        delete("{id}") {
//            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
//            if (customerStorage.removeIf { it.id == id }) {
//                call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
//            } else {
//                call.respondText("Not Found", status = HttpStatusCode.NotFound)
//            }
//        }
//    }
}