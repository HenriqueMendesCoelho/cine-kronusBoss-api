package com.kronusboss.cine.movie.application.spring.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kronusboss.cine.adapter.core.controller.dto.UserTokenDto;
import com.kronusboss.cine.adapter.util.CredentialUtil;
import com.kronusboss.cine.movie.adapter.controller.MovieController;
import com.kronusboss.cine.movie.adapter.controller.dto.MovieGenreResponseDto;
import com.kronusboss.cine.movie.adapter.controller.dto.MovieNoteRequestDto;
import com.kronusboss.cine.movie.adapter.controller.dto.MovieNoteResponseDto;
import com.kronusboss.cine.movie.adapter.controller.dto.MovieRequestDto;
import com.kronusboss.cine.movie.adapter.controller.dto.MovieResponseDto;
import com.kronusboss.cine.movie.usecase.exception.DuplicatedMovieException;
import com.kronusboss.cine.movie.usecase.exception.DuplicatedMovieNoteException;
import com.kronusboss.cine.movie.usecase.exception.MovieNotFoundException;
import com.kronusboss.cine.movie.usecase.exception.MovieNoteNotFoundException;
import com.kronusboss.cine.user.usecase.exception.UserNotAuthorizedException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/movie")
public class SpringMovieController {

	@Autowired
	private MovieController controller;

	@GetMapping
	public ResponseEntity<?> getAllMoviesTest(@RequestParam(required = false) String title,
			@RequestParam(required = false) String withGenres, @RequestParam(required = false) String sortJoin,
			Pageable pageable, @RequestHeader("Authorization") String token) {
		UserTokenDto user = CredentialUtil.getUserFromToken(token);
		Page<MovieResponseDto> response = controller.listAllMovies(title, withGenres, sortJoin, pageable, user);

		if (response.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{movieId}")
	public ResponseEntity<?> getMovieById(@PathVariable UUID movieId, @RequestHeader("Authorization") String token) {
		MovieResponseDto response;
		try {
			UserTokenDto user = CredentialUtil.getUserFromToken(token);
			response = controller.getById(movieId, user);
		} catch (MovieNoteNotFoundException e) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<?> createMovie(@RequestBody @Valid MovieRequestDto request,
			@RequestHeader("Authorization") String token) {
		try {
			UserTokenDto user = CredentialUtil.getUserFromToken(token);
			MovieResponseDto response = controller.save(request, user);
			return ResponseEntity.ok(response);
		} catch (DuplicatedMovieException e) {
			return ResponseEntity.badRequest().body(Map.of("error", true, "code", 400, "message", e.getMessage()));
		}
	}

	@PutMapping("/{movieId}/update")
	public ResponseEntity<?> updateMovie(@RequestBody @Valid MovieRequestDto request, @PathVariable UUID movieId,
			@RequestHeader("Authorization") String token) {

		try {
			UserTokenDto user = CredentialUtil.getUserFromToken(token);
			MovieResponseDto response = controller.update(request, movieId, user);
			return ResponseEntity.ok(response);
		} catch (MovieNotFoundException e) {
			return ResponseEntity.badRequest().body(Map.of("error", true, "code", 400, "message", e.getMessage()));
		} catch (UserNotAuthorizedException e) {
			return ResponseEntity.status(403).body(Map.of("error", true, "code", 400, "message", e.getMessage()));
		}
	}

	@DeleteMapping("/{movieId}/delete")
	public ResponseEntity<?> updateMovie(@PathVariable UUID movieId, @RequestHeader("Authorization") String token) {
		try {
			UserTokenDto user = CredentialUtil.getUserFromToken(token);
			controller.delete(movieId, user);
			return ResponseEntity.ok().build();
		} catch (UserNotAuthorizedException e) {
			return ResponseEntity.status(403).body(Map.of("error", true, "code", 403, "message", e.getMessage()));
		}
	}

	@GetMapping("/genres")
	public ResponseEntity<?> listMovieGenres() {

		List<MovieGenreResponseDto> response = controller.listGenres();

		if (response.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(response);

	}

	@GetMapping("/genres-with-movies")
	public ResponseEntity<?> listMovieGenresWithMovies() {

		List<MovieGenreResponseDto> response = controller.listAllGenresWithMovies();

		if (response.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(response);

	}

	@GetMapping("/note/{movieId}")
	public ResponseEntity<?> getMovieNote(@PathVariable UUID movieId, @RequestHeader("Authorization") String token) {
		try {
			UserTokenDto user = CredentialUtil.getUserFromToken(token);
			List<MovieNoteResponseDto> response = controller.listMovieNotes(movieId, user);
			return ResponseEntity.ok(response);
		} catch (MovieNotFoundException e) {
			return ResponseEntity.badRequest().body(Map.of("error", true, "code", 400, "message", e.getMessage()));
		}
	}

	@PostMapping("/note")
	public ResponseEntity<?> createMovieNote(@RequestBody @Valid MovieNoteRequestDto request,
			@RequestHeader("Authorization") String token) {
		try {
			UserTokenDto user = CredentialUtil.getUserFromToken(token);
			MovieNoteResponseDto response = controller.createMovieNote(request, user);
			return ResponseEntity.ok(response);
		} catch (MovieNotFoundException | DuplicatedMovieNoteException e) {
			return ResponseEntity.badRequest().body(Map.of("error", true, "code", 400, "message", e.getMessage()));
		}
	}

	@PatchMapping("note/{movieId}/update")
	public ResponseEntity<?> updateMovieNote(@RequestBody @Valid MovieNoteRequestDto request,
			@PathVariable UUID movieId, @RequestHeader("Authorization") String token) {
		try {
			UserTokenDto user = CredentialUtil.getUserFromToken(token);
			MovieNoteResponseDto response = controller.updateMovieNote(movieId, request, user);
			return ResponseEntity.ok(response);
		} catch (MovieNoteNotFoundException e) {
			return ResponseEntity.badRequest().body(Map.of("error", true, "code", 400, "message", e.getMessage()));
		}
	}

	@DeleteMapping("/note/{movieId}/delete")
	public ResponseEntity<?> deleteMovieNote(@PathVariable UUID movieId, @RequestHeader("Authorization") String token) {

		UserTokenDto user;
		user = CredentialUtil.getUserFromToken(token);
		controller.deleteMovieNote(movieId, user);
		return ResponseEntity.ok().build();

	}
}
