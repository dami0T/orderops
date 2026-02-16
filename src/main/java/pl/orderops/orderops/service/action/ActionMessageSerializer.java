package pl.orderops.orderops.service.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActionMessageSerializer {

    private final ObjectMapper objectMapper;

    public String serialize(ActionMessage message) throws JsonProcessingException {
        return objectMapper.writeValueAsString(message);
    }
}
