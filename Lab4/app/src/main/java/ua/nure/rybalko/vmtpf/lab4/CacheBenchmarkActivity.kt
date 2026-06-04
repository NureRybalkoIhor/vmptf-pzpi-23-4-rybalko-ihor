package ua.nure.rybalko.vmtpf.lab4

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import org.jetbrains.exposed.sql.transactions.transaction
import ua.nure.rybalko.vmtpf.lab4.cache.AvailableRoomsKey
import ua.nure.rybalko.vmtpf.lab4.cache.CacheService
import ua.nure.rybalko.vmtpf.lab4.database.entities.ClientEntity
import ua.nure.rybalko.vmtpf.lab4.database.entities.HotelEntity
import ua.nure.rybalko.vmtpf.lab4.database.entities.RoomEntity
import ua.nure.rybalko.vmtpf.lab4.repository.BookingRepository
import java.time.LocalDate

class CacheBenchmarkActivity : AppCompatActivity() {

    private lateinit var tvLogs: TextView
    private var hotelId: Int = 1
    private var clientId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cache_benchmark)

        tvLogs = findViewById(R.id.tvBenchmarkLogs)

        Thread {
            transaction {
                val firstHotel = HotelEntity.all().firstOrNull()
                if (firstHotel != null) {
                    hotelId = firstHotel.id.value
                }
                val firstClient = ClientEntity.all().firstOrNull()
                if (firstClient != null) {
                    clientId = firstClient.id.value
                }
            }
        }.start()

        findViewById<MaterialButton>(R.id.btnRunBenchmark).setOnClickListener {
            runBenchmark()
        }
    }

    private fun runBenchmark() {
        tvLogs.text = "Running Caching Speed Benchmark...\n"

        Thread {
            val checkIn = LocalDate.now()
            val checkOut = checkIn.plusDays(3)
            val cacheKey = AvailableRoomsKey(hotelId, checkIn, checkOut)

            val rooms = transaction { RoomEntity.find { RoomEntity.table.id.isNotNull() }.toList() }
            if (rooms.isEmpty()) {
                runOnUiThread {
                    tvLogs.append("\nError: No rooms seeded in the database. Please seed rooms first.\n")
                }
                return@Thread
            }
            val roomToBook = rooms[0].id.value

            val startDbTime = System.nanoTime()
            CacheService.availableRoomsCache.get(cacheKey)
            val endDbTime = System.nanoTime()
            val dbDuration = (endDbTime - startDbTime) / 1_000_000.0

            val startCacheTime = System.nanoTime()
            CacheService.availableRoomsCache.get(cacheKey)
            val endCacheTime = System.nanoTime()
            val cacheDuration = (endCacheTime - startCacheTime) / 1_000_000.0

            val newBooking = BookingRepository.addBooking(clientId, roomToBook, checkIn, checkOut)
            val bId = transaction { newBooking.id.value }

            BookingRepository.cancelBooking(bId)
            CacheService.invalidateAvailableRooms(hotelId)

            val startInvalidatedTime = System.nanoTime()
            CacheService.availableRoomsCache.get(cacheKey)
            val endInvalidatedTime = System.nanoTime()
            val invalidatedDuration = (endInvalidatedTime - startInvalidatedTime) / 1_000_000.0

            val speedup = if (cacheDuration > 0) String.format("%.2f", dbDuration / cacheDuration) else "N/A"

            runOnUiThread {
                tvLogs.append("\n==================================\n")
                tvLogs.append("BENCHMARK TIMINGS RESULTS:\n")
                tvLogs.append("==================================\n")
                tvLogs.append(String.format("Query 1 (DB hit): %.4f ms\n", dbDuration))
                tvLogs.append(String.format("Query 2 (Cache hit): %.4f ms\n", cacheDuration))
                tvLogs.append(String.format("Caching Speedup Factor: %s x\n\n", speedup))
                tvLogs.append("Cancelling booking & invalidating cache...\n")
                tvLogs.append(String.format("Query 3 (DB hit post-invalidate): %.4f ms\n", invalidatedDuration))
                tvLogs.append("==================================\n")
            }
        }.start()
    }
}
