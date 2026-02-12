package pl.orderops.orderops.ruleengine.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.data.annotation.Id;
import pl.orderops.orderops.action.model.ActionType;

@Entity
@Table(name = "rule_actions")
public class RuleAction {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private RuleRevision revision;

  @Enumerated(EnumType.STRING)
  private ActionType type;

  @Column(columnDefinition = "jsonb")
  private String configJson;
}
