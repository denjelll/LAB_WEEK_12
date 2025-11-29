package com.example.test_lab_week_12

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test_lab_week_12.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    // 1. Mengganti LiveData dengan StateFlow untuk List Movie
    // MutableStateFlow memungkinkan kita mengubah nilainya
    private val _popularMovies = MutableStateFlow(emptyList<Movie>())
    val popularMovies: StateFlow<List<Movie>> = _popularMovies

    // 2. Mengganti LiveData dengan StateFlow untuk Error Message
    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    init {
        fetchPopularMovies()
    }

    // 3. Fungsi fetch data yang baru menggunakan Flow
    private fun fetchPopularMovies() {
        // Menjalankan coroutine di viewModelScope dengan Dispatchers.IO
        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.fetchMovies()
                .catch { exception ->
                    // Terminal operator untuk menangkap error dari Flow
                    _error.value = "An exception occurred: ${exception.message}"
                }
                .collect { movies ->
                    // Terminal operator untuk mengumpulkan data (collect)
                    // Hasilnya dimasukkan (emit) ke dalam StateFlow
                    _popularMovies.value = movies
                }
        }
    }
}