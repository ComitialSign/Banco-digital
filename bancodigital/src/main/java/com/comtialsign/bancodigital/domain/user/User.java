package com.comtialsign.bancodigital.domain.user;

import com.comtialsign.bancodigital.dtos.UserDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Representa um usuário do sistema bancário digital.
 *
 * <p>Esta entidade contém as principais informações de um usuário, como nome,
 * email, documento de identificação e tipo de usuário. A classe inclui um
 * construtor baseado no UserDTO {@link UserDto}.</p>
 */
@Entity(name = "users")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    //ID do usuário
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //Nome do usuário
    private String firstName;

    //Sobrenome do usuário
    private String lastName;

    //Email do usuário, sendo único para o BD
    @Column(unique = true)
    private String email;

    //CPF ou CNPJ do usuário, sendo único para o BD
    @Column(unique = true)
    private String document;

    //Senha do usuário
    private String password;

    //Saldo da conta do usuário
    private BigDecimal balance;

    /** Registra qual o tipo do usuário é baseado na classe UserType {@link UserType} **/
    @Enumerated(EnumType.STRING)
    private UserType userType;


     //Construtor que inicializa um novo usuário com base em um DTO de dados do usuário.
    public User(UserDto data) {
        this.firstName = data.firstName();
        this.lastName = data.lastName();
        this.email = data.email();
        this.document = data.document();
        this.password = data.password();
        this.balance = data.balance();
        this.userType = data.userType();
    }
}
