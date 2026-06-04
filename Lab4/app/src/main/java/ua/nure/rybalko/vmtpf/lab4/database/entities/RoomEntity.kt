package ua.nure.rybalko.vmtpf.lab4.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ua.nure.rybalko.vmtpf.lab4.database.tables.Rooms

class RoomEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RoomEntity>(Rooms)
    var hotel by HotelEntity referencedOn Rooms.hotelId
    var number by Rooms.number
    var type by Rooms.type
    var pricePerNight by Rooms.pricePerNight
    var capacity by Rooms.capacity
    var isAvailable by Rooms.isAvailable
}
