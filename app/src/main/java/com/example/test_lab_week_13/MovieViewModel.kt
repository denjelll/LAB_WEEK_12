package com.example.test_lab_week_13

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.test_lab_week_13.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    init {
        fetchPopularMovies()
    }

    // 1. Private tetap MutableStateFlow (agar bisa di-update nilainya di logic bawah)
    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())

    // 2. Public (yang dibaca XML) diubah jadi LiveData menggunakan .asLiveData()
    // Ini akan otomatis membuka bungkus StateFlow dan mengirim List<Movie> ke XML
    val popularMovies: LiveData<List<Movie>> = _popularMovies.asLiveData()

    // StateFlow untuk error (Biarkan StateFlow tidak apa-apa jika tidak dipakai di BindingAdapter)
    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private fun fetchPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.fetchMovies()
                .catch { exception ->
                    _error.value = "An exception occurred: ${exception.message}"
                }
                .collect { movies ->
                    // Jangan difilter dulu, tampilkan semua apa adanya
                    _popularMovies.value = movies.sortedByDescending { it.popularity }
                }
//                .collect { movies ->
//                    // --- ASSIGNMENT LOGIC START ---
//                    val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
//
//                    val filteredAndSortedMovies = movies
//                        .filter { movie ->
//                            movie.releaseDate?.startsWith(currentYear) == true
//                        }
//                        .sortedByDescending { it.popularity }
//
//                    // Logic update tetap sama, update variabel private-nya
//                    _popularMovies.value = filteredAndSortedMovies
//                    // --- ASSIGNMENT LOGIC END ---
//                }
        }
    }
}