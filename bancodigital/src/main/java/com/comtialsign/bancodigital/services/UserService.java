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

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void validatedTransaction(User sender, BigDecimal amount) throws Exception{
        if(sender.getUserType() == UserType.MERCHANT){
            throw new Exception("Usuário do tipo logista não é autorizado a fazer transação");
        }
        if(sender.getBalance().compareTo(amount) < 0){
            throw new Exception("Valor insuficiente");
        }
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public User findUserById(Long id) throws EntityNotFoundException {
        return this.userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }


    public void saveUser(User user) throws Exception{
        this.userRepository.save(user);
    }

    public User createUser(UserDto data) throws Exception{
        User newUser = new User(data);
        this.userRepository.save(newUser);
        return newUser;
    }

    public List<User> findAllUsers() throws Exception{
        return this.userRepository.findAll();
    }
}
