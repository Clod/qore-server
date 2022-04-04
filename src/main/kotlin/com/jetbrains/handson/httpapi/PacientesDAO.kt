package com.jetbrains.handson.httpapi

import models.Paciente
import models.PacienteSerial
import models.Pacientes
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PacientesDAO {

    fun getAllPatients () : MutableList<PacienteSerial> {

        println("Starting getAllPatients()")

        var patientsList = mutableListOf<Paciente>()
        var patientsListSerial = mutableListOf<PacienteSerial>()

        // https://github.com/JetBrains/Exposed/wiki/DAO#read-entity-with-a-join-to-another-table
        transaction {
            val pacientes = Pacientes.selectAll()
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

            val pacientes = Pacientes.select { Pacientes.nacionalidad like token }
//            patientsList = pacientes.toList() as MutableList<Paciente>
            patientsList = Paciente.wrapRows(pacientes).toMutableList()
        }
        println("Ending getSomePatients()")

        patientsList.map { patientsListSerial.add(PacienteSerial(it)) }

        println("****************************************************************")

        return patientsListSerial
    }
}