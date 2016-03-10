package test.transfers;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import test.transfers.service.QueueService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class QueueModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(QueueService.class).asEagerSingleton();
  }

  @Provides
  @Singleton
  ThreadPoolExecutor threadPoolExecutor(@Named("pool.size") int poolSize) {
    return new ThreadPoolExecutor(poolSize, poolSize, 10, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
  }
}
