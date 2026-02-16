package pl.orderops.orderops.ruleengine.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RuleCacheRefresher {

  private final RuleRevisionCache cache;

  public void refresh() {
    cache.reloadAll();
  }
}
