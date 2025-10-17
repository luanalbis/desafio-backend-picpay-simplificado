package com.picpaysimplificado.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.exceptions.InsufficientBalanceException;
import com.picpaysimplificado.exceptions.InvalidTransactionAmountException;
import com.picpaysimplificado.exceptions.SelfTransactionException;
import com.picpaysimplificado.exceptions.UnauthorizedTransactionException;
import com.picpaysimplificado.exceptions.UserNotAllowedException;
import com.picpaysimplificado.repositories.TransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {
	private final TransactionRepository repository;
	private final UserService userService;
	private final AuthTransactionService authService;
	private final NotificationService notificationService;

	@Autowired
	public TransactionService(TransactionRepository repository, UserService userService,
			AuthTransactionService authService, NotificationService notificationService) {
		this.repository = repository;
		this.userService = userService;
		this.authService = authService;
		this.notificationService = notificationService;
	}

	@Transactional
	public Transaction createTransaction(TransactionDTO transactionDTO) {
		User sender = this.userService.getUserById(transactionDTO.senderId());
		User receiver = this.userService.getUserById(transactionDTO.receiverId());

		validateTransaction(sender, receiver, transactionDTO.value());

		var transaction = new Transaction();
		transaction.setAmount(transactionDTO.value());
		transaction.setSender(sender);
		transaction.setReceiver(receiver);
		transaction.setTimestamp(LocalDateTime.now());

		sender.setBalance(sender.getBalance().subtract(transactionDTO.value()));
		receiver.setBalance(receiver.getBalance().add(transactionDTO.value()));

		Transaction newTransaction = repository.save(transaction);
		userService.updateUser(sender);
		userService.updateUser(receiver);

		try {
			var senderMessage = "Transação de " + transactionDTO.value() + "realizada com sucesso";
			var receiverrMessage = "Transação de " + transactionDTO.value() + "recebida com sucesso";

			notificationService.sendNotification(sender, senderMessage);
			notificationService.sendNotification(receiver, receiverrMessage);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return newTransaction;
	}

	private void validateTransaction(User sender, User receiver, BigDecimal amount) {

		if (sender.getUserType() == UserType.MERCHANT) {
			throw new UserNotAllowedException("Lojistas não podem enviar dinheiro");
		}

		if (sender.getId().equals(receiver.getId())) {
			throw new SelfTransactionException();
		}

		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionAmountException();
		}

		if (sender.getBalance().compareTo(amount) == -1) {
			throw new InsufficientBalanceException("Saldo insuficiente para transferência");
		}

		if (!authService.authorizeTransaction(sender, amount)) {
			throw new UnauthorizedTransactionException("Transferência não autorizada");
		}
	}

	public List<Transaction> getAllTransactions() {

		return repository.findAll();
	}
}
