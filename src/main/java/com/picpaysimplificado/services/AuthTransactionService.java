package com.picpaysimplificado.services;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.exceptions.UnauthorizedTransactionException;

public class AuthTransactionService {
	private final RestTemplate restTemplate;

	@Autowired
	public AuthTransactionService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;

	}

	public boolean authorizeTransaction(User sender, BigDecimal value) {
		var url = "https://util.devi.tools/api/v2/authorize";
		try {
			return restTemplate.getForEntity(url, Map.class).getStatusCode() == HttpStatus.OK;
		} catch (HttpClientErrorException.Forbidden ex) {

			throw new UnauthorizedTransactionException();
		} catch (Exception ex) {

			throw new RuntimeException();
		}
	}
}
