package test.transfers.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadPoolExecutor;

@Singleton
public class QueueService {
  @Inject
  @Named("message.ttl")
  private int messageTtl;

  @Inject
  private ThreadPoolExecutor threadPoolExecutor;

  @Inject
  private TransferService transferService;

  public void submitJob(Long transactionId) {
    LocalDateTime date = LocalDateTime.now();

    threadPoolExecutor.submit(() -> {
      if (date.plusSeconds(messageTtl).isBefore(LocalDateTime.now())) {
        return;
      }

      transferService.processTransaction(transactionId);
    });
  }
}
