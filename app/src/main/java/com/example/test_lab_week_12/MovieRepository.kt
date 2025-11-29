package com.example.test_lab_week_12

// Import MovieService yang benar (sesuaikan jika package berbeda)
import MovieService
import com.example.test_lab_week_12.model.Movie

// Import wajib untuk Coroutines Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository(private val movieService: MovieService) {
    // API Key Anda
    private val apiKey = "500aab151a021843ccce13851cb91724"

    // INSTRUKSI: LiveData (movies & error) telah DIHAPUS[cite: 171].

    // Fungsi fetchMovies yang baru menggunakan Flow [cite: 177-186]
    fun fetchMovies(): Flow<List<Movie>> {
        return flow {
            // Mengambil data dari API lalu di-emit (dikirim) ke stream
            val response = movieService.getPopularMovies(apiKey)
            emit(response.results)
        }.flowOn(Dispatchers.IO) // Menjalankan proses ini di Background Thread (IO)
    }
}