package com.starwar.movie.impl;

import com.starwar.movie.dao.MoviesDAO;
import com.starwar.movie.exception.MovieNotFoundException;
import com.starwar.movie.model.Directors;
import com.starwar.movie.model.Movies;
import com.starwar.movie.model.Ratings;
import com.starwar.movie.model.Stars;
import com.starwar.movie.repository.DirectorRepository;
import com.starwar.movie.repository.MoviesRepository;
import com.starwar.movie.repository.RatingsRepository;
import com.starwar.movie.repository.StarsRepository;
import com.starwar.movie.util.MovieData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MoviesDAOImpl implements MoviesDAO {

    @Autowired
    DirectorRepository directorRepository;
    @Autowired
    StarsRepository starsRepository;
    @Autowired
    MoviesRepository moviesRepository;
    @Autowired
    RatingsRepository ratingsRepository;

    @Override
    public List<Movies> getMoviesByPersonId(Integer personId) {
        List<Movies> movies = new ArrayList<>();

        // Get movies directed by the person
        List<Directors> directedMovies = directorRepository.findByPersonId(personId);
        for (Directors director : directedMovies) {
            movies.add(director.getMovie());
        }

        // Get movies starred by the person
        List<Stars> starredMovies = starsRepository.findByPersonId(personId);
        for (Stars star : starredMovies) {
            movies.add(star.getMovie());
        }

        return movies;
    }

    @Override
    public MovieData getMovieData(Integer movieId) {
        Movies movie = moviesRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with ID: " + movieId));

        List<Directors> directors = directorRepository.findByMovieId(movieId);
        List<Stars> stars = starsRepository.findByMovieId(movieId);
        List<Ratings> ratings = ratingsRepository.findByMovieId(movieId);

        return new MovieData(movie, directors, stars, ratings);
    }
    @Override
    public List<Movies> getMovies(int page, int size, String sortBy, String sortOrder) {
        Pageable pageable = createPageable(page, size, sortBy, sortOrder);
        Page<Movies> moviePage = moviesRepository.findAll(pageable);
        return moviePage.getContent();
    }

    @Override
    public Pageable createPageable(int page, int size, String sortBy, String sortOrder) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortOrder.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }


    @Override
    public Movies save(Movies movies) {

        Movies _newMovie = new Movies();

        _newMovie.setId(_newMovie.getId());
        _newMovie.setTitle(_newMovie.getTitle());
        _newMovie.setYear(_newMovie.getYear());

        return moviesRepository.save(movies);
    }

    @Override
    public Movies findById(Integer id) {
        Optional<Movies> optionalRatings = moviesRepository.findById(id);
        return optionalRatings.orElse(null);
    }

    @Override
    public Movies updateById(Movies movies, Integer id) {
        Optional<Movies> optionalMovies = moviesRepository.findById(id);

        if (optionalMovies.isPresent()) {
            Movies existingMovie = optionalMovies.get();

            existingMovie.setTitle(movies.getTitle());
            existingMovie.setYear(movies.getYear());


            return moviesRepository.save(existingMovie);
        } else {
            throw new IllegalArgumentException("Movie not found with ID: " + id);
        }
    }

    @Override
    public void deleteById(Integer id) {
        moviesRepository.deleteById(id);
    }
}
