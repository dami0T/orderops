package pl.orderops.orderops.service.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pl.orderops.orderops.config.RedisConfig;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DelayQueueProcessor {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ActionQueuePublisher mainPublisher;

    @PostConstruct
    public void startProcessor() {
        Thread processorThread = new Thread(this::process, "delay-queue-processor");
        processorThread.setDaemon(true);
        processorThread.start();
        log.info("Started delay queue processor thread");
    }

    private void process() {
        log.info("Starting delay queue processor...");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream()
                        .range(DelayQueuePublisher.DELAY_STREAM, 
                               org.springframework.data.domain.Range.unbounded());

                if (messages != null && !messages.isEmpty()) {
                    for (MapRecord<String, Object, Object> record : messages) {
                        try {
                            processDelayMessage(record);
                        } catch (Exception e) {
                            log.error("Error processing delay message {}: {}", record.getId(), e.getMessage());
                        }
                    }
                }

                Thread.sleep(1000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error in delay processor: {}", e.getMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void processDelayMessage(MapRecord<String, Object, Object> record) {
        try {
            String json = (String) record.getValue().get("value");
            ActionMessage message = objectMapper.readValue(json, ActionMessage.class);

            long now = Instant.now().toEpochMilli();
            Long retryAt = message.getRetryAt();

            if (retryAt != null && retryAt <= now) {
                redisTemplate.opsForStream().delete(DelayQueuePublisher.DELAY_STREAM, record.getId());
                
                message.setRetryAt(null);
                mainPublisher.publish(message);
                
                log.info("Moved delayed message to main queue: revisionId={}, eventId={}",
                        message.getRuleRevisionId(), message.getEventId());
            }

        } catch (Exception e) {
            log.error("Error processing delay message: {}", e.getMessage());
        }
    }
}
