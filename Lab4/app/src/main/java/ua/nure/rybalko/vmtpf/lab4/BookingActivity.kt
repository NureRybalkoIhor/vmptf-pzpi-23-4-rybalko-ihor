package ua.nure.rybalko.vmtpf.lab4

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import org.jetbrains.exposed.sql.transactions.transaction
import ua.nure.rybalko.vmtpf.lab4.database.entities.ClientEntity
import ua.nure.rybalko.vmtpf.lab4.database.entities.HotelEntity
import ua.nure.rybalko.vmtpf.lab4.repository.BookingRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

data class RoomItem(
    val id: Int,
    val number: String,
    val type: String,
    val price: Double,
    val capacity: Int
)

data class BookingItem(
    val id: Int,
    val roomNumber: String,
    val checkIn: String,
    val checkOut: String,
    val totalPrice: Double
)

class BookingActivity : AppCompatActivity() {

    private lateinit var tvCheckIn: TextView
    private lateinit var tvCheckOut: TextView
    private lateinit var rvAvailable: RecyclerView
    private lateinit var rvActive: RecyclerView

    private var checkInDate: LocalDate = LocalDate.now()
    private var checkOutDate: LocalDate = LocalDate.now().plusDays(3)

    private var hotelId: Int = 1
    private var clientId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        tvCheckIn = findViewById(R.id.tvCheckIn)
        tvCheckOut = findViewById(R.id.tvCheckOut)
        rvAvailable = findViewById(R.id.rvAvailableRooms)
        rvActive = findViewById(R.id.rvActiveBookings)

        rvAvailable.layoutManager = LinearLayoutManager(this)
        rvActive.layoutManager = LinearLayoutManager(this)

        updateDateViews()

        tvCheckIn.setOnClickListener { showDatePicker { date ->
            checkInDate = date
            updateDateViews()
        }}

        tvCheckOut.setOnClickListener { showDatePicker { date ->
            checkOutDate = date
            updateDateViews()
        }}

        findViewById<MaterialButton>(R.id.btnSearchRooms).setOnClickListener {
            loadAvailableRooms()
        }

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
            loadAvailableRooms()
            loadActiveBookings()
        }.start()
    }

    private fun updateDateViews() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        tvCheckIn.text = checkInDate.format(formatter)
        tvCheckOut.text = checkOutDate.format(formatter)
    }

    private fun showDatePicker(onDateSelected: (LocalDate) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selected = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(selected)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun loadAvailableRooms() {
        if (checkOutDate.isBefore(checkInDate.plusDays(1))) {
            runOnUiThread {
                Toast.makeText(this, "Check Out must be at least 1 day after Check In", Toast.LENGTH_SHORT).show()
            }
            return
        }

        Thread {
            val rooms = transaction {
                BookingRepository.getAvailableRooms(hotelId, checkInDate, checkOutDate).map {
                    RoomItem(it.id.value, it.number, it.type, it.pricePerNight, it.capacity)
                }
            }
            runOnUiThread {
                rvAvailable.adapter = AvailableRoomsAdapter(rooms) { room ->
                    bookRoom(room)
                }
            }
        }.start()
    }

    private fun loadActiveBookings() {
        Thread {
            val bookings = transaction {
                BookingRepository.getClientBookings(clientId)
                    .filter { it.status != "CANCELLED" }
                    .map {
                        BookingItem(it.id.value, it.room.number, it.checkIn.toString(), it.checkOut.toString(), it.totalPrice)
                    }
            }
            runOnUiThread {
                rvActive.adapter = ActiveBookingsAdapter(bookings) { booking ->
                    cancelBooking(booking)
                }
            }
        }.start()
    }

    private fun bookRoom(room: RoomItem) {
        Thread {
            try {
                BookingRepository.addBooking(clientId, room.id, checkInDate, checkOutDate)
                runOnUiThread {
                    Toast.makeText(this, "Booking Successful!", Toast.LENGTH_SHORT).show()
                }
                loadAvailableRooms()
                loadActiveBookings()
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun cancelBooking(booking: BookingItem) {
        Thread {
            val success = BookingRepository.cancelBooking(booking.id)
            if (success) {
                runOnUiThread {
                    Toast.makeText(this, "Booking Cancelled", Toast.LENGTH_SHORT).show()
                }
                loadAvailableRooms()
                loadActiveBookings()
            }
        }.start()
    }
}

class AvailableRoomsAdapter(
    private val items: List<RoomItem>,
    private val onBookClicked: (RoomItem) -> Unit
) : RecyclerView.Adapter<AvailableRoomsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRoomNum: TextView = view.findViewById(R.id.tvRoomNum)
        val tvRoomType: TextView = view.findViewById(R.id.tvRoomType)
        val tvRoomCapacity: TextView = view.findViewById(R.id.tvRoomCapacity)
        val tvRoomPrice: TextView = view.findViewById(R.id.tvRoomPrice)
        val btnBook: MaterialButton = view.findViewById(R.id.btnBookRoomAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hotel_room, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvRoomNum.text = "Room ${item.number}"
        holder.tvRoomType.text = item.type
        holder.tvRoomCapacity.text = "Capacity: ${item.capacity} people"
        holder.tvRoomPrice.text = "${item.price} UAH/night"
        holder.btnBook.setOnClickListener { onBookClicked(item) }
    }

    override fun getItemCount(): Int = items.size
}

class ActiveBookingsAdapter(
    private val items: List<BookingItem>,
    private val onCancelClicked: (BookingItem) -> Unit
) : RecyclerView.Adapter<ActiveBookingsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBookingHeader: TextView = view.findViewById(R.id.tvBookingHeader)
        val tvBookingPeriod: TextView = view.findViewById(R.id.tvBookingPeriod)
        val tvBookingCost: TextView = view.findViewById(R.id.tvBookingCost)
        val btnCancel: MaterialButton = view.findViewById(R.id.btnCancelBookingAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_active_booking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvBookingHeader.text = "Booking #${item.id}: Room ${item.roomNumber}"
        holder.tvBookingPeriod.text = "${item.checkIn} to ${item.checkOut}"
        holder.tvBookingCost.text = "Total Price: ${item.totalPrice} UAH"
        holder.btnCancel.setOnClickListener { onCancelClicked(item) }
    }

    override fun getItemCount(): Int = items.size
}
