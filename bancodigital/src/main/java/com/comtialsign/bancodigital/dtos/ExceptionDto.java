package com.comtialsign.bancodigital.dtos;

import org.springframework.http.HttpStatus;

/**
 *Record DTO do que vai de uma Exception
 *
 * @param message
 * @param status
 */
public record ExceptionDto(String message, HttpStatus status) {
}
