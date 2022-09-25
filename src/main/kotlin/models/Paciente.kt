package models

//import com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

// https://medium.com/nerd-for-tech/an-opinionated-kotlin-backend-service-database-migration-orm-52527ce3228

//  Table
object Pacientes : IntIdTable("PACIENTES")
{
    val nombre = varchar("nombre", 30)
    val apellido = varchar("apellido", 30)
    val documento = varchar("documento", 25).nullable()
    val nacionalidad = varchar("nacionalidad", 25).nullable()
    val fechaNacimiento = date("fechaNacimiento").nullable()
    val fechaCreacionFicha = date("fecha_creacion_ficha").nullable()
    val sexo = char("sexo").nullable()
    val diagnosticoPrenatal = char("diagnostico_prenatal").nullable()
    val pacienteFallecido = char("paciente_fallecido").nullable()
    val semanasGestacion = integer("semanas_gestacion").nullable()
    val diag1 = varchar("diag1", 140).nullable()
    val diag2 = varchar("diag2", 140).nullable()
    val diag3 = varchar("diag3", 140).nullable()
    val diag4 = varchar("diag4", 140).nullable()
    val fechaPrimerDiagnostico = date("fecha_primer_diagnostico").nullable()
    val nroHistClinicaPapel = varchar("nro_hist_clinica_papel", 20).nullable()
    val nroFichaDiagPrenatal = varchar("nro_ficha_diag_prenatal", 20).nullable()
    val comentarios = varchar("comentarios", 500).nullable()
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
    var sexo by Pacientes.sexo
    var diagnosticoPrenatal by Pacientes.diagnosticoPrenatal
    var pacienteFallecido by Pacientes.pacienteFallecido
    var semanasGestacion by Pacientes.semanasGestacion
    var diag1 by Pacientes.diag1
    var diag2 by Pacientes.diag2
    var diag3 by Pacientes.diag3
    var diag4 by Pacientes.diag4
    var fechaPrimerDiagnostico by Pacientes.fechaPrimerDiagnostico
    var nroHistClinicaPapel by Pacientes.nroHistClinicaPapel
    var nroFichaDiagPrenatal by Pacientes.nroFichaDiagPrenatal
    var comentarios by Pacientes.comentarios
}

// No sé si es la solución más bonita pero anda y, al menos, está todo en el mismo archivo
// Esto vendría a ser un DTO
// Esto es para resolver los distintos formatos de fecha: date en la BD y string en http
@Serializable
data class PacienteSerial (
    val id : Int,
    val nombre : String,
    val apellido : String,
    val documento : String?,
    val nacionalidad : String?,
    val fechaNacimiento : String?,
    val fechaCreacionFicha : String?,
    val sexo : Char?,
    val diagnosticoPrenatal : Char?,
    val pacienteFallecido : Char?,
    val semanasGestacion : Int?,
    val diag1 : String?,
    val diag2 : String?,
    val diag3 : String?,
    val diag4 : String?,
    val fechaPrimerDiagnostico : String?,
    val nroHistClinicaPapel : String?,
    val nroFichaDiagPrenatal : String?,
    val comentarios : String?,
) {
    constructor(paciente: Paciente) :
            this (
                paciente.id.value,
                paciente.nombre,
                paciente.apellido,
                paciente.documento,
                paciente.nacionalidad,
                paciente.fechaNacimiento.toString(),
                paciente.fechaCreacionFicha.toString(),
                paciente.sexo,
                paciente.diagnosticoPrenatal,
                paciente.pacienteFallecido,
                paciente.semanasGestacion,
                paciente.diag1,
                paciente.diag2,
                paciente.diag3,
                paciente.diag4,
                paciente.fechaPrimerDiagnostico.toString(),
                paciente.nroHistClinicaPapel,
                paciente.nroFichaDiagPrenatal,
                paciente.comentarios)
}