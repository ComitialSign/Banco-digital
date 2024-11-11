package com.comtialsign.bancodigital.domain.transaction;

import com.comtialsign.bancodigital.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa o que será necessário para realizar um transação
 *
 * <p>
 *      Nela vai ser passado o Usuário e a quantidade de quem vai efetuar a tranferência de dinheiro para o Usuário que vai receber,
 *      Cada transação conta com um Id e salvará a data e hora de quando foi efetuado
 * </p>
 */
@Entity(name = "transactions")
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Transaction {

    //Id da tranferência
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Quantidade de dinheiro que será enviada
    private BigDecimal amount;

    //Usuário que vai realizar a transação
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    //Usuário que vai receber
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    //Data e Hora da tranferência
    private LocalDateTime timestamp;
}
