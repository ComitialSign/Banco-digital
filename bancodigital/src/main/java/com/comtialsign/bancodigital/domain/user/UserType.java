package com.comtialsign.bancodigital.domain.user;

/**
 * Representa que tipo de conta o usuário é
 *
 * <p>O usuário pode escolher entre a comum (COMMON), podendo fazer transferência e receber dinheiro, ou ser
 * um mercante (MERCHANT), que não pode fazer transferência, apenas receber.</p>
 */
public enum UserType {
    COMMON,
    MERCHANT
}
