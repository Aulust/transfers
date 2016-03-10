package test.transfers.transaction;

import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionMethodInterceptor implements MethodInterceptor {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionMethodInterceptor.class);

  @Inject
  private JdbcTransactionManager transactionManager;

  @Override
  public Object invoke(MethodInvocation methodInvocation) throws Throwable {
    try {
      boolean created = transactionManager.createIfNotExists(true);

      Object result = methodInvocation.proceed();

      if (created) {
        transactionManager.commit();
      }

      return result;
    } catch (Exception e) {
      LOGGER.error("Exception in transaction", e);
      try {
        transactionManager.rollback();
      } catch (Exception ex) {
        LOGGER.error("Failed to rollback transaction", ex);
      }
      throw e;
    }
  }
}
