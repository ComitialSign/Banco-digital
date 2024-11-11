package com.comtialsign.bancodigital.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


/**
 * Configuração do Bean para o WebClient funcionar na aplicação
 *
 * <p>
 *     Ele é utilizado para pegar URL externas da aplicação e trazer o JSON do conteúdo delas.
 * </p>
 *
 * Aonde ela é utilizada:
 * {@link com.comtialsign.bancodigital.services.NotificationService}
 * {@link com.comtialsign.bancodigital.services.TransactionService}
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
