package com.comtialsign.bancodigital.services;

import com.comtialsign.bancodigital.domain.user.User;
import com.comtialsign.bancodigital.dtos.NotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class NotificationService {

    @Autowired
    private WebClient webClient;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

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
