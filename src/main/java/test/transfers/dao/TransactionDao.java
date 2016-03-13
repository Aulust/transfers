package test.transfers.dao;

import com.google.inject.Inject;
import org.sql2o.Connection;
import test.transfers.model.Transaction;
import test.transfers.service.EntityNotFoundException;
import test.transfers.transaction.JdbcTransactionManager;
import java.math.BigDecimal;

public class TransactionDao {
  private static final String INSERT = "INSERT INTO transaction (account_id, amount, finished) " +
      "VALUES (:accountId, :amount, :finished)";
  private static final String SELECT = "SELECT transaction_id as transactionId, account_id as accountId, amount, finished " +
      "FROM transaction where transaction_id = :transactionId";
  private static final String UPDATE = "UPDATE transaction SET finished = :finished WHERE transaction_id = :transactionId";

  @Inject
  private JdbcTransactionManager transactionManager;

  public Transaction get(Long id) {
    Connection connection = transactionManager.getConnection();
    Transaction transaction = connection.createQuery(SELECT).addParameter("transactionId", id).executeAndFetchFirst(Transaction.class);

    if (transaction == null) {
      throw new EntityNotFoundException();
    }

    return transaction;
  }

  public Transaction create(Long accountId, BigDecimal amount, boolean finished) {
    Transaction transaction = new Transaction();
    transaction.setAccountId(accountId);
    transaction.setAmount(amount);
    transaction.setFinished(finished);

    transaction.setTransactionId(
        transactionManager.getConnection().createQuery(INSERT, true).bind(transaction).executeUpdate().getKey(Long.class));
    return transaction;
  }

  public void update(Transaction transaction) {
    transactionManager.getConnection().createQuery(UPDATE).bind(transaction).executeUpdate();
  }
}
