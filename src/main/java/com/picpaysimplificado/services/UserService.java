package com.picpaysimplificado.services;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.repositories.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void validateTransaction(User sender, User receiver, BigDecimal amount) {

		sender = userRepository.findUserByDocument(sender.getDocument())
				.orElseThrow(() -> new NoSuchElementException("Usuário remetente não encontrado"));

		receiver = userRepository.findUserByDocument(receiver.getDocument())
				.orElseThrow(() -> new NoSuchElementException("Usuário destinatário não encontrado"));

		if (sender.getUserType() == UserType.MERCHANT) {
			throw new IllegalArgumentException("Lojistas não podem enviar dinheiro");
		}

		if (sender.getBalance().compareTo(amount) == -1) {
			throw new IllegalArgumentException("Saldo insuficiente para transferência");
		}

	}

	public User findUserById(Long id) {
		return userRepository.findUserById(id).orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));
	}

	public void saveUser(User user) {
		this.userRepository.save(user);
	}
}
