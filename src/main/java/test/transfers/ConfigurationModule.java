package test.transfers;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationModule extends AbstractModule {
  @Override
  protected void configure() {
    try {
      Properties properties = new Properties();
      properties.load(new FileReader(System.getProperty("ConfigFile")));
      Names.bindProperties(binder(), properties);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
