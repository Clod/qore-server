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
import models.Customer
import models.PacienteSerial
import models.customerStorage
import java.io.FileInputStream
import kotlin.text.get


// Si Clod, es una extension function.
fun Route.customerRouting() {

    PacientesDB.connectToDB()

    // https://firebase.google.com/docs/admin/setup
    val serviceAccount = FileInputStream("D:\\home\\Kotlin\\ktor-http-api-sample-main\\src\\main\\resources\\cardio-gut-firebase-adminsdk-q7jz3-6c2cf52658.json")

    val options: FirebaseOptions = FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()

    FirebaseApp.initializeApp(options)

    route ("/patients") {
        val pacientesDAO = PacientesDAO()

        get {

            pacientesDAO.getAllPatients()

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
            val token = call.parameters["token"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
//            val customer =
//                customerStorage.find { it.id == id } ?: return@get call.respondText(
//                    "No customer with id $id",
//                    status = HttpStatusCode.NotFound
//                )

            call.respond(pacientesDAO.getSomePatients("%${token}%"))
        }

        post {
            // call.receive integrates with the Content Negotiation plugin we configured one
            // of the previous sections. Calling it with the generic parameter Customer
            // automatically deserializes the JSON request body into a Kotlin Customer object.
            val patient = call.receive<PacienteSerial>()
            val token = call.request.headers["authorization"]?.substring(7)
            //customerStorage.add(customer)
            print("PACIENTE: $patient")
            print("ENVIADO POR: $token")
            call.respondText("Patient stored correctly", status = HttpStatusCode.Created)
        }

    }

    route("/customer") {
        get {
            if (customerStorage.isNotEmpty()) {
                call.respond(customerStorage)
            } else {
                call.respondText("No customers found", status = HttpStatusCode.NotFound)
            }
        }
        get("{id}") {

            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val customer =
                customerStorage.find { it.id == id } ?: return@get call.respondText(
                    "No customer with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(customer)
        }
        post {
            // call.receive integrates with the Content Negotiation plugin we configured one
            // of the previous sections. Calling it with the generic parameter Customer
            // automatically deserializes the JSON request body into a Kotlin Customer object.
            val customer = call.receive<Customer>()
            customerStorage.add(customer)
            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (customerStorage.removeIf { it.id == id }) {
                call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}