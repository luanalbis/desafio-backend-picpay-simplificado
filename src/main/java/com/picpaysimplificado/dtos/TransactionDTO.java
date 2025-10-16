package com.picpaysimplificado.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record TransactionDTO(
		@NotNull(message = "O saldo inicial é obrigatório.")
		@DecimalMin(value = "0.01", inclusive = true, message = "O saldo deve ser positivo.")
		BigDecimal value,
		
		@NotNull(message = "O ID do Sender é obrigatório.")
		Long senderId,
		
		@NotNull(message = "O ID do Receiver é é obrigatório.")
		Long receiverId) {

}
