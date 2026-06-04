package ua.nure.rybalko.vmtpf.lab4.database.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ua.nure.rybalko.vmtpf.lab4.database.tables.Clients

class ClientEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ClientEntity>(Clients)
    var firstName by Clients.firstName
    var lastName by Clients.lastName
    var email by Clients.email
    var phone by Clients.phone
    var passportNumber by Clients.passportNumber
}
