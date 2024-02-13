package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

    @Value("${server.application.name:ewm-main-service}")
    private String applicationName;

    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> postHit(HttpServletRequest request) {
        final HitDto hit = HitDto.builder()
                .app(applicationName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(Timestamp.from(Instant.now()).toLocalDateTime())
                .build();
        return post("/hit", hit);
    }

    public ResponseEntity<Object> getStats(String start,
                                           String end,
                                           @Nullable List<String> uris,
                                           boolean unique) {
        StringBuilder uriBuilder = new StringBuilder("/stats/?start={start}&end={end}&unique={unique}");
        Map<String, Object> parameters;

        if (uris == null) {
            parameters = Map.of("start", start,
                    "end", end,
                    "unique", unique);
            return get(uriBuilder.toString(), parameters);
        }
        parameters = Map.of("start", start,
                "end", end,
                "uris", String.join(",", uris),
                "unique", unique);
        uriBuilder.append("&uris={uris}");
        return get(uriBuilder.toString(), parameters);
    }
}
