package com.picpaysimplificado.services;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.exceptions.DuplicateUserException;
import com.picpaysimplificado.repositories.UserRepository;

class UserServiceTest {

	@Mock
	private UserRepository repository;

	@InjectMocks
	private UserService service;

	@BeforeEach
	void initialSetup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("Criação de usuário: Deve retornar o usuário quando os dados forem válidos")
	void createUser_Success() {
		User user = new User(1L, "Amanda", "o", "000000000-02", "amanda@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);

		when(repository.existsByEmail(user.getEmail())).thenReturn(false);
		when(repository.existsByDocument(user.getDocument())).thenReturn(false);

		service.createUser(user.toDTO());
		verify(repository, times(1)).save(any());
	}

	@Test
	@DisplayName("Criação de usuário: lança DuplicateUserException se o Document já existir")
	void createUser_document_DuplicateUserException() {

		User user = new User(1L, "Amanda", "o", "000000000-02", "amanda@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);

		when(repository.existsByEmail(user.getEmail())).thenReturn(false);
		when(repository.existsByDocument(user.getDocument())).thenReturn(true);

		assertThrows(DuplicateUserException.class, () -> service.createUser(user.toDTO()));
	}

	@Test
	@DisplayName("Criação de usuário: lança DuplicateUserException se o Email já existir")
	void createUser_email_DuplicateUserException() {
		User user = new User(1L, "Amanda", "o", "000000000-02", "amanda@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);

		when(repository.existsByEmail(user.getEmail())).thenReturn(true);
		when(repository.existsByDocument(user.getDocument())).thenReturn(false);

		assertThrows(DuplicateUserException.class, () -> service.createUser(user.toDTO()));
	}

	@Test
	@DisplayName("Atualização de usuário: Deve retornar o usuário quando os dados forem válidos")
	void updateUser_Success() {
		User user = new User(1L, "Amanda", "o", "000000000-02", "amanda@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);

		when(repository.findById(user.getId())).thenReturn(Optional.of(user));
		when(repository.existsByDocument(user.getDocument())).thenReturn(false);
		when(repository.existsByEmail(user.getEmail())).thenReturn(false);
		when(repository.save(any())).thenReturn(user);

		var firstName = user.getFirstName();
		user.setFirstName("João");

		assertNotNull(service.updateUser(user));
		assertNotEquals(firstName, user.getFirstName());
		verify(repository, times(1)).save(any());
	}

	@Test
	@DisplayName("Atualização de usuário: lança DuplicateUserException se o Documento já existir")
	void updateUser_document_DuplicateUserException() {
		User userToUpdate = new User(1L, "Amanda", "o", "000000000-00", "amanda@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);
		User originalUser = new User(1L, "Amanda", "o", "000000000-02", "amanda@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);

		when(repository.findById(userToUpdate.getId())).thenReturn(Optional.of(originalUser));
		when(repository.existsByDocument(userToUpdate.getDocument())).thenReturn(true);
		when(repository.existsByEmail(userToUpdate.getEmail())).thenReturn(false);

		assertThrows(DuplicateUserException.class, () -> service.updateUser(userToUpdate));
	}

	@Test
	@DisplayName("Atualização de usuário: lança DuplicateUserException se o Email já existir")
	void updateUser_email_DuplicateUserException() {
		User userToUpdate = new User(1L, "Amanda", "o", "000000000-00", "amanda@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);
		User originalUser = new User(1L, "Amanda", "o", "000000000-00", "amandaEXISTE@email.com", "password",
				BigDecimal.TEN, UserType.COMMON);

		when(repository.findById(userToUpdate.getId())).thenReturn(Optional.of(originalUser));
		when(repository.existsByDocument(userToUpdate.getDocument())).thenReturn(false);
		when(repository.existsByEmail(userToUpdate.getEmail())).thenReturn(true);

		assertThrows(DuplicateUserException.class, () -> service.updateUser(userToUpdate));
	}
}
