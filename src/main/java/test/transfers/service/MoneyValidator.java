package test.transfers.service;

import java.math.BigDecimal;

public class MoneyValidator {
  private static final BigDecimal LIMIT = new BigDecimal(100000000);

  public static void validate(BigDecimal amount) {
    if (amount.scale() > 2) {
      throw new IllegalArgumentException("Invalid scale");
    }
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Negative value");
    }
    if (amount.abs().compareTo(LIMIT) > 0) {
      throw new IllegalArgumentException("Value is too big");
    }
  }
}
