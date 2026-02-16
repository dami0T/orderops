package pl.orderops.orderops.action.model;

import jakarta.persistence.Column;
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
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "action_execution")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionExecution {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long ruleRevisionId;

  @Column(name = "action_index")
  private int actionIndex; // pozycja w actions[]

  private Long eventId;

  @Enumerated(EnumType.STRING)
  private ActionStatus status;

  private int attempts;

  private OffsetDateTime nextRetryAt;

  @Column(columnDefinition = "TEXT")
  private String lastError;

  private OffsetDateTime createdAt;

  private OffsetDateTime completedAt;
}