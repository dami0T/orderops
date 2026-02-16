package pl.orderops.orderops.ruleengine.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import pl.orderops.orderops.action.model.ActionType;

@Entity
@Table(name = "rule_actions")
public class RuleAction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rule_revision_id")
  private RuleRevision revision;

  @Enumerated(EnumType.STRING)
  private ActionType type;

  @Column(columnDefinition = "TEXT")
  private String configJson;
}
