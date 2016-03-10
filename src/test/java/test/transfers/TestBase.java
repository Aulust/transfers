package test.transfers;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import org.junit.BeforeClass;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import test.transfers.service.DatabaseService;

public class TestBase {
  protected static Injector injector;

  @BeforeClass
  public static void prepareEnvironment() throws Exception {
    injector = Guice.createInjector(Stage.PRODUCTION, new TestConfigurationModule(), new ServerModule(),
        new DatabaseModule(), new QueueModule());
    injector.getInstance(DatabaseService.class).initDatabase();
  }

  protected static boolean hasUnfinishedTransactions() {
    try (Connection connection = injector.getInstance(Sql2o.class).open()) {
      return connection.createQuery("SELECT count(*) FROM transaction WHERE finished = false").executeAndFetchFirst(Long.class) > 0;
    }
  }
}
