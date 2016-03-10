package test.transfers.transaction;

import com.google.inject.Inject;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class JdbcTransactionManager {
  private static ThreadLocal<Connection> connections = new ThreadLocal<>();

  @Inject
  private Sql2o sql2o;

  public Connection getConnection() {
    Connection connection = connections.get();

    if (connection == null) {
      throw new IllegalStateException("Trying to obtain a connection without opening");
    }

    return connection;
  }

  public boolean createIfNotExists(boolean transactional) {
    if (connections.get() != null) {
      return false;
    }

    Connection connection;
    if (transactional) {
      connection = sql2o.beginTransaction();
    } else {
      connection = sql2o.open();
    }

    connections.set(connection);
    return true;
  }

  public void close() {
    Connection connection = connections.get();

    if (connection != null) {
      connection.close();
    }

    connections.remove();
  }

  public void commit() {
    Connection connection = connections.get();

    if (connection != null) {
      connection.commit(true);
    }

    connections.remove();
  }

  public void rollback() {
    Connection connection = connections.get();

    if (connection != null) {
      connection.rollback(true);
    }

    connections.remove();
  }
}
