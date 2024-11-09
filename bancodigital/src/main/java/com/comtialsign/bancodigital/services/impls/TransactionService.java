package com.comtialsign.bancodigital.services.impls;

import com.comtialsign.bancodigital.domain.transaction.Transaction;
import com.comtialsign.bancodigital.domain.user.User;
import com.comtialsign.bancodigital.dtos.TransactionDto;
import com.comtialsign.bancodigital.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {
    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private WebClient webClient;

    public void createTransaction(TransactionDto transaction) throws Exception {
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        userService.validatedTransaction(sender, transaction.value());

        Boolean isAuthorized = this.authorizeTransaction(sender, transaction.value());
        if(!isAuthorized){
            throw new Exception("Transação não autorizada");
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transaction.value());
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        this.repository.save(newTransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);
    }

    public Boolean authorizeTransaction(User sender, BigDecimal value) {
        try{
            ResponseEntity<Map<String, String>> authorizationResponse = webClient.get()
                    .uri("https://util.devi.tools/api/v2/authorize")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, String>>() {
                    })
                    .block();

            if(authorizationResponse != null && authorizationResponse.getStatusCode().is2xxSuccessful()) {
                Map<String, String> responseBody = authorizationResponse.getBody();
                if (responseBody != null) {
                    String message = responseBody.get("status");
                    return "success".equals(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
