package com.example.test_lab_week_12

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test_lab_week_12.model.Movie
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val movieAdapter by lazy {
        MovieAdapter(object : MovieAdapter.MovieClickListener {
            override fun onMovieClick(movie: Movie) {
                openMovieDetails(movie)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.movie_list)
        // PENTING: Jangan lupa LayoutManager agar list muncul
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = movieAdapter

        val movieRepository = (application as MovieApplication).movieRepository
        val movieViewModel = ViewModelProvider(
            this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MovieViewModel(movieRepository) as T
                }
            })[MovieViewModel::class.java]

        // --- BAGIAN IMPLEMENTASI FLOW (Part 2) & ASSIGNMENT (Tugas) ---

        // lifecycleScope is a lifecycle-aware coroutine scope [cite: 219]
        lifecycleScope.launch {
            // repeatOnLifecycle is a lifecycle-aware coroutine builder [cite: 221]
            // Lifecycle.State.STARTED means that the coroutine will run when the activity is started
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1. Mengambil data list film
                launch {
                    // collect the list of movies from the StateFlow [cite: 224]
                    movieViewModel.popularMovies.collect { movies ->

                        // LOGIKA ASSIGNMENT (HALAMAN 8):
                        // "Implement the same data filter (descending by popularity) to the State Flow" [cite: 249]
                        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()

                        val filteredMovies = movies
                            .filter { movie ->
                                // Filter film yang rilis tahun ini
                                movie.releaseDate?.startsWith(currentYear) == true
                            }
                            .sortedByDescending { it.popularity } // Sort berdasarkan popularitas

                        // add the list of movies to the adapter
                        movieAdapter.addMovies(filteredMovies)
                    }
                }

                // 2. Mengambil pesan error
                launch {
                    // collect the error message from the StateFlow [cite: 230]
                    movieViewModel.error.collect { error ->
                        // if an error occurs, show a Snackbar with the error [cite: 231]
                        if (error.isNotEmpty()) {
                            Snackbar.make(
                                recyclerView, error, Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun openMovieDetails(movie: Movie) {
        val intent = Intent(this, DetailsActivity::class.java).apply {
            putExtra(DetailsActivity.EXTRA_TITLE, movie.title)
            putExtra(DetailsActivity.EXTRA_RELEASE, movie.releaseDate)
            putExtra(DetailsActivity.EXTRA_OVERVIEW, movie.overview)
            putExtra(DetailsActivity.EXTRA_POSTER, movie.posterPath)
        }
        startActivity(intent)
    }
}