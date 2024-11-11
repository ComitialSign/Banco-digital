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

/**
 * Serviço responsável pela criação e gerenciamento de transações entre usuários.
 *
 * <p>Este serviço valida a autorização, realiza a transferência de fundos entre usuários,
 * salva as informações da transação e envia notificações de sucesso aos envolvidos.</p>
 */
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

    /**
     * Cria uma nova transação entre dois usuários, com base nas informações fornecidas em {@code TransactionDto}.
     *
     * <p>Este método valida o saldo do remetente, verifica a autorização com um serviço externo,
     * salva a transação no banco de dados e notifica os usuários envolvidos.</p>
     *
     * @param transaction DTO contendo os dados da transação.
     * @throws Exception se a transação não for autorizada ou ocorrer algum erro de comunicação.
     */
    @Transactional(timeout = 10)
    public void createTransaction(TransactionDto transaction) throws Exception {
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        userService.validatedTransaction(sender, transaction.value());

        Boolean isAuthorized = this.authorizeTransaction();
        if(!isAuthorized){
            throw new Exception("Transação não autorizada");
        }

        notifyUsers(sender, receiver);

        saveTransaction(sender, receiver, transaction.value());
    }

    /**
     * Autoriza a transação consultando um serviço externo.
     *
     * <p>Faz uma requisição GET para o serviço de autorização e verifica se a transação é permitida.</p>
     *
     * @return {@code true} se a transação for autorizada; caso contrário, {@code false}.
     * @throws Exception se ocorrer um erro de comunicação com o serviço de autorização.
     */
    private Boolean authorizeTransaction() throws Exception {
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


    /**
     * Salva a transação no banco de dados e atualiza os saldos dos usuários.
     *
     * @param sender Usuário remetente da transação.
     * @param receiver Usuário destinatário da transação.
     * @param value Valor da transação.
     * @throws Exception se ocorrer algum erro durante o salvamento da transação.
     */
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

    /**
     * Atualiza os saldos do remetente e do destinatário da transação.
     *
     * @param sender Usuário que envia a transação.
     * @param receiver Usuário que recebe a transação.
     * @param value Valor a ser transferido.
     */
    private void updateBalances(User sender, User receiver, BigDecimal value) {
        sender.setBalance(sender.getBalance().subtract(value));
        receiver.setBalance(receiver.getBalance().add(value));
    }

    /**
     * Envia notificações para os usuários envolvidos na transação.
     *
     * @param sender Usuário que enviou a transação.
     * @param receiver Usuário que recebeu a transação.
     * @throws Exception se ocorrer um erro durante o envio das notificações.
     */
    private void notifyUsers(User sender, User receiver) throws Exception {
        notificationService.sendNotification(sender, "Transação realizada com sucesso");
        notificationService.sendNotification(receiver, "Transação recebida com sucesso");
    }
}
