package com.picpaysimplificado.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.repositories.UserRepository;

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

	public User saveUser(User user) {
		return this.userRepository.save(user);
	}

	public User createUser(UserDTO data) {
		return this.saveUser(new User(data));
	}

	public List<User> getAllUsers() {

		return userRepository.findAll();
	}
}
