package com.kronusboss.cine.movie.usecase.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import com.kronusboss.cine.discord.usecase.UpdateMessageWebhookUseCase;
import com.kronusboss.cine.movie.adapter.repository.MovieNoteRepository;
import com.kronusboss.cine.movie.adapter.repository.rest.MovieSocketRespository;
import com.kronusboss.cine.movie.domain.Movie;
import com.kronusboss.cine.movie.domain.MovieNote;
import com.kronusboss.cine.movie.domain.MovieNoteKey;
import com.kronusboss.cine.movie.usecase.DeleteMovieNoteUseCase;
import com.kronusboss.cine.user.domain.User;

@Component
public class DeleteMovieNoteUseCaseImpl implements DeleteMovieNoteUseCase {

	@Autowired
	private MovieNoteRepository repository;

	@Autowired
	private UpdateMessageWebhookUseCase updateMessageWebhookUseCase;

	@Autowired
	private MovieSocketRespository movieSocketRespository;

	@Override
	@CacheEvict(value = "statistics", allEntries = true)
	public void delete(UUID userId, UUID movieId) {
		MovieNote movieNoteToDelete = repository.findById(new MovieNoteKey(userId, movieId)).orElse(null);

		if (movieNoteToDelete == null) {
			return;
		}

		repository.delete(movieNoteToDelete);
		updateMessageWebhookUseCase.updateMovieMessage(movieId);
		sendEventSocket(userId, movieId);
	}

	private void sendEventSocket(UUID userId, UUID movieId) {
		MovieNote eventContent = MovieNote.builder()
				.movie(Movie.builder().id(movieId).build())
				.user(User.builder().id(userId).build())
				.build();
		movieSocketRespository.emitEventMovie(movieId, "delete-note", eventContent);
	}
}
