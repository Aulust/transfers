package test.transfers;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import org.junit.BeforeClass;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import test.transfers.service.DatabaseService;

public class TestBase {
  private static final String COUNT_NOT_FINISHED_TRANSACTIONS = "SELECT count(*) FROM transaction WHERE finished = false";

  protected static Injector injector;

  @BeforeClass
  public static void prepareEnvironment() throws Exception {
    injector = Guice.createInjector(Stage.PRODUCTION, new TestConfigurationModule(), new ServerModule(),
        new DatabaseModule(), new QueueModule());
    injector.getInstance(DatabaseService.class).initDatabase();
  }

  protected static void waitTransactionsFinished() {
    while (true) {
      try (Connection connection = injector.getInstance(Sql2o.class).open()) {
        if (connection.createQuery(COUNT_NOT_FINISHED_TRANSACTIONS).executeAndFetchFirst(Long.class) == 0) {
          return;
        }
      }

      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
