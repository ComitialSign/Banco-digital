package com.comtialsign.bancodigital.dtos;

/**
 * Record DTO do que será enviado para o email
 *
 * @param email
 * @param message
 */
public record NotificationDto(String email, String message) {
}
