package com.comtialsign.bancodigital.dtos;

import com.comtialsign.bancodigital.domain.user.UserType;

import java.math.BigDecimal;

/**
 * Record DTO do usuário
 *
 * @param firstName
 * @param lastName
 * @param email
 * @param document
 * @param password
 * @param balance
 * @param userType
 */
public record UserDto(String firstName, String lastName, String email, String document, String password, BigDecimal balance, UserType userType) {
}
