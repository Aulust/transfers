package test.transfers;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import java.io.IOException;
import java.util.Properties;

public class TestConfigurationModule extends AbstractModule {
  @Override
  protected void configure() {
    try {
      Properties properties = new Properties();
      properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));
      Names.bindProperties(binder(), properties);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
