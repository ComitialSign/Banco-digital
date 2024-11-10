package com.comtialsign.bancodigital.dtos;

import org.springframework.http.HttpStatus;

public record ExceptionDto(String message, HttpStatus status) {
}
