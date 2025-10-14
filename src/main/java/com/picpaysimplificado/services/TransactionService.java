package com.picpaysimplificado.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dto.TransactionDTO;
import com.picpaysimplificado.repositories.TransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {
	private final TransactionRepository repository;
	private final UserService userService;
	private final RestTemplate restTemplate;

	@Autowired
	public TransactionService(TransactionRepository repository, UserService userService, RestTemplate restTemplate) {
		this.repository = repository;
		this.userService = userService;
		this.restTemplate = restTemplate;
	}

	@Transactional
	public void createTransaction(TransactionDTO transactionDTO) {
		User sender = this.userService.findUserById(transactionDTO.senderId());
		User receiver = this.userService.findUserById(transactionDTO.receiverId());

		validateTransaction(sender, receiver, transactionDTO.value());

		Transaction transaction = new Transaction();
		transaction.setAmount(transactionDTO.value());
		transaction.setSender(sender);
		transaction.setReceiver(receiver);
		transaction.setTimestamp(LocalDateTime.now());

		sender.setBalance(sender.getBalance().subtract(transactionDTO.value()));
		receiver.setBalance(receiver.getBalance().add(transactionDTO.value()));

		repository.save(transaction);
		userService.saveUser(sender);
		userService.saveUser(receiver);
	}

	public void validateTransaction(User sender, User receiver, BigDecimal amount) {

		if (sender.getUserType() == UserType.MERCHANT) {
			throw new IllegalArgumentException("Lojistas não podem enviar dinheiro");
		}

		if (sender.getBalance().compareTo(amount) == -1) {
			throw new IllegalArgumentException("Saldo insuficiente para transferência");
		}

		if (!authorizeTransaction(sender, amount)) {
			throw new IllegalStateException("Transferência não autorizada");
		}

	}

	public boolean authorizeTransaction(User sender, BigDecimal value) {
		String url = "https://util.devi.tools/api/v2/authorize";
		return restTemplate.getForEntity(url, Map.class).getStatusCode() == HttpStatus.OK;
	}
}
