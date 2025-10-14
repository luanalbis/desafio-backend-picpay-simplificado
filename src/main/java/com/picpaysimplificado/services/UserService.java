package com.picpaysimplificado.services;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	private final UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public User findUserById(Long id) {
		return userRepository.findUserById(id).orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));
	}

	public void saveUser(User user) {
		this.userRepository.save(user);
	}
}
