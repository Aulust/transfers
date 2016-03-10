package test.transfers.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sql2o.Connection;
import test.transfers.model.Account;
import test.transfers.service.EntityNotFoundException;
import test.transfers.transaction.JdbcTransactionManager;
import java.math.BigDecimal;

@Singleton
public class AccountDao {
  private static final String INSERT = "INSERT INTO account (balance) VALUES (:balance)";
  private static final String SELECT = "SELECT account_id as accountId, balance from account WHERE account_id = :accountId";
  private static final String SELECT_FOR_UPDATE = "SELECT account_id as accountId, balance FROM account WHERE account_id = :accountId FOR UPDATE";
  private static final String UPDATE = "UPDATE account SET balance = :balance WHERE account_id = :accountId";

  @Inject
  private JdbcTransactionManager transactionManager;

  public Account get(Long id) {
    return get(id, false);
  }

  public Account getForUpdate(Long id) {
    return get(id, true);
  }

  private Account get(Long id, boolean locked) {
    Connection connection = transactionManager.getConnection();
    String query = locked ? SELECT_FOR_UPDATE : SELECT;
    Account account = connection.createQuery(query).addParameter("accountId", id).executeAndFetchFirst(Account.class);

    if (account == null) {
      throw new EntityNotFoundException();
    }

    return account;
  }

  public Account create(BigDecimal balance) {
    Connection connection = transactionManager.getConnection();
    Long id = connection.createQuery(INSERT, true).addParameter("balance", balance).executeUpdate().getKey(Long.class);

    return get(id);
  }

  public void update(Account account) {
    transactionManager.getConnection().createQuery(UPDATE).bind(account).executeUpdate();
  }
}
