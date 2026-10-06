package example.roommate.Application.Service;

import example.roommate.Domain.model.Room.Key;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Component
@ConditionalOnProperty(name = "roommate.keymaster.enabled", havingValue = "true", matchIfMissing = true)
public class KeySynchronizerService {
    private KeyService keyService;

    public KeySynchronizerService(KeyService keyService) {
        this.keyService = keyService;
    }
    @Scheduled(fixedDelay = 10000)
    public void fetchEvents() {
        List<Key> keys = WebClient.create()
                .get()
                .uri(
                        uriBuilder -> uriBuilder
                                .scheme("http")
                                .host("localhost")
                                .port(3000)
                                .path("key")
                                .build()
                )
                .retrieve()
                .bodyToFlux(Key.class)
                .collectList()
                .block(Duration.of(8, ChronoUnit.SECONDS));
        for (Key key : keys) {
            processEvent(key);
        }
    }

    private void processEvent(Key key) {
        if(keyService.findByUUID(key.getId()).isEmpty()){
            keyService.saveKey(key);
        }
    }
}
