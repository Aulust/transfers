package test.transfers.model;

import java.math.BigDecimal;

public class Account {
  private Long accountId;
  private BigDecimal balance;

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
}
