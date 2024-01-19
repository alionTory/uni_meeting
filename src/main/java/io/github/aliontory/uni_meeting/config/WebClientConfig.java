package io.github.aliontory.uni_meeting.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;


@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
//        HttpClient httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
//                .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS)));
//        return WebClient.builder().
//                clientConnector(new ReactorClientHttpConnector(httpClient))
//                .baseUrl("https://place.map.kakao.com/main/v/").build();
        // 기본적으로 카카오맵에서 장소를 가져오는 데에만 사용 가능.
        // 다른 주소의 정보를 가져오고 싶다면 mutate() 와 baseUrl("...") 을 실행해야 함.
        return WebClient.create("https://place.map.kakao.com/main/v/");

    }
}
