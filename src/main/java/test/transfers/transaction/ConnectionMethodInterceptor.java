package test.transfers.transaction;

import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionMethodInterceptor implements MethodInterceptor {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionMethodInterceptor.class);

  @Inject
  private JdbcTransactionManager transactionManager;

  @Override
  public Object invoke(MethodInvocation methodInvocation) throws Throwable {
    try {
      boolean created = transactionManager.createIfNotExists(false);

      Object result = methodInvocation.proceed();

      if (created) {
        transactionManager.close();
      }

      return result;
    } catch (Exception e) {
      try {
        transactionManager.close();
      } catch (Exception ex) {
        LOGGER.error("Failed to lose connection", ex);
      }
      throw e;
    }
  }
}
