package pl.orderops.orderops.action.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "action_execution")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionExecution {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long actionId;
  private Long eventId;

  @Enumerated(EnumType.STRING)
  private ExecutionStatus status;

  private int attempts;
  private OffsetDateTime nextRetryAt;
  private String lastError;

  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  private Long ruleRevisionId;
}