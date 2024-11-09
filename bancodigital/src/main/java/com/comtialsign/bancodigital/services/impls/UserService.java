package com.comtialsign.bancodigital.services.impls;

import com.comtialsign.bancodigital.domain.user.User;
import com.comtialsign.bancodigital.domain.user.UserType;
import com.comtialsign.bancodigital.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

    public User findUserById(Long id) throws Exception{
        return this.userRepository.findUserById(id).orElseThrow(() -> new Exception("Usuário não encontrado"));
    }

    public void saveUser(User user) throws Exception{
        this.userRepository.save(user);
    }
}
