package com.picpaysimplificado.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.exceptions.InsufficientBalanceException;
import com.picpaysimplificado.exceptions.InvalidTransactionAmountException;
import com.picpaysimplificado.exceptions.UserNotAllowedException;
import com.picpaysimplificado.exceptions.UserNotFoundException;
import com.picpaysimplificado.repositories.TransactionRepository;
import com.picpaysimplificado.repositories.UserRepository;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class TransactionServiceTest {

	private final TransactionService service;
	private final UserService userService;
	private final TransactionRepository repository;
	private final UserRepository userRepository;

	@MockBean
	private RestTemplate restTemplate;

	@Autowired
	public TransactionServiceTest(TransactionService service, UserService userService, TransactionRepository repository,
			UserRepository userRepository) {
		this.service = service;
		this.repository = repository;
		this.userService = userService;
		this.userRepository = userRepository;
	}

	@BeforeAll
	void userSetup() {
		var blc = new BigDecimal(5000.00);
		var merchant = new UserDTO("Luan", "O", "123456782", blc, "test3@email.com", "123456", UserType.MERCHANT);
		var commom = new UserDTO("Amanda", "G", "123456789", blc, "test1@email.com", "123456", UserType.COMMON);
		userService.createUser(merchant);
		userService.createUser(commom);

	}

	@Test
	@DisplayName("Transação: Deve retornar uma Transação quando os dados forem válidos")
	void createTransactionSuccess() {
		when(restTemplate.getForEntity(anyString(), eq(Map.class)))
				.thenReturn(new ResponseEntity<>(Map.of("status", "success"), HttpStatus.OK));
		User merchant = getUserByType(UserType.MERCHANT);

		User common = getUserByType(UserType.COMMON);
		var dto = new TransactionDTO(new BigDecimal(5000.00), common.getId(), merchant.getId());

		assertNotNull(service.createTransaction(dto));
	}

	@Test
	@DisplayName("Transação: Deve retornar um erro quando o usuário não tiver saldo suficiente")
	void createTransactionInsufficientBalance() {
		User merchant = getUserByType(UserType.MERCHANT);
		User common = getUserByType(UserType.COMMON);
		var dto = new TransactionDTO(new BigDecimal(6000.00), common.getId(), merchant.getId());
		assertThrows(InsufficientBalanceException.class, () -> service.createTransaction(dto));
	}

	@Test
	@DisplayName("Transação: Deve retornar um erro quando o valor for <= 0")
	void createTransactionInvalidTransactionAmmount() {
		User merchant = getUserByType(UserType.MERCHANT);
		User common = getUserByType(UserType.COMMON);

		var dto = new TransactionDTO(new BigDecimal(0.00), common.getId(), merchant.getId());
		var dto2 = new TransactionDTO(new BigDecimal(-50.00), common.getId(), merchant.getId());
		assertThrows(InvalidTransactionAmountException.class, () -> service.createTransaction(dto));
		assertThrows(InvalidTransactionAmountException.class, () -> service.createTransaction(dto2));
	}

	@Test
	@DisplayName("Transação: Deve retornar um erro quando o usuário não existir")
	void createTransactionUserNotFound() {

		var dto = new TransactionDTO(new BigDecimal(1000.00), 5L, 1L);
		var dto2 = new TransactionDTO(new BigDecimal(1000.00), 1L, 5L);
		assertThrows(UserNotFoundException.class, () -> service.createTransaction(dto));
		assertThrows(UserNotFoundException.class, () -> service.createTransaction(dto2));
	}

	@Test
	@DisplayName("Transação: Deve retornar um erro quando userType.MERCHANT tentar eviar dinheiro")
	void createTransactionUserNotAllowed() {

		User merchant = getUserByType(UserType.MERCHANT);
		User common = getUserByType(UserType.COMMON);
		var dto = new TransactionDTO(new BigDecimal(1000.00), merchant.getId(), common.getId());

		assertThrows(UserNotAllowedException.class, () -> service.createTransaction(dto));

	}

	@BeforeEach
	void resetUsersBalance() {
		List<User> users = userRepository.findAll();
		for (User user : users) {
			user.setBalance(new BigDecimal(5000.00));
			userService.updateUser(user);
		}
	}

	private User getUserByType(UserType type) {
		return userService.getAllUsers().stream().filter(u -> u.getUserType() == type).findFirst().get();

	}

}
