package com.jetbrains.handson.httpapi

import models.Pacientes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.DriverManager

// https://www.baeldung.com/kotlin/exposed-persistence
//object Users : Table() {
//    val id = varchar("id", 10) // Column<String>
//    val name = varchar("name", length = 50) // Column<String>
//    val cityId = (integer("city_id") references Cities.id).nullable() // Column<Int?>
//
//    override val primaryKey = PrimaryKey(id, name = "PK_User_ID") // name is optional here
//}

//object Cities : Table() {
//    val id = integer("id").autoIncrement() // Column<Int>
//    val name = varchar("name", 50) // Column<String>
//
//    override val primaryKey = PrimaryKey(id, name = "PK_Cities_ID")
//}

/*
    https://docs.google.com/document/d/10LVrSiR2a6cftdBJ3MsaKB2O4CvpA_hUdYRnam4cXX8/edit?usp=sharing

    Como el driver de la Base de Datos (mysql-connector-java-8.0.28.jar) se carga en forma
    dinámica, cuando se arma el FAT JAR no se lo incluye automáticamente. Para incluirlo
    hay que agregarlo en el directorio:

    src/main/resources/
*/


class PacientesDB {

    // https://devexperto.com/object-kotlin-singleton/
    companion object {
        fun connectToDB() {

            Database.connect(
                "jdbc:mysql://localhost:3306/qore", driver = "org.h2.Driver",
                user = "claudio", password = "claudio"
            )

            transaction {

                addLogger(StdOutSqlLogger)

                SchemaUtils.create (Pacientes)

                /*
                    Anda perfecto si defino el campo como datetime
                    en Paciente: val fechaNacimiento = date("fechaNacimiento");

                    val stringDate = "08/02/1962 09:30"
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    val dt = LocalDateTime.parse(stringDate, formatter)
                */

            }
        }
    }
}