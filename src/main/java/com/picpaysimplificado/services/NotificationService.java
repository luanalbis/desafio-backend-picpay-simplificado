package com.picpaysimplificado.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.NotificationDTO;

@Service
public class NotificationService {
	private final RestTemplate restTemplate;

	@Autowired
	public NotificationService(RestTemplate restTemplate) {

		this.restTemplate = restTemplate;
	}

	public void sendNotification(User user, String message) {
		var url = "https://util.devi.tools/api/v1/notify";
		NotificationDTO notificationRequest = new NotificationDTO(user.getEmail(), message);
		ResponseEntity<String> response = restTemplate.postForEntity(url, notificationRequest, String.class);

		if (response.getStatusCode() != HttpStatus.OK) {
			System.out.println("Erro ao enviar notificação");
			throw new IllegalStateException("Serviço de notificação indisponível");
		}

	}
}
