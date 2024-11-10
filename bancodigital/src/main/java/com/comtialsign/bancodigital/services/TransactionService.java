package com.comtialsign.bancodigital.services;

import com.comtialsign.bancodigital.domain.transaction.Transaction;
import com.comtialsign.bancodigital.domain.user.User;
import com.comtialsign.bancodigital.dtos.TransactionDto;
import com.comtialsign.bancodigital.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {
    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WebClient webClient;

    @Autowired
    private NotificationService notificationService;

    @Value("${authorization.service.url}")
    private String authorizationServiceUrl;

    @Transactional(timeout = 10)
    public void createTransaction(TransactionDto transaction) throws Exception {
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        userService.validatedTransaction(sender, transaction.value());

        Boolean isAuthorized = this.authorizeTransaction(sender, transaction.value());
        if(!isAuthorized){
            throw new Exception("Transação não autorizada");
        }

        notifyUsers(sender, receiver);

        saveTransaction(sender, receiver, transaction.value());
    }

    private Boolean authorizeTransaction(User sender, BigDecimal value) throws Exception {
        try{
            ResponseEntity<Map<String, Object>> authorizationResponse = webClient.get()
                    .uri(authorizationServiceUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();

            if(authorizationResponse != null && authorizationResponse.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = authorizationResponse.getBody();
                if (responseBody != null && "success".equals(responseBody.get("status"))) {
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                    if (data != null) {
                        return Boolean.TRUE.equals(data.get("authorization"));
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("Erro ao tentar se comunicar com o validador");
        }
        return false;
    }

    private void saveTransaction(User sender, User receiver, BigDecimal value) throws Exception {
        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(value);
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTimestamp(LocalDateTime.now());

        updateBalances(sender, receiver, value);

        this.transactionRepository.save(newTransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);
    }

    private void updateBalances(User sender, User receiver, BigDecimal value) {
        sender.setBalance(sender.getBalance().subtract(value));
        receiver.setBalance(receiver.getBalance().add(value));
    }

    private void notifyUsers(User sender, User receiver) throws Exception {
        notificationService.sendNotification(sender, "Transação realizada com sucesso");
        notificationService.sendNotification(receiver, "Transação recebida com sucesso");
    }
}
