package ua.nure.rybalko.vmtpf.lab4.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ua.nure.rybalko.vmtpf.lab4.database.tables.Bookings

class BookingEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BookingEntity>(Bookings)
    var client by ClientEntity referencedOn Bookings.clientId
    var room by RoomEntity referencedOn Bookings.roomId
    var checkIn by Bookings.checkIn
    var checkOut by Bookings.checkOut
    var totalPrice by Bookings.totalPrice
    var status by Bookings.status
}
