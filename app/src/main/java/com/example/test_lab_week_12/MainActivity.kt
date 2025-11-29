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
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // --- BAGIAN INI YANG DIMODIFIKASI ---
                launch {
                    movieViewModel.popularMovies.collect { movies ->
                        // 1. Ambil tahun saat ini
                        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()

                        // 2. Lakukan Filter dan Sorting (Sesuai tugas hal 8)
                        val filteredMovies = movies
                            .filter { movie ->
                                // Ambil hanya film yang rilis tahun ini
                                movie.releaseDate?.startsWith(currentYear) == true
                            }
                            .sortedByDescending {
                                // Urutkan berdasarkan popularitas tertinggi
                                it.popularity
                            }

                        // 3. Masukkan data yang sudah difilter ke adapter
                        movieAdapter.addMovies(filteredMovies)
                    }
                }
                // -------------------------------------

                launch {
                    movieViewModel.error.collect { error ->
                        if (error.isNotEmpty()) {
                            Snackbar.make(recyclerView, error, Snackbar.LENGTH_LONG).show()
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