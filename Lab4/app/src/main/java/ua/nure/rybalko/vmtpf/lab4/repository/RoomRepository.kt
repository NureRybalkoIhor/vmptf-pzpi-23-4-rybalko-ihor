package ua.nure.rybalko.vmtpf.lab4.repository

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import ua.nure.rybalko.vmtpf.lab4.database.entities.RoomEntity
import ua.nure.rybalko.vmtpf.lab4.database.tables.Rooms

object RoomRepository {
    fun getRoomsByHotel(hotelId: Int): List<RoomEntity> = transaction {
        RoomEntity.find { Rooms.hotelId eq hotelId }.toList()
    }

    fun getRoomsByType(hotelId: Int, type: String): List<RoomEntity> = transaction {
        RoomEntity.find { (Rooms.hotelId eq hotelId) and (Rooms.type eq type) }.toList()
    }

    fun updateAvailability(roomId: Int, available: Boolean) = transaction {
        val room = RoomEntity.findById(roomId)
        room?.isAvailable = available
    }
}
