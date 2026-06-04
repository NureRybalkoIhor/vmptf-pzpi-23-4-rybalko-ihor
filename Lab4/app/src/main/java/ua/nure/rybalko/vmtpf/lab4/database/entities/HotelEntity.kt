package ua.nure.rybalko.vmtpf.lab4.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ua.nure.rybalko.vmtpf.lab4.database.tables.Hotels

class HotelEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<HotelEntity>(Hotels)
    var name by Hotels.name
    var city by Hotels.city
    var stars by Hotels.stars
    var address by Hotels.address
    var phone by Hotels.phone
}
