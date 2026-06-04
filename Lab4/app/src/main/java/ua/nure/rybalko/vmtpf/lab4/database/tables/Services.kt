package ua.nure.rybalko.vmtpf.lab4.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Services : IntIdTable("services") {
    val name = varchar("name", 100)
    val description = varchar("description", 200)
    val price = double("price")
}
