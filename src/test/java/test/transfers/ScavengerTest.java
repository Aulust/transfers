package test.transfers;

import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import test.transfers.model.Account;
import test.transfers.resource.AccountResource;
import test.transfers.service.DatabaseService;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class ScavengerTest extends TestBase {
  private static final String INSERT = "INSERT INTO transaction (account_id, amount, finished, updated) " +
      "VALUES (:accountId, :amount, :finished, :updated)";

  @Test
  public void testAccountCreation() throws InterruptedException {
    AccountResource accountResource = injector.getInstance(AccountResource.class);
    DatabaseService databaseService = injector.getInstance(DatabaseService.class);

    Account account = accountResource.create(new BigDecimal(10));

    try (Connection connection = injector.getInstance(Sql2o.class).open()) {
      createTransaction(connection, account.getAccountId(), BigDecimal.ONE, false, LocalDateTime.now());
      createTransaction(connection, account.getAccountId(), BigDecimal.ONE, false, LocalDateTime.now().minusMinutes(20));
      createTransaction(connection, account.getAccountId(), BigDecimal.ONE, false, LocalDateTime.now().minusMinutes(20));
      createTransaction(connection, account.getAccountId(), BigDecimal.ONE, false, LocalDateTime.now());
      createTransaction(connection, account.getAccountId(), BigDecimal.ONE, true, LocalDateTime.now().minusMinutes(20));
    }

    databaseService.reQueueStalledTransactions();

    Thread.sleep(1000);

    Account accountFetched = accountResource.get(account.getAccountId());

    assertEquals(account.getBalance().add(new BigDecimal(2)), accountFetched.getBalance());
  }

  private void createTransaction(Connection connection, Long accountId, BigDecimal amount, boolean finished, LocalDateTime updated) {
    connection.createQuery(INSERT)
        .addParameter("accountId", accountId)
        .addParameter("amount", amount)
        .addParameter("updated", updated.toString())
        .addParameter("finished", finished)
        .executeUpdate();
  }
}
