package com.comtialsign.bancodigital.services.impls;

import com.comtialsign.bancodigital.domain.user.User;
import com.comtialsign.bancodigital.dtos.NotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class NotificationService {

    @Autowired
    private WebClient webClient;

    public void sendNotification(User user, String message) {
        String email = user.getEmail();
        NotificationDto notificationDto = new NotificationDto(email, message);

        webClient.post()
                .uri("https://util.devi.tools/api/v1/notify")
                .bodyValue(notificationDto)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class);
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                })
                .doOnError(ex -> System.out.println("Error: " + ex.getMessage()));
    }
}
