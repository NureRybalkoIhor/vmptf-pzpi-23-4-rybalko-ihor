package ua.nure.rybalko.vmtpf.lab4.database

import android.content.Context
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ua.nure.rybalko.vmtpf.lab4.database.tables.Bookings
import ua.nure.rybalko.vmtpf.lab4.database.tables.Clients
import ua.nure.rybalko.vmtpf.lab4.database.tables.Hotels
import ua.nure.rybalko.vmtpf.lab4.database.tables.Rooms
import ua.nure.rybalko.vmtpf.lab4.database.tables.Services
import java.io.File

object DatabaseFactory {
    fun init(context: Context) {
        val dbFile = File(context.filesDir, "hotel_booking")
        Database.connect(
            url = "jdbc:h2:file:${dbFile.absolutePath};DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver"
        )
        transaction {
            SchemaUtils.drop(Bookings, Rooms, Hotels, Clients, Services)
            SchemaUtils.create(Hotels, Rooms, Clients, Services, Bookings)
        }
    }
}
