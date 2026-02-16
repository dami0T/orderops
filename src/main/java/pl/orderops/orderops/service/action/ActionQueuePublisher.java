package pl.orderops.orderops.service.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pl.orderops.orderops.config.RedisConfig;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActionQueuePublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ActionMetrics metrics;

    public void publish(ActionMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            
            Map<String, String> data = new HashMap<>();
            data.put("value", json);
            
            var record = StreamRecords.newRecord()
                    .in(RedisConfig.ACTION_STREAM)
                    .ofMap(data);

            RecordId recordId = redisTemplate.opsForStream().add(record);
            metrics.incrementPublished();
            log.info("Published action message: revisionId={}, eventId={}, recordId={}",
                    message.getRuleRevisionId(), message.getEventId(), recordId);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize action message", e);
            throw new RuntimeException("Failed to publish action message", e);
        }
    }
}
