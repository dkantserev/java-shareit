package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.requests.dto.RequestDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }


    public ResponseEntity<Object> add(RequestDto request, long userId) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> getRequest(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getForId(Long requestId, long userId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getAll(long userId, long from, long size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        String url = String.format("/all?from=%s&size=%s", from, size);
        return get(url, userId, parameters);
    }
}
