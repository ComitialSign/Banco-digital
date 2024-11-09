package com.comtialsign.bancodigital.dtos;

import com.comtialsign.bancodigital.domain.user.UserType;

import java.math.BigDecimal;

public record UserDto(String firstName, String lastName, String email, String document, String password, BigDecimal balance, UserType userType) {
}
