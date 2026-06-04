package ua.nure.rybalko.vmtpf.lab4

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import org.jetbrains.exposed.sql.transactions.transaction
import ua.nure.rybalko.vmtpf.lab4.database.DatabaseFactory
import ua.nure.rybalko.vmtpf.lab4.database.entities.ClientEntity
import ua.nure.rybalko.vmtpf.lab4.database.entities.HotelEntity
import ua.nure.rybalko.vmtpf.lab4.database.entities.RoomEntity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread {
            DatabaseFactory.init(applicationContext)
            seedDatabase()
        }.start()

        findViewById<MaterialButton>(R.id.btnBookRooms).setOnClickListener {
            startActivity(Intent(this, BookingActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnCacheBenchmark).setOnClickListener {
            startActivity(Intent(this, CacheBenchmarkActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnSqlInjection).setOnClickListener {
            startActivity(Intent(this, SqlInjectionActivity::class.java))
        }
    }

    private fun seedDatabase() {
        transaction {
            if (HotelEntity.all().empty()) {
                val hotel = HotelEntity.new {
                    name = "Sea Breeze Palace"
                    city = "Odesa"
                    stars = 5
                    address = "Primorsky Boulevard 12"
                    phone = "+380481234567"
                }

                RoomEntity.new {
                    this.hotel = hotel
                    number = "101"
                    type = "Single"
                    pricePerNight = 1200.0
                    capacity = 1
                    isAvailable = true
                }
                RoomEntity.new {
                    this.hotel = hotel
                    number = "102"
                    type = "Double"
                    pricePerNight = 2200.0
                    capacity = 2
                    isAvailable = true
                }
                RoomEntity.new {
                    this.hotel = hotel
                    number = "103"
                    type = "Suite"
                    pricePerNight = 4500.0
                    capacity = 4
                    isAvailable = true
                }
            }

            if (ClientEntity.all().empty()) {
                ClientEntity.new {
                    firstName = "Ігор"
                    lastName = "Рибалко"
                    email = "user@store.com"
                    phone = "+380998765432"
                    passportNumber = "AB123456"
                }
            }
        }
    }
}
