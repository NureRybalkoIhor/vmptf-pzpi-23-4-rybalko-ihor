package ua.nure.rybalko.vmtpf.lab4.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Hotels : IntIdTable("hotels") {
    val name = varchar("name", 100)
    val city = varchar("city", 100)
    val stars = integer("stars")
    val address = varchar("address", 200)
    val phone = varchar("phone", 50)
}
