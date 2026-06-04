package ua.nure.rybalko.vmtpf.lab4.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.jetbrains.exposed.sql.transactions.transaction
import ua.nure.rybalko.vmtpf.lab4.database.entities.ClientEntity
import ua.nure.rybalko.vmtpf.lab4.database.entities.HotelEntity
import ua.nure.rybalko.vmtpf.lab4.repository.BookingRepository
import java.time.LocalDate
import java.util.concurrent.TimeUnit

data class HotelDto(
    val id: Int,
    val name: String,
    val city: String,
    val stars: Int,
    val address: String,
    val phone: String
)

data class ClientDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val passportNumber: String
)

data class RoomDto(
    val id: Int,
    val hotelId: Int,
    val number: String,
    val type: String,
    val pricePerNight: Double,
    val capacity: Int,
    val isAvailable: Boolean
)

data class AvailableRoomsKey(
    val hotelId: Int,
    val checkIn: LocalDate,
    val checkOut: LocalDate
)

object CacheService {
    val hotelCache: LoadingCache<Int, HotelDto> = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .maximumSize(100)
        .build { id ->
            transaction {
                val entity = HotelEntity.findById(id) ?: throw IllegalArgumentException("Hotel not found")
                HotelDto(entity.id.value, entity.name, entity.city, entity.stars, entity.address, entity.phone)
            }
        }

    val clientCache: LoadingCache<Int, ClientDto> = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .maximumSize(100)
        .build { id ->
            transaction {
                val entity = ClientEntity.findById(id) ?: throw IllegalArgumentException("Client not found")
                ClientDto(entity.id.value, entity.firstName, entity.lastName, entity.email, entity.phone, entity.passportNumber)
            }
        }

    val availableRoomsCache: LoadingCache<AvailableRoomsKey, List<RoomDto>> = Caffeine.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .maximumSize(100)
        .build { key ->
            transaction {
                BookingRepository.getAvailableRooms(key.hotelId, key.checkIn, key.checkOut).map { entity ->
                    RoomDto(
                        entity.id.value,
                        entity.hotel.id.value,
                        entity.number,
                        entity.type,
                        entity.pricePerNight,
                        entity.capacity,
                        entity.isAvailable
                    )
                }
            }
        }

    fun invalidateAvailableRooms(hotelId: Int) {
        val keysToRemove = availableRoomsCache.asMap().keys.filter { it.hotelId == hotelId }
        keysToRemove.forEach { availableRoomsCache.invalidate(it) }
    }
}
