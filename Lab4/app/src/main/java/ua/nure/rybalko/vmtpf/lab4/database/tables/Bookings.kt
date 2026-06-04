package ua.nure.rybalko.vmtpf.lab4.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object Bookings : IntIdTable("bookings") {
    val clientId = reference("client_id", Clients)
    val roomId = reference("room_id", Rooms)
    val checkIn = date("check_in")
    val checkOut = date("check_out")
    val totalPrice = double("total_price")
    val status = varchar("status", 30).default("PENDING")

    init {
        check("check_date") { checkOut greater checkIn }
    }
}
