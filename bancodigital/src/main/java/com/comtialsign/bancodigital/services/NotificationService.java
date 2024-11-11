package com.comtialsign.bancodigital.services;

import com.comtialsign.bancodigital.domain.user.User;
import com.comtialsign.bancodigital.dtos.NotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Serviço responsável pelo envio de notificações a usuários após uma transação for concluida.
 *
 * <p>O {@code NotificationService} usa um {@link WebClient} para fazer chamadas
 * HTTP ao serviço de notificação configurado via {@code notification.service.url}.
 * Este serviço envia mensagens personalizadas para o email dos usuários.</p>
 */
@Service
public class NotificationService {

    @Autowired
    private WebClient webClient;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;


    /**
     * Envia uma notificação para o usuário especificado com uma mensagem customizada.
     *
     * @param user   Usuário que receberá a notificação. O email será obtido a partir deste objeto.
     * @param message Mensagem personalizada que será enviada ao usuário.
     * @throws Exception se ocorrer um erro ao tentar enviar a notificação.
     */
    public void sendNotification(User user, String message) throws Exception{
        String email = user.getEmail();
        NotificationDto notificationDto = new NotificationDto(email, message);


        webClient.post()
                .uri(notificationServiceUrl)
                .bodyValue(notificationDto)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class);
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                })
                .doOnNext(e -> {throw new RuntimeException("Erro ao tentar enviar notificação ao email");});
    }
}
