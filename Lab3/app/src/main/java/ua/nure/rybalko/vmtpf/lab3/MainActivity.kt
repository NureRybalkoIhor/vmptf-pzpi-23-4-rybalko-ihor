package ua.nure.rybalko.vmtpf.lab3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var rvProducts: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var tvCartBadge: TextView
    private var activeCategory = "Всі"

    private val allProducts = listOf(
        Product("1", "Бездротові навушники Aura Pro", 3200.0, "Електроніка", "Навушники з активним шумозаглушенням, глибоким басом та автономністю до 30 годин.", "aura_pro"),
        Product("2", "Смарт-годинник Chronos", 4800.0, "Електроніка", "Стильний смарт-годинник з AMOLED дисплеєм, пульсометром та вологозахистом IP68.", "chronos"),
        Product("3", "Механічна клавіатура KeyClick", 2600.0, "Аксесуари", "Ігрова механічна клавіатура з RGB підсвіткою та перемикачами Cherry MX Red.", "keyclick"),
        Product("4", "Ергономічна мишка Glide", 1500.0, "Аксесуари", "Бездротова вертикальна мишка для тривалої роботи за комп'ютером без втоми.", "glide"),
        Product("5", "Шкіряний рюкзак CityPack", 3500.0, "Стиль життя", "Місткий міський рюкзак із натуральної шкіри з відділенням для ноутбука до 15.6 дюймів.", "citypack"),
        Product("6", "Спортивна пляшка Aqua", 650.0, "Стиль життя", "Екологічна термопляшка з нержавіючої сталі, яка зберігає температуру до 24 годин.", "aqua"),
        Product("7", "Бездротова зарядка Orbit", 1200.0, "Електроніка", "Швидка магнітна бездротова зарядна станція 3-в-1 для телефону, годинника та навушників.", "orbit"),
        Product("8", "Блокнот у шкіряній обкладинці", 800.0, "Стиль життя", "Преміальний записник формату А5 зі змінними блоками та екологічним папером.", "notebook")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SessionManager.init(this)
        CartManager.init(this)

        tvCartBadge = findViewById(R.id.tvCartBadge)
        rvProducts = findViewById(R.id.rvProducts)
        rvProducts.layoutManager = LinearLayoutManager(this)

        setupAdapter()

        val rgCategories = findViewById<RadioGroup>(R.id.rgCategories)
        rgCategories.setOnCheckedChangeListener { _, checkedId ->
            activeCategory = when (checkedId) {
                R.id.rbElectronics -> "Електроніка"
                R.id.rbAccessories -> "Аксесуари"
                R.id.rbLifestyle -> "Стиль життя"
                else -> "Всі"
            }
            filterProducts()
        }

        findViewById<View>(R.id.btnCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        findViewById<View>(R.id.btnFavorites).setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        findViewById<View>(R.id.btnRecommendations).setOnClickListener {
            val user = SessionManager.getCurrentUser()
            if (user == null) {
                Toast.makeText(this, "Будь ласка, увійдіть, щоб переглянути рекомендації", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, RecommendationsActivity::class.java))
            }
        }

        findViewById<View>(R.id.btnProfile).setOnClickListener {
            showProfileMenu()
        }
    }

    override fun onResume() {
        super.onResume()
        updateFavoritesFromPrefs()
        filterProducts()
        updateCartBadge()
    }

    private fun setupAdapter() {
        updateFavoritesFromPrefs()
        adapter = ProductAdapter(
            getFilteredList(),
            onFavoriteToggled = { product ->
                saveFavoriteState(product.id, product.isFavorite)
            },
            onCartUpdated = {
                updateCartBadge()
            }
        )
        rvProducts.adapter = adapter
    }

    private fun updateFavoritesFromPrefs() {
        val favs = getSharedPreferences("fav_prefs", Context.MODE_PRIVATE)
            .getStringSet("fav_ids", emptySet()) ?: emptySet()
        allProducts.forEach { product ->
            product.isFavorite = favs.contains(product.id)
        }
    }

    private fun saveFavoriteState(productId: String, isFav: Boolean) {
        val prefs = getSharedPreferences("fav_prefs", Context.MODE_PRIVATE)
        val favs = prefs.getStringSet("fav_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
        if (isFav) {
            favs.add(productId)
        } else {
            favs.remove(productId)
        }
        prefs.edit().putStringSet("fav_ids", favs).apply()
    }

    private fun filterProducts() {
        adapter.updateList(getFilteredList())
    }

    private fun getFilteredList(): List<Product> {
        return if (activeCategory == "Всі") {
            allProducts
        } else {
            allProducts.filter { it.category == activeCategory }
        }
    }

    private fun updateCartBadge() {
        val count = CartManager.getItemsCount()
        if (count > 0) {
            tvCartBadge.text = count.toString()
            tvCartBadge.visibility = View.VISIBLE
        } else {
            tvCartBadge.visibility = View.GONE
        }
    }

    private fun showProfileMenu() {
        val user = SessionManager.getCurrentUser()
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            val options = if (user.isAdmin) {
                arrayOf("Панель адміністратора", "Вийти з акаунту")
            } else {
                arrayOf("Мої замовлення", "Вийти з акаунту")
            }

            AlertDialog.Builder(this)
                .setTitle(user.name)
                .setItems(options) { _, which ->
                    if (which == 0) {
                        if (user.isAdmin) {
                            startActivity(Intent(this, AdminActivity::class.java))
                        } else {
                            startActivity(Intent(this, OrdersActivity::class.java))
                        }
                    } else {
                        SessionManager.logout()
                        Toast.makeText(this, "Ви вийшли з акаунту", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Закрити", null)
                .show()
        }
    }
}
