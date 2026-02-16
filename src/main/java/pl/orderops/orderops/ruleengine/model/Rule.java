package pl.orderops.orderops.ruleengine.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import pl.orderops.orderops.model.tenant.Tenant;

@Entity
@Table(name = "rule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tenant_id")
  private Tenant tenant;

  private String name;

  // 🔴 NA JAKI EVENT REAGUJE REGUŁA
  private String eventType;

  private boolean active = true;

  // soft delete
  @Column(name = "deleted")
  private boolean deleted = false;
  private OffsetDateTime deletedAt;
}
