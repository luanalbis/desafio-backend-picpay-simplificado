package com.picpaysimplificado.dtos;

import java.math.BigDecimal;

import com.picpaysimplificado.domain.user.UserType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDTO(

    @NotBlank(message = "O primeiro nome é obrigatório.")
    String firstName,

    @NotBlank(message = "O sobrenome é obrigatório.")
    String lastName,

    @NotBlank(message = "O documento é obrigatório.")
    @Size(min = 11, max = 14, message = "O documento deve ter entre 11 e 14 caracteres.")
    String document,

    @NotNull(message = "O saldo inicial é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O saldo deve ser positivo.")
    BigDecimal balance,

    @Email(message = "E-mail inválido.")
    @NotBlank(message = "O e-mail é obrigatório.")
    String email,

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
    String password,

    @NotNull(message = "O tipo de usuário é obrigatório.")
    UserType userType

) {}
