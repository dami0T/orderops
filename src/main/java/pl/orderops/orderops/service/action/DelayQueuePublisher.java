package pl.orderops.orderops.service.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pl.orderops.orderops.config.RedisConfig;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DelayQueuePublisher {

    public static final String DELAY_STREAM = "orderops:actions-delay";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publishDelayed(ActionMessage message, long delaySeconds) {
        long retryAt = Instant.now().plusSeconds(delaySeconds).toEpochMilli();
        
        message.setRetryAt(retryAt);
        publish(message);
        
        log.info("Scheduled retry for revisionId={}, eventId={} at {} (delay={}s)",
                message.getRuleRevisionId(), message.getEventId(), retryAt, delaySeconds);
    }

    public void publish(ActionMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            
            Map<String, String> data = new HashMap<>();
            data.put("value", json);
            
            var record = StreamRecords.newRecord()
                    .in(DELAY_STREAM)
                    .ofMap(data);

            RecordId recordId = redisTemplate.opsForStream().add(record);
            log.debug("Published to delay stream: recordId={}", recordId);
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize action message", e);
            throw new RuntimeException("Failed to publish action message", e);
        }
    }
}
