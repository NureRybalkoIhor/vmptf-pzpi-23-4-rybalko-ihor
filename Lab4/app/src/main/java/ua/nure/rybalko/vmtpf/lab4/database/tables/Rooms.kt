package ua.nure.rybalko.vmtpf.lab4.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Rooms : IntIdTable("rooms") {
    val hotelId = reference("hotel_id", Hotels)
    val number = varchar("number", 50)
    val type = varchar("type", 50)
    val pricePerNight = double("price_per_night")
    val capacity = integer("capacity")
    val isAvailable = bool("is_available")
}
