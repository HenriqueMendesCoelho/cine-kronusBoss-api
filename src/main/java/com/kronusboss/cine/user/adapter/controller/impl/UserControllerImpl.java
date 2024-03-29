package com.kronusboss.cine.user.adapter.controller.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.kronusboss.cine.adapter.core.controller.dto.UserTokenDto;
import com.kronusboss.cine.user.adapter.controller.UserController;
import com.kronusboss.cine.user.adapter.controller.dto.InviteResponseDto;
import com.kronusboss.cine.user.adapter.controller.dto.UserEmailRequestDto;
import com.kronusboss.cine.user.adapter.controller.dto.UserRedefinePasswordByKeyRequestDto;
import com.kronusboss.cine.user.adapter.controller.dto.UserRequestDto;
import com.kronusboss.cine.user.adapter.controller.dto.UserResponseAdmDto;
import com.kronusboss.cine.user.adapter.controller.dto.UserResponseDto;
import com.kronusboss.cine.user.domain.Invite;
import com.kronusboss.cine.user.domain.User;
import com.kronusboss.cine.user.usecase.CreateInviteUseCase;
import com.kronusboss.cine.user.usecase.CreateUserUseCase;
import com.kronusboss.cine.user.usecase.DeleteInviteUseCase;
import com.kronusboss.cine.user.usecase.DeleteUserUseCase;
import com.kronusboss.cine.user.usecase.SearchInviteUseCase;
import com.kronusboss.cine.user.usecase.SearchUserUseCase;
import com.kronusboss.cine.user.usecase.UpdateUserUseCase;
import com.kronusboss.cine.user.usecase.UserRedefinePasswordUseCase;
import com.kronusboss.cine.user.usecase.exception.DuplicatedUserException;
import com.kronusboss.cine.user.usecase.exception.InviteNotValidException;
import com.kronusboss.cine.user.usecase.exception.UserNotAuthorizedException;
import com.kronusboss.cine.user.usecase.exception.UserNotFoundException;
import com.kronusboss.cine.user.usecase.exception.UserRedefinePasswordKeyInvalid;
import com.kronusboss.cine.user.usecase.exception.UserRedefinePasswordKeyNotFound;

@Controller
public class UserControllerImpl implements UserController {

	@Autowired
	private CreateUserUseCase createUserUseCase;

	@Autowired
	private SearchUserUseCase searchUserUseCase;

	@Autowired
	private UpdateUserUseCase updateUserUseCase;

	@Autowired
	private DeleteUserUseCase deleteUserUseCase;

	@Autowired
	private SearchInviteUseCase searchInviteUseCase;

	@Autowired
	private CreateInviteUseCase createInviteUseCase;

	@Autowired
	private DeleteInviteUseCase deleteInviteUseCase;

	@Autowired
	private UserRedefinePasswordUseCase redefinePasswordUseCase;

	@Override
	public UserResponseDto getUserByEmail(UserTokenDto request, String email)
			throws UserNotFoundException, UserNotAuthorizedException {
		User user = searchUserUseCase.getUserByEmail(email, request.getLogin());
		return new UserResponseDto(user);
	}

	@Override
	public UserResponseDto createUser(UserRequestDto request) throws DuplicatedUserException, InviteNotValidException {
		User user = createUserUseCase.save(request.toDomain(), request.getInviteCode());
		return new UserResponseDto(user);
	}

	@Override
	public UserResponseDto update(UserRequestDto request, UUID id, UserTokenDto userLoged)
			throws UserNotFoundException, UserNotAuthorizedException {
		User response = updateUserUseCase.update(request.toDomain(), id, userLoged.getLogin());
		return new UserResponseDto(response);
	}

	@Override
	public UserResponseDto updateUserProfile(UUID userId, String name, String email, boolean notify)
			throws UserNotFoundException {
		User response = updateUserUseCase.updateUserProfile(userId, name, email, notify);
		return new UserResponseDto(response);
	}

	@Override
	public UserResponseDto updateUserPassoword(UUID userId, String password, String newPassword)
			throws UserNotFoundException, UserNotAuthorizedException {
		User response = updateUserUseCase.updateUserPassoword(userId, password, newPassword);
		return new UserResponseDto(response);
	}

	@Override
	public UserResponseAdmDto getUserByEmailAdm(UserTokenDto request, String email)
			throws UserNotFoundException, UserNotAuthorizedException {
		User response = searchUserUseCase.getUserByEmail(email, request.getLogin());
		return new UserResponseAdmDto(response);
	}

	@Override
	public UserResponseAdmDto promoteUserToAdmin(UUID userId) throws UserNotFoundException {
		User response = updateUserUseCase.promoteUserToAdmin(userId);
		return new UserResponseAdmDto(response);
	}

	@Override
	public UserResponseAdmDto demoteUserToAdmin(UUID userId) throws UserNotFoundException {
		User response = updateUserUseCase.demoteUserToAdmin(userId);
		return new UserResponseAdmDto(response);
	}

	@Override
	public UserResponseAdmDto blockUser(UUID userId) throws UserNotFoundException {
		User response = updateUserUseCase.blockUser(userId);
		return new UserResponseAdmDto(response);
	}

	@Override
	public List<UserResponseDto> getAllUsers() throws UserNotFoundException {
		return searchUserUseCase.getAllUsers().stream().map(UserResponseDto::new).collect(Collectors.toList());
	}

	@Override
	public void delete(UUID id) {
		deleteUserUseCase.deleteUser(id);
	}

	@Override
	public List<InviteResponseDto> getAllInvites() {
		List<Invite> response = searchInviteUseCase.list();
		return response.stream().map(InviteResponseDto::new).collect(Collectors.toList());
	}

	@Override
	public void createRedefinePasswordKey(UserEmailRequestDto request) {
		redefinePasswordUseCase.createRedefinePassword(request.getEmail());
	}

	@Override
	public void redefinePasswordByKey(UserRedefinePasswordByKeyRequestDto request, String key)
			throws UserRedefinePasswordKeyNotFound, UserRedefinePasswordKeyInvalid, UserNotAuthorizedException {
		redefinePasswordUseCase.redefine(key, request.getEmail(), request.getPassword());

	}

	// Invites
	@Override
	public InviteResponseDto createUserInvite() {
		Invite response = createInviteUseCase.create();
		return new InviteResponseDto(response);
	}

	@Override
	public void deleteInvite(String code) {
		deleteInviteUseCase.delete(code);
	}

}
