package com.comtialsign.bancodigital.services;

import com.comtialsign.bancodigital.domain.user.User;
import com.comtialsign.bancodigital.domain.user.UserType;
import com.comtialsign.bancodigital.dtos.UserDto;
import com.comtialsign.bancodigital.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pela criação e gerenciamento de usuários.
 *
 * <p>Este serviço valida a autorização do usuário para realizar transferência de fundos,
 * cria usuário, os busca pelo ID e retorna todos os usuários localizados no DB.</p>
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Valida se o usuário remetente está autorizado a realizar uma transação.
     *
     * <p>Verifica se o usuário não é do tipo logista e se ele possui saldo suficiente para a transação.</p>
     *
     * @param sender Usuário remetente que deseja realizar a transação.
     * @param amount Valor a ser transferido.
     * @throws Exception se o usuário for do tipo logista ou se o saldo for insuficiente.
     */
    public void validatedTransaction(User sender, BigDecimal amount) throws Exception{
        if(sender.getUserType() == UserType.MERCHANT){
            throw new Exception("Usuário do tipo logista não é autorizado a fazer transação");
        }
        if(sender.getBalance().compareTo(amount) < 0){
            throw new Exception("Valor insuficiente");
        }
    }

    /**
     * Encontra um usuário pelo seu ID com um bloqueio pessimista para operações de escrita.
     *
     * <p>Utiliza {@code LockModeType.PESSIMISTIC_WRITE} para evitar conflitos de atualização em
     * operações simultâneas.</p>
     *
     * @param id Identificador único do usuário.
     * @return Usuário correspondente ao ID.
     * @throws EntityNotFoundException se o usuário não for encontrado.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public User findUserById(Long id) throws EntityNotFoundException {
        return this.userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    /**
     * Salva ou atualiza o usuário no banco de dados.
     *
     * @param user Usuário a ser salvo.
     * @throws Exception se ocorrer algum erro durante a operação de salvamento.
     */
    public void saveUser(User user) throws Exception{
        this.userRepository.save(user);
    }

    /**
     * Cria um novo usuário a partir de um DTO.
     *
     * <p>Instancia um novo {@link User} com base nos dados do {@link UserDto} e salva-o no banco de dados.</p>
     *
     * @param data Dados do usuário encapsulados em um {@code UserDto}.
     * @return O novo usuário criado.
     * @throws Exception se ocorrer algum erro durante a criação do usuário.
     */
    public User createUser(UserDto data) throws Exception{
        User newUser = new User(data);
        this.userRepository.save(newUser);
        return newUser;
    }

    /**
     * Retorna uma lista de todos os usuários cadastrados.
     *
     * @return Lista contendo todos os usuários.
     * @throws Exception se ocorrer algum erro ao buscar os usuários.
     */
    public List<User> findAllUsers() throws Exception{
        return this.userRepository.findAll();
    }
}
