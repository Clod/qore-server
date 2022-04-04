package com.jetbrains.handson.httpapi

import models.Pacientes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

// https://www.baeldung.com/kotlin/exposed-persistence
object Users : Table() {
    val id = varchar("id", 10) // Column<String>
    val name = varchar("name", length = 50) // Column<String>
    val cityId = (integer("city_id") references Cities.id).nullable() // Column<Int?>

    override val primaryKey = PrimaryKey(id, name = "PK_User_ID") // name is optional here
}

object Cities : Table() {
    val id = integer("id").autoIncrement() // Column<Int>
    val name = varchar("name", 50) // Column<String>

    override val primaryKey = PrimaryKey(id, name = "PK_Cities_ID")
}

class PacientesDB {

    // https://devexperto.com/object-kotlin-singleton/
    companion object {
        fun connectToDB() {
            Database.connect(
                "jdbc:mysql://localhost:3306/prueba", driver = "org.h2.Driver",
                user = "root", password = "root"
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



//                Paciente.new {
//                    nombre = "Juan"
//                    apellido = "De los Palotes"
//                    nacionalidad = "Argentina"
//                    documento = "1234567890"
//                    fechaNacimiento = LocalDate.parse("1962-02-08")
//                }

                // val pacientes = Pacientes.selectAll()

                // val pacientes = Pacientes.select { Pacientes.nombre eq "Juan"  } // Anda

//                val pacientes = Pacientes.select { Pacientes.nacionalidad like "%in%"  }
//
//                println("Todos los pacientes: ${pacientes.toList()}")
            }
        }
    }
}