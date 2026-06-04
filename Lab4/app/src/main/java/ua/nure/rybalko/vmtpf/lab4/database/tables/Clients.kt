package ua.nure.rybalko.vmtpf.lab4.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Clients : IntIdTable("clients") {
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val email = varchar("email", 150).uniqueIndex()
    val phone = varchar("phone", 50)
    val passportNumber = varchar("passport_number", 50)
}
