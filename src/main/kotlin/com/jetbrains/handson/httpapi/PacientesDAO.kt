package com.jetbrains.handson.httpapi

import models.Paciente
import models.PacienteSerial
import models.Pacientes
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// https://github.com/JetBrains/Exposed/wiki/DAO#read-entity-with-a-join-to-another-table

// Entity.new { } to insert new entities
class PacientesDAO {

    fun getAllPatients(): MutableList<PacienteSerial> {

        println("Starting getAllPatients()")

        var patientsList = mutableListOf<Paciente>()
        val patientsListSerial = mutableListOf<PacienteSerial>()

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

    fun getPatientsByLastName(token: String): MutableList<PacienteSerial> {

        println("Starting getSomePatients()")

        var patientsList = mutableListOf<Paciente>()
        val patientsListSerial = mutableListOf<PacienteSerial>()

        // https://github.com/JetBrains/Exposed/wiki/DAO#read-entity-with-a-join-to-another-table
        transaction {

            val pacientes = Pacientes.select { Pacientes.apellido like token }
//            patientsList = pacientes.toList() as MutableList<Paciente>
            patientsList = Paciente.wrapRows(pacientes).toMutableList()
        }
        println("Ending getSomePatients()")

        patientsList.map { patientsListSerial.add(PacienteSerial(it)) }

        println("****************************************************************")

        return patientsListSerial
    }

    fun getPatientsById(token: String): MutableList<PacienteSerial> {

        println("Starting getPatientsById()")

        var patientsList = mutableListOf<Paciente>()
        val patientsListSerial = mutableListOf<PacienteSerial>()

        // https://github.com/JetBrains/Exposed/wiki/DAO#read-entity-with-a-join-to-another-table
        transaction {

            val pacientes = Pacientes.select { Pacientes.documento like token }
//            patientsList = pacientes.toList() as MutableList<Paciente>
            patientsList = Paciente.wrapRows(pacientes).toMutableList()
        }
        println("Ending getSomePatients()")

        patientsList.map { patientsListSerial.add(PacienteSerial(it)) }

        println("****************************************************************")

        return patientsListSerial
    }

    fun checkIfPatientByCountryAndId(country: String?, idAlpha: String?): Boolean {

        println("Starting checkIfPatientByCountryAndId()")

        var patientExists = true

        // https://github.com/JetBrains/Exposed/wiki/DAO#read-entity-with-a-join-to-another-table
        transaction {

            patientExists = !Pacientes.select { (Pacientes.documento eq idAlpha) and (Pacientes.nacionalidad eq country) }.empty()

            println("El paciente ya existe: $patientExists")
        }
        println("Ending checkIfPatientByCountryAndId()")

        println("****************************************************************")

        return patientExists
    }

    fun storePatient(patient: PacienteSerial): Int {

        var numeroPacienteCreado = 0

        transaction {
            val newPatient = Paciente.new {
                nombre = patient.nombre
                apellido = patient.apellido
                documento = patient.documento
                nacionalidad = patient.nacionalidad
//                fechaNacimiento = LocalDate.parse(patient.fechaNacimiento, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                fechaNacimiento = if (patient.fechaNacimiento != null) LocalDate.parse(
                    patient.fechaNacimiento,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                ) else null
                fechaCreacionFicha =
                    LocalDate.parse(patient.fechaCreacionFicha, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                sexo = patient.sexo
                diagnosticoPrenatal = patient.diagnosticoPrenatal
                pacienteFallecido = patient.pacienteFallecido
                semanasGestacion = patient.semanasGestacion
                diag1 = patient.diag1
                diag2 = patient.diag2
                diag3 = patient.diag3
                diag4 = patient.diag4
                fechaPrimerDiagnostico = if (patient.fechaPrimerDiagnostico != null) LocalDate.parse(
                    patient.fechaPrimerDiagnostico,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                ) else null
                nroHistClinicaPapel = patient.nroHistClinicaPapel
                nroFichaDiagPrenatal = patient.nroFichaDiagPrenatal
                comentarios = patient.comentarios
            }

            numeroPacienteCreado = newPatient.id.value
        }

        return numeroPacienteCreado
    }

    fun updatePatient(patient: PacienteSerial): Int {

        var numeroPacienteModificado = 0

        transaction {
            numeroPacienteModificado = Pacientes.update({ Pacientes.id eq patient.id }) {
                it[nombre] = patient.nombre
                it[apellido] = patient.apellido
                it[documento] = patient.documento
                it[nacionalidad] = patient.nacionalidad
                it[fechaNacimiento] = if (patient.fechaNacimiento != null) LocalDate.parse(
                    patient.fechaNacimiento,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                ) else null
                it[fechaCreacionFicha] =
                    LocalDate.parse(patient.fechaCreacionFicha, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                it[diagnosticoPrenatal] = patient.diagnosticoPrenatal
                it[sexo] = patient.sexo
                it[pacienteFallecido] = patient.pacienteFallecido
                it[semanasGestacion] = patient.semanasGestacion
                it[diag1] = patient.diag1
                it[diag2] = patient.diag2
                it[diag3] = patient.diag3
                it[diag4] = patient.diag4
                it[fechaPrimerDiagnostico] = if (patient.fechaPrimerDiagnostico != null) LocalDate.parse(
                    patient.fechaPrimerDiagnostico,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                ) else null
                it[nroHistClinicaPapel] = patient.nroHistClinicaPapel
                it[nroFichaDiagPrenatal] = patient.nroFichaDiagPrenatal
                it[comentarios] = patient.comentarios
            }
        }

        if (numeroPacienteModificado == 1)
            return patient.id
        else
            return 0
    }
}