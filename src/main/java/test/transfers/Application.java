package test.transfers;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import org.eclipse.jetty.server.Server;
import test.transfers.service.DatabaseService;

public class Application {
  public static void main(String... args) throws Exception {
    Injector injector = Guice.createInjector(Stage.PRODUCTION, new ConfigurationModule(), new ServerModule(),
        new DatabaseModule(), new QueueModule());

    injector.getInstance(DatabaseService.class).initDatabase();
    injector.getInstance(DatabaseService.class).initScavenger();

    Server server = injector.getInstance(Server.class);

    server.start();
    server.join();
  }
}
