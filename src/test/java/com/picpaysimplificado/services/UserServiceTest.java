package com.picpaysimplificado.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.exceptions.DuplicateUserException;
import com.picpaysimplificado.repositories.UserRepository;

@SpringBootTest
class UserServiceTest {

	private final UserService service;
	private final UserRepository repository;

	@Autowired
	public UserServiceTest(UserService service, UserRepository repository) {
		this.service = service;
		this.repository = repository;
	}

	private User createValidUser(String doc, String email) {
		var userDTO = new UserDTO("Luan", "Albis", doc, new BigDecimal("5000.00"), email, "123456", UserType.COMMON);
		return new User(userDTO);
	}

	@BeforeEach
	@DisplayName("Limpa a Database a cada método")
	void cleanDatabase() {
		repository.deleteAll();
	}

	@Test
	@DisplayName("Criação de usuário: Deve retornar o usuário quando os dados forem válidos")
	void createUserSuccess() {
		assertNotNull(service.createUser(this.createValidUser("999999999-99", "email@test.com").toDTO()));
	}

	@Test
	@DisplayName("Criação de usuário: lança DuplicateUserException se o Document já existir")
	void createUserDuplicateDocument() {
		var doc = "999999999-99";
		User user = createValidUser(doc, "email@test.com");
		User user2 = createValidUser(doc, "email@test.com");
		user2.setEmail("emaildiferente@exemplo.com");
		service.createUser(user.toDTO());
		assertThrows(DuplicateUserException.class, () -> service.createUser(user2.toDTO()));
	}

	@Test
	@DisplayName("Criação de usuário: lança DuplicateUserException se o Email já existir")
	void createUserEmail() {
		var email = "email@test.com";
		User user = createValidUser("09090938932", email);
		User user2 = createValidUser("98903284545", email);
		user2.setDocument("8748923743");
		service.createUser(user.toDTO());
		assertThrows(DuplicateUserException.class, () -> service.createUser(user2.toDTO()));
	}

	@Test
	@DisplayName("Atualização de usuário: Deve retornar o usuário quando os dados forem válidos")
	void updateUserSuccess() {
		User user = service.createUser(createValidUser("999999999-99", "email@test.com").toDTO());

		var newFirstName = "João";
		user.setFirstName(newFirstName);

		assertNotNull(service.updateUser(user));
		assertEquals(newFirstName, service.getUserById(user.getId()).getFirstName());
	}

	@Test
	@DisplayName("Atualização de usuário: lança DuplicateUserException se o Documento já existir")
	void updateUserDuplicateDocument() {
		User user1 = service.createUser(createValidUser("999999999", "email@test.com").toDTO());
		User user2 = service.createUser(createValidUser("000000000", "email2@test.com").toDTO());
		user2.setDocument(user1.getDocument());

		assertThrows(DuplicateUserException.class, () -> service.updateUser(user2));
	}

	@Test
	@DisplayName("Atualização de usuário: lança DuplicateUserException se o Email já existir")
	void updateUserDuplicateEmail() {
		User user1 = service.createUser(createValidUser("999999999", "email@test.com").toDTO());
		User user2 = service.createUser(createValidUser("000000000", "email2@test.com").toDTO());
		user2.setEmail(user1.getEmail());

		assertThrows(DuplicateUserException.class, () -> service.updateUser(user2));
	}

}
