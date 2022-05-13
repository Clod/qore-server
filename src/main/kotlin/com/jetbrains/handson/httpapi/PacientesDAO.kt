package com.jetbrains.handson.httpapi

import models.Paciente
import models.PacienteSerial
import models.Pacientes
import models.Pacientes.apellido
import models.Pacientes.documento
import models.Pacientes.fechaNacimiento
import models.Pacientes.nacionalidad
import models.Pacientes.nombre
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// https://github.com/JetBrains/Exposed/wiki/DAO#read-entity-with-a-join-to-another-table

// Entity.new { } to insert new entities
class PacientesDAO {

    fun getAllPatients () : MutableList<PacienteSerial> {

        println("Starting getAllPatients()")

        var patientsList = mutableListOf<Paciente>()
        var patientsListSerial = mutableListOf<PacienteSerial>()

        // https://github.com/JetBrains/Exposed/wiki/DAO#read-entity-with-a-join-to-another-table
        transaction {
            val pacientes = Pacientes.selectAll().limit(10)
//            patientsList = pacientes.toList() as MutableList<Paciente>
            patientsList = Paciente.wrapRows(pacientes).toMutableList()
        }
        println("Ending getAllPatients()")

        patientsList.map { patientsListSerial.add(PacienteSerial(it)) }

        println("****************************************************************")

        return patientsListSerial
    }

    fun getSomePatients (token: String) : MutableList<PacienteSerial> {

        println("Starting getSomePatients()")

        var patientsList = mutableListOf<Paciente>()
        var patientsListSerial = mutableListOf<PacienteSerial>()

        // https://github.com/JetBrains/Exposed/wiki/DAO#read-entity-with-a-join-to-another-table
        transaction {

            val pacientes = Pacientes.select { Pacientes.apellido like token}
//            patientsList = pacientes.toList() as MutableList<Paciente>
            patientsList = Paciente.wrapRows(pacientes).toMutableList()
        }
        println("Ending getSomePatients()")

        patientsList.map { patientsListSerial.add(PacienteSerial(it)) }

        println("****************************************************************")

        return patientsListSerial
    }

    fun storePatient(patient: PacienteSerial) {

        transaction {
            Paciente.new {
                nombre = patient.nombre
                apellido = patient.apellido
                documento = patient.documento
                nacionalidad = patient.nacionalidad
                fechaNacimiento = LocalDate.parse(patient.fechaNacimiento, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                fechaCreacionFicha = LocalDate.parse(patient.fechaCreacionFicha, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }
        }

    }

    fun updatePatient(patient: PacienteSerial) {

        transaction {
            Pacientes.update ({ Pacientes.id eq patient.id }) {
                it[nombre] = patient.nombre
                it[apellido] = patient.apellido
                it[documento] = patient.documento
                it[nacionalidad] = patient.nacionalidad
                it[fechaNacimiento] = LocalDate.parse(patient.fechaNacimiento, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                it[fechaCreacionFicha] = LocalDate.parse(patient.fechaCreacionFicha, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            }
        }
    }
}