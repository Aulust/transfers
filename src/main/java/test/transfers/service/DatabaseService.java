package test.transfers.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Singleton
public class DatabaseService {
  private static final String SELECT_STALLED = "SELECT transaction_id FROM transaction WHERE finished = false AND updated < :updated LIMIT 50";
  private static final String UPDATE_STALLED = "UPDATE transaction SET updated = now() WHERE transaction_id in (:ids)";

  @Inject
  @Named("message.ttl")
  private int messageTtl;

  @Inject
  private QueueService queueService;

  @Inject
  private Sql2o sql2o;

  public void initDatabase() throws IOException {
    String script = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/scheme.sql")))
        .lines().collect(Collectors.joining("\n"));

    try (Connection connection = sql2o.open()) {
      connection.createQuery(script).executeUpdate();
    }
  }

  public void reQueueStalledTransactions() {
    List<Long> ids;

    try (Connection connection = sql2o.open()) {
      ids = connection.createQuery(SELECT_STALLED)
          .addParameter("updated", LocalDateTime.now().minusSeconds(2 * messageTtl).toString())
          .executeAndFetch(Long.class);

      connection.createQuery(UPDATE_STALLED).addParameter("ids", ids.stream().map(Object::toString).collect(Collectors.joining(",")));
    }

    ids.forEach(queueService::submitJob);
  }

  public void initScavenger() {
    new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(this::reQueueStalledTransactions, 0, 2, TimeUnit.SECONDS);
  }
}
