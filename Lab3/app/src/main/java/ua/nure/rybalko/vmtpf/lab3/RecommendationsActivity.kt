package ua.nure.rybalko.vmtpf.lab3

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecommendationsActivity : AppCompatActivity() {

    private lateinit var rvRecommendations: RecyclerView
    private lateinit var tvSubtitle: TextView
    private lateinit var adapter: ProductAdapter

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
        setContentView(R.layout.activity_recommendations)

        val user = SessionManager.getCurrentUser()
        if (user == null) {
            Toast.makeText(this, "Увійдіть для перегляду рекомендацій", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        rvRecommendations = findViewById(R.id.rvRecommendations)
        tvSubtitle = findViewById(R.id.tvRecommendationsSubtitle)

        rvRecommendations.layoutManager = LinearLayoutManager(this)

        setupRecommendations(user.id)
    }

    private fun setupRecommendations(userId: String) {
        val recPrefs = getSharedPreferences("rec_prefs", Context.MODE_PRIVATE)
        val purchasedCats = recPrefs.getStringSet("user_${userId}_cats", emptySet()) ?: emptySet()

        val favPrefs = getSharedPreferences("fav_prefs", Context.MODE_PRIVATE)
        val favs = favPrefs.getStringSet("fav_ids", emptySet()) ?: emptySet()
        allProducts.forEach { product ->
            product.isFavorite = favs.contains(product.id)
        }

        val recommendedProducts: List<Product>
        if (purchasedCats.isNotEmpty()) {
            tvSubtitle.text = "Ці товари підібрані на основі ваших попередніх покупок:"
            recommendedProducts = allProducts.filter { purchasedCats.contains(it.category) }
        } else {
            tvSubtitle.text = "Популярні товари в нашому магазині (рекомендовано):"
            recommendedProducts = allProducts.take(3)
        }

        adapter = ProductAdapter(
            recommendedProducts,
            onFavoriteToggled = { product ->
                saveFavoriteState(product.id, product.isFavorite)
            },
            onCartUpdated = {}
        )
        rvRecommendations.adapter = adapter
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
}
