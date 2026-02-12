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

  @ManyToOne(optional = false)
  private Tenant tenant;

  private String name;

  private boolean active = true;

  // soft delete
  private boolean deleted = false;
  private OffsetDateTime deletedAt;
}
