package ua.nure.rybalko.vmtpf.pract3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MoviesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        val movies = listOf(
            Movie(
                "Початок (Inception)",
                "Наукова фантастика",
                5.0f,
                2010,
                "2 год 28 хв",
                "Леонардо Ді Капріо, Джозеф Гордон-Левітт, Елліот Пейдж, Том Гарді",
                "Кобб – талановитий злодій, який краде цінні секрети з глибин підсвідомості під час сну, коли розум найбільш вразливий."
            ),
            Movie(
                "Інтерстеллар (Interstellar)",
                "Космос / Драма",
                5.0f,
                2014,
                "2 год 49 хв",
                "Меттью Мак-Конагей, Енн Гетевей, Джессіка Честейн, Майкл Кейн",
                "Група дослідників вирушає в космічну подорож крізь червоточину, щоб знайти нову планету з придатними для життя людства умовами."
            ),
            Movie(
                "Аватар (Avatar)",
                "Пригоди / Фентезі",
                4.5f,
                2009,
                "2 год 42 хв",
                "Сем Вортінгтон, Зої Салдана, Сігурні Вівер, Стівен Ленг",
                "Колишній морський піхотинець Джейк Саллі потрапляє на планету Пандора, де мешкає дивовижна раса На'ві, і постає перед вибором між обов'язком та новим домом."
            ),
            Movie(
                "Темний лицар (The Dark Knight)",
                "Екшн / Трилер",
                4.8f,
                2008,
                "2 год 32 хв",
                "Крістіан Бейл, Гіт Леджер, Аарон Екгарт, Меггі Джилленхол",
                "Бетмен піднімає ставки у війні з криміналом. За допомогою лейтенанта Джима Гордона та прокурора Гарві Дента він має намір очистити Готем від злочинності, але з'являється Джокер."
            ),
            Movie(
                "Дюна (Dune)",
                "Фантастика",
                4.3f,
                2021,
                "2 год 35 хв",
                "Тімоті Шаламе, Ребекка Фергюсон, Оскар Айзек, Зендея, Джейсон Момоа",
                "Пол Атрід, талановитий молодий чоловік, має вирушити на найнебезпечнішу планету у всесвіті, щоб забезпечити майбутнє своєї родини та свого народу."
            )
        )

        val rvMovies = findViewById<RecyclerView>(R.id.rvMovies)
        rvMovies.layoutManager = LinearLayoutManager(this)
        rvMovies.adapter = MovieAdapter(movies)
    }
}
