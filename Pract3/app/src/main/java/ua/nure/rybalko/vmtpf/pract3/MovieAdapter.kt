package ua.nure.rybalko.vmtpf.pract3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(private val movieList: List<Movie>) : 
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMovieName: TextView = view.findViewById(R.id.tvMovieName)
        val tvMovieInfo: TextView = view.findViewById(R.id.tvMovieInfo)
        val tvMovieRating: TextView = view.findViewById(R.id.tvMovieRating)
        val btnMovieDetails: Button = view.findViewById(R.id.btnMovieDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.tvMovieName.text = movie.name
        holder.tvMovieInfo.text = "${movie.genre} • ${movie.year} • ${movie.duration}"

        val roundedRating = Math.round(movie.rating)
        val stars = "★".repeat(roundedRating) + "☆".repeat(5 - roundedRating)
        holder.tvMovieRating.text = "$stars ${movie.rating}"

        holder.btnMovieDetails.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle(movie.name)
                .setMessage(
                    "Жанр: ${movie.genre}\n" +
                    "Рік: ${movie.year}\n" +
                    "Тривалість: ${movie.duration}\n" +
                    "Рейтинг: $stars (${movie.rating})\n\n" +
                    "Актори:\n${movie.actors}\n\n" +
                    "Про фільм:\n${movie.description}"
                )
                .setPositiveButton("Закрити", null)
                .show()
        }
    }

    override fun getItemCount(): Int = movieList.size
}
