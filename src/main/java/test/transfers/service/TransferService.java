package test.transfers.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import test.transfers.dao.AccountDao;
import test.transfers.dao.TransactionDao;
import test.transfers.model.Account;
import test.transfers.model.Transaction;
import test.transfers.transaction.OpenTransaction;
import java.math.BigDecimal;

@Singleton
public class TransferService {
  @Inject
  private AccountDao accountDao;

  @Inject
  private TransactionDao transactionDao;

  @Inject
  private QueueService queueService;

  public void transfer(Long from, Long to, BigDecimal amount) {
    MoneyValidator.validate(amount);

    Transaction transaction = transferInternal(from, to, amount);

    queueService.submitJob(transaction.getTransactionId());
  }

  @OpenTransaction
  public Transaction transferInternal(Long from, Long to, BigDecimal amount) {
    Account accountFrom = accountDao.getForUpdate(from);
    Account accountTo = accountDao.get(to);

    if (accountFrom.getBalance().compareTo(amount) < 0) {
      throw new IllegalArgumentException("Insufficient funds");
    }

    accountFrom.setBalance(accountFrom.getBalance().subtract(amount));

    accountDao.update(accountFrom);
    return transactionDao.create(accountTo.getAccountId(), amount, false);
  }

  @OpenTransaction
  public void processTransaction(Long transactionId) {
    Transaction transaction = transactionDao.get(transactionId);
    Account account = accountDao.getForUpdate(transaction.getAccountId());

    transaction.setFinished(true);
    account.setBalance(account.getBalance().add(transaction.getAmount()));

    accountDao.update(account);
    transactionDao.update(transaction);
  }
}
