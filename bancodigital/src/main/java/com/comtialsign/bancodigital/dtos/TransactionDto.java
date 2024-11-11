package com.comtialsign.bancodigital.dtos;

import java.math.BigDecimal;

/**
 * Record DTO do que será usado para realizar uma tranferência
 *
 * @param value
 * @param senderId
 * @param receiverId
 */

public record TransactionDto(BigDecimal value, Long senderId, Long receiverId) {

}
