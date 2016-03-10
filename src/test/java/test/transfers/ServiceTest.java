package test.transfers;

import org.junit.Test;
import test.transfers.model.Account;
import test.transfers.resource.AccountResource;
import test.transfers.resource.TransferResource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class ServiceTest extends TestBase {
  @Test
  public void testAccountCreation() {
    AccountResource resource = injector.getInstance(AccountResource.class);

    Account accountCreated = resource.create(new BigDecimal(10));
    Account accountFetched = resource.get(accountCreated.getAccountId());

    assertEquals(accountCreated.getBalance(), accountFetched.getBalance());
  }

  @Test
  public void testTransfer() throws InterruptedException {
    AccountResource accountResource = injector.getInstance(AccountResource.class);
    TransferResource transferResource = injector.getInstance(TransferResource.class);

    Account aliceAccount = accountResource.create(new BigDecimal(1001));
    Account bobAccount = accountResource.create(new BigDecimal(10));

    List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> {
      for (int j = 0; j < 100; j++) {
        transferResource.transfer(aliceAccount.getAccountId(), bobAccount.getAccountId(), BigDecimal.ONE);
      }
    })).collect(Collectors.toList());

    threads.forEach(Thread::run);
    threads.forEach((thread) -> {
      try {
        thread.join();
      } catch (InterruptedException ignored) { }
    });

    while (hasUnfinishedTransactions()) {
      Thread.sleep(200);
    }
    Thread.sleep(1000);

    assertEquals(aliceAccount.getBalance().subtract(new BigDecimal(1000)), accountResource.get(aliceAccount.getAccountId()).getBalance());
    assertEquals(bobAccount.getBalance().add(new BigDecimal(1000)), accountResource.get(bobAccount.getAccountId()).getBalance());
  }
}
