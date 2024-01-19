package io.github.aliontory.uni_meeting.kakaoMap;

import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@Log4j2
public class RequestTest {
    @Test
    public void startRequest() throws InterruptedException {
        request();
        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void request() throws InterruptedException {
        WebClient webClient = WebClient.create("https://place.map.kakao.com/main/v/27080014");
        Mono<String> jsonMono = webClient.get().accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(clientResponse -> {
                    return clientResponse.bodyToMono(String.class);
                });

        String jsonString = jsonMono.block();

        log.info(jsonString);
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

            JSONObject basicInfo = (JSONObject) jsonObject.get("basicInfo");

            long cid = (long) basicInfo.get("cid");
            String placeName = (String) basicInfo.get("placenamefull");
            JSONObject address = (JSONObject) basicInfo.get("address");
            JSONObject newaddr = (JSONObject) address.get("newaddr");
            JSONObject region = (JSONObject) address.get("region");
            String addrbunho = (String) address.get("addrbunho");

            String newaddrfull = null;
            if (newaddr != null)
                newaddrfull = (String) newaddr.get("newaddrfull");
            String regionDetail = (String) region.get("name3");
            String newaddrOuter = (String) region.get("newaddrfullname");

            log.info(cid);
            log.info(placeName);
            if (newaddr != null)
                log.info(newaddrOuter + " " + newaddrfull);
            log.info(newaddrOuter + " " + regionDetail + " " + addrbunho);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
//        jsonMono.subscribe(jsonString -> {
//            JSONParser parser = new JSONParser();
//            try {
//                JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
//                log.info(jsonObject);
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
//        });
    }
}
