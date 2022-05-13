package models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

// https://medium.com/nerd-for-tech/an-opinionated-kotlin-backend-service-database-migration-orm-52527ce3228

//  Table
object Pacientes : IntIdTable("PACIENTES")
{
    val nombre = varchar("nombre", 50)
    val apellido = varchar("apellido", 50)
    val documento = varchar("documento", 50)
    val nacionalidad = varchar("nacionalidad", 50)
    val fechaNacimiento = date("fechaNacimiento");
    val fechaCreacionFicha = date("fecha_creacion_ficha");
}

// Entity
class Paciente(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Paciente>(Pacientes)

    var nombre          by Pacientes.nombre
    var apellido        by Pacientes.apellido
    var documento       by Pacientes.documento
    var nacionalidad    by Pacientes.nacionalidad
    var fechaNacimiento by Pacientes.fechaNacimiento
    var fechaCreacionFicha by Pacientes.fechaCreacionFicha
}

// No sé si es la solución más bonita pero anda y, al menos, está todo en el mismo archivo
// Esto vendría a ser un DTO
// Esto es para resolver los distintos formatos de fecha: date en la BD y string en http
@Serializable
data class PacienteSerial (
    val id : Int,
    val nombre : String,
    val apellido : String,
    val documento : String,
    val nacionalidad : String,
    val fechaNacimiento : String,
    val fechaCreacionFicha : String,
) {
    constructor(paciente: Paciente) :
            this (
                paciente.id.value,
                paciente.nombre,
                paciente.apellido,
                paciente.documento,
                paciente.nacionalidad,
                paciente.fechaNacimiento.toString(),
                paciente.fechaCreacionFicha.toString())
}