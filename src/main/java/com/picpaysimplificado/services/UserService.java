package com.picpaysimplificado.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.exceptions.DuplicateUserException;
import com.picpaysimplificado.exceptions.UserNotFoundException;
import com.picpaysimplificado.repositories.UserRepository;

@Service
public class UserService {
	private final UserRepository repository;

	@Autowired
	public UserService(UserRepository repository) {
		this.repository = repository;
	}

	public User createUser(UserDTO data) {
		User user = new User(data);
		if (repository.existsByEmail(user.getEmail()) || repository.existsByDocument(user.getDocument())) {
			throw new DuplicateUserException();
		}
		return repository.save(user);
	}

	public List<User> getAllUsers() {
		return repository.findAll();
	}

	public User updateUser(User user) {
		User finded = repository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException());
		if (!user.getDocument().equals(finded.getDocument()) && repository.existsByDocument(user.getDocument())) {
			throw new DuplicateUserException("Documento já cadastrado");
		}

		if (!user.getEmail().equals(finded.getEmail()) && repository.existsByEmail(user.getEmail())) {
			throw new DuplicateUserException("Email já cadastrado");
		}

		return repository.save(user);
	}

	public User getUserById(Long id) {
		return repository.findUserById(id).orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
	}

}
