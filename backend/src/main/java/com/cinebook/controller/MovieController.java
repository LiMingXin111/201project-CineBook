package com.cinebook.controller;

import com.cinebook.model.Movie;
import com.cinebook.service.MovieService;
import com.cinebook.server.Request;
import com.cinebook.server.Response;
import com.cinebook.util.JsonUtil;

import java.util.List;

public class MovieController {
    private final MovieService movieService = new MovieService();

    public Response handleRequest(Request request) {
        if ("GET".equals(request.getMethod())) {
            if ("/api/movies".equals(request.getPath())) {
                return getAllMovies();
            } else if (request.getPath().matches("/api/movies/\\d+")) {
                return getMovieById(request);
            }
        } else if ("POST".equals(request.getMethod())) {
            if ("/api/movies".equals(request.getPath())) {
                return addMovie(request);
            }
        } else if ("PUT".equals(request.getMethod())) {
            if (request.getPath().matches("/api/movies/\\d+")) {
                return updateMovie(request);
            }
        } else if ("DELETE".equals(request.getMethod())) {
            if (request.getPath().matches("/api/movies/\\d+")) {
                return deleteMovie(request);
            }
        }

        return new Response(404, "text/plain", "Not Found");
    }

    private Response getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        return new Response(200, "application/json", JsonUtil.toJson(movies));
    }

    private Response getMovieById(Request request) {
        String[] parts = request.getPath().split("/");
        int id = Integer.parseInt(parts[3]);
        Movie movie = movieService.getMovieById(id);

        if (movie == null) {
            return new Response(404, "application/json",
                JsonUtil.toJson(new ErrorResponse("Movie not found", "Movie with id " + id + " not found")));
        }

        return new Response(200, "application/json", JsonUtil.toJson(movie));
    }

    private Response addMovie(Request request) {
        if (!isAdmin(request)) {
            return new Response(403, "application/json",
                JsonUtil.toJson(new ErrorResponse("Forbidden", "Admin access required")));
        }
        if (request.getBody() == null || request.getBody().trim().isEmpty()) {
            return new Response(400, "application/json",
                JsonUtil.toJson(new ErrorResponse("Invalid request", "Request body is required")));
        }

        Movie movie = JsonUtil.fromJson(request.getBody(), Movie.class);
        Movie createdMovie = movieService.addMovie(movie);
        return new Response(201, "application/json", JsonUtil.toJson(createdMovie));
    }

    private Response updateMovie(Request request) {
        if (!isAdmin(request)) {
            return new Response(403, "application/json",
                JsonUtil.toJson(new ErrorResponse("Forbidden", "Admin access required")));
        }
        if (request.getBody() == null || request.getBody().trim().isEmpty()) {
            return new Response(400, "application/json",
                JsonUtil.toJson(new ErrorResponse("Invalid request", "Request body is required")));
        }

        String[] parts = request.getPath().split("/");
        int id = Integer.parseInt(parts[3]);
        Movie movie = JsonUtil.fromJson(request.getBody(), Movie.class);
        movie.setId(id);

        Movie updatedMovie = movieService.updateMovie(movie);
        if (updatedMovie == null) {
            return new Response(404, "application/json",
                JsonUtil.toJson(new ErrorResponse("Movie not found", "Movie with id " + id + " not found")));
        }

        return new Response(200, "application/json", JsonUtil.toJson(updatedMovie));
    }

    private Response deleteMovie(Request request) {
        if (!isAdmin(request)) {
            return new Response(403, "application/json",
                JsonUtil.toJson(new ErrorResponse("Forbidden", "Admin access required")));
        }

        String[] parts = request.getPath().split("/");
        int id = Integer.parseInt(parts[3]);
        boolean deleted = movieService.deleteMovie(id);

        if (!deleted) {
            return new Response(404, "application/json",
                JsonUtil.toJson(new ErrorResponse("Movie not found", "Movie with id " + id + " not found")));
        }

        return new Response(204, null, null);
    }

    private boolean isAdmin(Request request) {
        return "true".equals(request.getHeader("Admin"));
    }

    private static class ErrorResponse {
        public String error;
        public String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
    }
}
