package pl.orderops.orderops.ruleengine.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

@Entity
@Table(name = "rule_revision")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleRevision {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Rule rule;

  private int version;
  private boolean active;

  @Column(columnDefinition = "TEXT")
  private String triggerJson;

  @Column(columnDefinition = "TEXT")
  private String conditionsJson;

  @Column(columnDefinition = "TEXT")
  private String actionsJson;

  private OffsetDateTime createdAt;
}
