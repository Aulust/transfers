package test.transfers;

import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import test.transfers.model.Account;
import test.transfers.resource.AccountResource;
import test.transfers.service.DatabaseService;
import test.transfers.service.QueueService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class ScavengerTest extends TestBase {
  private static final String INSERT = "INSERT INTO transaction (account_id, amount, finished, updated) " +
      "VALUES (:accountId, :amount, :finished, :updated)";

  @Test
  public void testScavenger() {
    AccountResource accountResource = injector.getInstance(AccountResource.class);
    DatabaseService databaseService = injector.getInstance(DatabaseService.class);
    QueueService queueService = injector.getInstance(QueueService.class);

    Account account = accountResource.create(new BigDecimal(10));

    try (Connection connection = injector.getInstance(Sql2o.class).open()) {
      queueService.submitJob(
          createTransaction(connection, account.getAccountId(), BigDecimal.ONE, false, LocalDateTime.now()));

      createTransaction(connection, account.getAccountId(), BigDecimal.ONE, false, LocalDateTime.now().minusMinutes(20));
      createTransaction(connection, account.getAccountId(), BigDecimal.ONE, false, LocalDateTime.now().minusMinutes(20));

      queueService.submitJob(
          createTransaction(connection, account.getAccountId(), BigDecimal.ONE, false, LocalDateTime.now()));
      createTransaction(connection, account.getAccountId(), BigDecimal.ONE, true, LocalDateTime.now().minusMinutes(20));
    }

    int processed = databaseService.reQueueStalledTransactions();
    assertEquals(2, processed);

    waitTransactionsFinished();

    Account accountFetched = accountResource.get(account.getAccountId());

    assertEquals(account.getBalance().add(new BigDecimal(4)), accountFetched.getBalance());
  }

  private Long createTransaction(Connection connection, Long accountId, BigDecimal amount, boolean finished, LocalDateTime updated) {
    return connection.createQuery(INSERT, true)
        .addParameter("accountId", accountId)
        .addParameter("amount", amount)
        .addParameter("updated", Timestamp.valueOf(updated))
        .addParameter("finished", finished)
        .executeUpdate()
        .getKey(Long.class);
  }
}
