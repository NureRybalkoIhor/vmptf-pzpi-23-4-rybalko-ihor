package ua.nure.rybalko.vmtpf.lab4.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ua.nure.rybalko.vmtpf.lab4.database.tables.Services

class ServiceEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ServiceEntity>(Services)
    var name by Services.name
    var description by Services.description
    var price by Services.price
}
