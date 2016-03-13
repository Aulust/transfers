package test.transfers;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Named;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.sql2o.Sql2o;
import org.sql2o.quirks.PostgresQuirks;
import test.transfers.service.AccountService;
import test.transfers.service.DatabaseService;
import test.transfers.service.TransferService;
import test.transfers.transaction.JdbcTransactionManager;
import test.transfers.transaction.OpenConnection;
import test.transfers.transaction.OpenTransaction;
import test.transfers.transaction.ConnectionMethodInterceptor;
import test.transfers.transaction.TransactionMethodInterceptor;

import javax.sql.DataSource;

public class DatabaseModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(DatabaseService.class).asEagerSingleton();
    bind(AccountService.class).asEagerSingleton();
    bind(TransferService.class).asEagerSingleton();

    bind(JdbcTransactionManager.class);
    ConnectionMethodInterceptor connectionMethodInterceptor = new ConnectionMethodInterceptor();
    TransactionMethodInterceptor transactionalMethodInterceptor = new TransactionMethodInterceptor();
    requestInjection(connectionMethodInterceptor);
    requestInjection(transactionalMethodInterceptor);
    bindInterceptor(Matchers.any(), Matchers.annotatedWith(OpenConnection.class), connectionMethodInterceptor);
    bindInterceptor(Matchers.any(), Matchers.annotatedWith(OpenTransaction.class), transactionalMethodInterceptor);
  }

  @Provides
  @Singleton
  DataSource createDataSource(@Named("jdbc.url") String jdbcUrl,
                              @Named("jdbc.user") String user,
                              @Named("jdbc.password") String password) {
    ComboPooledDataSource dataSource = new ComboPooledDataSource();
    dataSource.setJdbcUrl(jdbcUrl);
    dataSource.setUser(user);
    dataSource.setPassword(password);

    dataSource.setUnreturnedConnectionTimeout(200);
    dataSource.setMinPoolSize(5);
    dataSource.setInitialPoolSize(5);
    dataSource.setAcquireIncrement(5);
    dataSource.setMaxPoolSize(20);
    dataSource.setMaxStatements(180);
    dataSource.setAutoCommitOnClose(true);

    return dataSource;
  }

  @Provides
  @Singleton
  @Inject
  Sql2o createSql2o(DataSource dataSource) {
    return new Sql2o(dataSource, new PostgresQuirks());
  }
}
