package com.picpaysimplificado.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.services.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")

public class TransactionController {

	private final TransactionService service;

	@Autowired
	public TransactionController(TransactionService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody TransactionDTO data) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.createTransaction(data));
	}

	@GetMapping
	public ResponseEntity<List<Transaction>> getAllTransactions() {
		return ResponseEntity.status(HttpStatus.OK).body(service.getAllTransactions());
	}
}
