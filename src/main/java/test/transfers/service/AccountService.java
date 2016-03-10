package test.transfers.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import test.transfers.dao.AccountDao;
import test.transfers.model.Account;
import test.transfers.transaction.OpenConnection;
import java.math.BigDecimal;

@Singleton
public class AccountService {
  @Inject
  private AccountDao accountDao;

  @OpenConnection
  public Account getAccount(Long id) {
    return accountDao.get(id);
  }

  @OpenConnection
  public Account createAccount(BigDecimal balance) {
    MoneyValidator.validate(balance);
    return accountDao.create(balance);
  }
}
