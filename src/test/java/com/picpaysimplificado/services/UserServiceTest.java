package com.picpaysimplificado.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;

@SpringBootTest
class UserServiceTest {

	private final UserService service;

	@Autowired
	public UserServiceTest(UserService service) {
		this.service = service;
	}

	@Test
	void createUserSuccess() {
		var document = "999999999-99";
		var balance = new BigDecimal(5000.00);
		var userDTO = new UserDTO("Luan", "Albis", document, balance, "luan@exemplo", "1234", UserType.COMMON);
		assertNotNull(service.createUser(userDTO));
	}

}
