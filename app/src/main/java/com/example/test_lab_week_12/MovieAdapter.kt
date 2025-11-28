package com.example.test_lab_week_12

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Pastikan library Glide sudah ada di build.gradle
import com.example.test_lab_week_12.model.Movie

class MovieAdapter(private val onClick: MovieClickListener) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private val movies = mutableListOf<Movie>()

    interface MovieClickListener {
        fun onMovieClick(movie: Movie)
    }

    fun addMovies(newMovies: List<Movie>) {
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        // Pastikan kamu sudah membuat layout bernama 'item_movie.xml'
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
        holder.itemView.setOnClickListener {
            onClick.onMovieClick(movie)
        }
    }

    override fun getItemCount(): Int = movies.size

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Sesuaikan ID ini dengan file xml 'item_movie.xml' kamu
        private val title: TextView = itemView.findViewById(R.id.movie_title)
        private val releaseDate: TextView = itemView.findViewById(R.id.movie_release_date)
        private val poster: ImageView = itemView.findViewById(R.id.movie_poster)

        fun bind(movie: Movie) {
            title.text = movie.title
            releaseDate.text = movie.releaseDate

            // Load gambar pakai Glide
            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
            Glide.with(itemView.context)
                .load(imageUrl)
                .into(poster)
        }
    }
}