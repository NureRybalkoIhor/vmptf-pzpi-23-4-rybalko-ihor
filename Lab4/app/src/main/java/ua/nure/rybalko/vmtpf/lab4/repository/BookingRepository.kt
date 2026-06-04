package ua.nure.rybalko.vmtpf.lab4.repository

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import ua.nure.rybalko.vmtpf.lab4.database.entities.BookingEntity
import ua.nure.rybalko.vmtpf.lab4.database.entities.ClientEntity
import ua.nure.rybalko.vmtpf.lab4.database.entities.RoomEntity
import ua.nure.rybalko.vmtpf.lab4.database.tables.Bookings
import ua.nure.rybalko.vmtpf.lab4.database.tables.Rooms
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object BookingRepository {
    fun isRoomAvailable(roomId: Int, checkInDate: LocalDate, checkOutDate: LocalDate): Boolean = transaction {
        val overlapping = BookingEntity.find {
            (Bookings.roomId eq roomId) and (Bookings.status neq "CANCELLED")
        }.any {
            checkInDate.isBefore(it.checkOut) && checkOutDate.isAfter(it.checkIn)
        }
        !overlapping
    }

    fun addBooking(clientId: Int, roomId: Int, checkInDate: LocalDate, checkOutDate: LocalDate): BookingEntity = transaction {
        require(!checkOutDate.isBefore(checkInDate.plusDays(1))) {
            "checkOut must be at least 1 day after checkIn"
        }
        val client = ClientEntity.findById(clientId) ?: throw IllegalArgumentException("Client not found")
        val room = RoomEntity.findById(roomId) ?: throw IllegalArgumentException("Room not found")
        require(isRoomAvailable(roomId, checkInDate, checkOutDate)) {
            "Room is already booked for these dates"
        }
        val nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate)
        val totalPrice = room.pricePerNight * nights
        BookingEntity.new {
            this.client = client
            this.room = room
            this.checkIn = checkInDate
            this.checkOut = checkOutDate
            this.totalPrice = totalPrice
            this.status = "CONFIRMED"
        }
    }

    fun cancelBooking(bookingId: Int): Boolean = transaction {
        val booking = BookingEntity.findById(bookingId) ?: return@transaction false
        booking.status = "CANCELLED"
        true
    }

    fun getAvailableRooms(hotelId: Int, checkInDate: LocalDate, checkOutDate: LocalDate): List<RoomEntity> = transaction {
        val allRooms = RoomEntity.find { Rooms.hotelId eq hotelId }.toList()
        allRooms.filter { isRoomAvailable(it.id.value, checkInDate, checkOutDate) }
    }

    fun getClientBookings(clientId: Int): List<BookingEntity> = transaction {
        BookingEntity.find { Bookings.clientId eq clientId }.toList()
    }
}
