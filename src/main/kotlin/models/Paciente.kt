package models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object Pacientes : IntIdTable("PACIENTES")
{
    val nombre = varchar("nombre", 50)
    val apellido = varchar("apellido", 50)
    val documento = varchar("documento", 50)
    val nacionalidad = varchar("nacionalidad", 50)
    val fechaNacimiento = date("fechaNacimiento");
}


class Paciente(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Paciente>(Pacientes)

    var nombre          by Pacientes.nombre
    var apellido        by Pacientes.apellido
    var documento       by Pacientes.documento
    var nacionalidad    by Pacientes.nacionalidad
    var fechaNacimiento by Pacientes.fechaNacimiento
}

// No se si es la solución más bonita pero anda y, al menos, está todo en el mismo archivo
@Serializable
data class PacienteSerial (
    val id : Int,
    val nombre : String,
    val apellido : String,
    val documento : String,
    val nacionalidad : String,
    val fechaNacimiento : String,
) {
    constructor(paciente: Paciente) :
            this (
                paciente.id.value,
                paciente.nombre,
                paciente.apellido,
                paciente.documento,
                paciente.nacionalidad,
                paciente.fechaNacimiento.toString())
}