package com.example.test_lab_week_12

import com.example.test_lab_week_12.api.MovieService
import com.example.test_lab_week_12.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository(private val movieService: MovieService) {
    private val apiKey = "500aab151a021843ccce13851cb91724"

    // fetch movies from the API
    // this function returns a Flow of Movie objects List
    // a Flow is a type of coroutine that can emit multiple values
    fun fetchMovies(): Flow<List<Movie>> {
        return flow {
            // emit the list of popular movies from the API
            // Pastikan getPopularMovies di Service Anda bersifat 'suspend'
            val response = movieService.getPopularMovies(apiKey)
            emit(response.results)
        }.flowOn(Dispatchers.IO) // use Dispatchers.IO to run this on a background thread
    }
}