package com.picpaysimplificado.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.exceptions.InsufficientBalanceException;
import com.picpaysimplificado.exceptions.InvalidTransactionAmountException;
import com.picpaysimplificado.exceptions.SelfTransactionException;
import com.picpaysimplificado.exceptions.UnauthorizedTransactionException;
import com.picpaysimplificado.exceptions.UserNotAllowedException;
import com.picpaysimplificado.exceptions.UserNotFoundException;
import com.picpaysimplificado.repositories.TransactionRepository;

class TransactionServiceTest {

	@Mock
	private UserService userService;

	@Mock
	private TransactionRepository repository;

	@Mock
	private AuthTransactionService authService;

	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private TransactionService service;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("Transação: Deve retornar uma Transação quando os dados forem válidos")
	void createTransaction_Success() {

		User sender = new User(1L, "Luan", "A", "000000000-01", "luan@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);
		User receiver = new User(2L, "Amanda", "o", "000000000-02", "amanda@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);
		when(userService.getUserById(sender.getId())).thenReturn(sender);
		when(userService.getUserById(receiver.getId())).thenReturn(receiver);
		when(authService.authorizeTransaction(any(), any())).thenReturn(true);

		BigDecimal amount = BigDecimal.TEN;
		BigDecimal senderExpectedBalance = sender.getBalance().subtract(amount);
		BigDecimal receiverExpectedBalance = receiver.getBalance().add(amount);

		var request = new TransactionDTO(amount, sender.getId(), receiver.getId());
		service.createTransaction(request);

		verify(repository, times(1)).save(any());
		verify(userService, times(2)).updateUser(any());
		verify(notificationService, times(2)).sendNotification(any(), any());
		assertEquals(senderExpectedBalance, sender.getBalance());
		assertEquals(receiverExpectedBalance, receiver.getBalance());
	}

	@Test
	@DisplayName("Transação: Deve retornar um erro quando o usuário não tiver saldo suficiente")
	void createTransaction_InsufficientBalanceException() {
		User sender = new User(1L, "Luan", "A", "000000000-01", "luan@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);
		User receiver = new User(2L, "Amanda", "o", "000000000-02", "amanda@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);

		when(userService.getUserById(sender.getId())).thenReturn(sender);
		when(userService.getUserById(receiver.getId())).thenReturn(receiver);
		when(authService.authorizeTransaction(any(), any())).thenReturn(true);

		BigDecimal amountToSend = sender.getBalance().add(BigDecimal.ONE);

		var request = new TransactionDTO(amountToSend, sender.getId(), receiver.getId());
		assertThrows(InsufficientBalanceException.class, () -> service.createTransaction(request));
	}

	@Test
	@DisplayName("Transação: Deve retornar um erro quando o valor for <= 0")
	void createTransaction_InvalidTransactionAmmountException() {
		User sender = new User(1L, "Luan", "A", "000000000-01", "luan@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);
		User receiver = new User(2L, "Amanda", "o", "000000000-02", "amanda@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);

		when(userService.getUserById(sender.getId())).thenReturn(sender);
		when(userService.getUserById(receiver.getId())).thenReturn(receiver);
		when(authService.authorizeTransaction(any(), any())).thenReturn(true);

		BigDecimal amountToSend = BigDecimal.ZERO;

		var request = new TransactionDTO(amountToSend, sender.getId(), receiver.getId());
		assertThrows(InvalidTransactionAmountException.class, () -> service.createTransaction(request));
	}

	@Test
	@DisplayName("Transação: Deve retornar um erro quando o usuário não existir")
	void createTransaction_UserNotFoundException() {
		Long nonExistentId = 99L;
		User existingUser = new User(1L, "Luan", "A", "000000000-01", "luan@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);

		when(userService.getUserById(existingUser.getId())).thenReturn(existingUser);
		when(userService.getUserById(nonExistentId)).thenThrow(new UserNotFoundException());
		when(authService.authorizeTransaction(any(), any())).thenReturn(true);

		var request = new TransactionDTO(BigDecimal.TEN, nonExistentId, existingUser.getId());
		var request2 = new TransactionDTO(BigDecimal.TEN, existingUser.getId(), nonExistentId);

		assertThrows(UserNotFoundException.class, () -> service.createTransaction(request));
		assertThrows(UserNotFoundException.class, () -> service.createTransaction(request2));
	}

	@Test
	@DisplayName("Transação: Deve retornar um erro quando userType.MERCHANT tentar eviar dinheiro")
	void createTransaction_UserNotAllowedException() {
		User merchant = new User(1L, "Luan", "A", "000000000-01", "luan@email.com", "password", BigDecimal.TEN,
				UserType.MERCHANT);
		User common = new User(2L, "Amanda", "o", "000000000-02", "amanda@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);

		when(userService.getUserById(merchant.getId())).thenReturn(merchant);
		when(userService.getUserById(common.getId())).thenReturn(common);
		when(authService.authorizeTransaction(any(), any())).thenReturn(true);

		var request = new TransactionDTO(merchant.getBalance(), merchant.getId(), common.getId());
		assertThrows(UserNotAllowedException.class, () -> service.createTransaction(request));
	}

	@Test
	@DisplayName("Transação: Deve retornar um erro quando IDs dos usuários são iguais")
	void createTransaction_SelfTransactionException() {
		User user = new User(1L, "Amanda", "o", "000000000-02", "amanda@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);

		when(userService.getUserById(user.getId())).thenReturn(user);
		when(authService.authorizeTransaction(any(), any())).thenReturn(true);

		var request = new TransactionDTO(user.getBalance(), user.getId(), user.getId());
		assertThrows(SelfTransactionException.class, () -> service.createTransaction(request));
	}

	@Test
	@DisplayName("Transação: Deve retornar um erro quando a transação não é autorizada")
	void createTransaction_UnauthorizedTransactionException() {

		User sender = new User(1L, "Luan", "A", "000000000-01", "luan@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);
		User receiver = new User(2L, "Amanda", "o", "000000000-02", "amanda@email.com", "password", BigDecimal.TEN,
				UserType.COMMON);
		when(userService.getUserById(sender.getId())).thenReturn(sender);
		when(userService.getUserById(receiver.getId())).thenReturn(receiver);
		when(authService.authorizeTransaction(any(), any())).thenReturn(false);

		var request = new TransactionDTO(sender.getBalance(), sender.getId(), receiver.getId());
		assertThrows(UnauthorizedTransactionException.class, () -> service.createTransaction(request));
	}
}
