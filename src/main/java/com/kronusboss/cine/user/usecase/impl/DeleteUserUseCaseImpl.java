package com.kronusboss.cine.user.usecase.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kronusboss.cine.adapter.user.repository.jpa.UserRepository;
import com.kronusboss.cine.user.domain.User;
import com.kronusboss.cine.user.usecase.DeleteUserUseCase;

@Service
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

	@Autowired
	UserRepository repository;

	@Override
	public void deleteUser(UUID id) {

		User userToDelete = repository.getReferenceById(id);

		if (userToDelete == null) {
			return;
		}

		repository.delete(userToDelete);

	}

}
