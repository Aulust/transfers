package test.transfers;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import test.transfers.mappers.EntityNotFoundExceptionMapper;
import test.transfers.mappers.IllegalArgumentExceptionMapper;
import test.transfers.resource.AccountResource;
import test.transfers.resource.TransferResource;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class ServerModule extends ServletModule {
  @Override
  protected void configureServlets() {
    bind(DefaultServlet.class).in(Singleton.class);

    bind(AccountResource.class).in(Singleton.class);
    bind(TransferResource.class).in(Singleton.class);

    bind(IllegalArgumentExceptionMapper.class).in(Singleton.class);
    bind(EntityNotFoundExceptionMapper.class).in(Singleton.class);

    Map<String, String> options = new HashMap<>();
    options.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
    options.put("com.sun.jersey.config.property.packages", "com.fasterxml.jackson.jaxrs.json");

    serve("/*").with(GuiceContainer.class, options);
  }

  @Provides
  @Singleton
  Server createServer(@Named("jetty.port") int port, @Named("jetty.threads.max") int maxThreads,
                      @Named("jetty.threads.min") int minThreads, @Named("jetty.acceptors") int acceptors,
                      @Named("jetty.selectors") int selectors) {
    ThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads);

    Server server = new Server(threadPool);

    ServerConnector serverConnector = new ServerConnector(server, acceptors, selectors);
    serverConnector.setPort(port);

    server.addConnector(serverConnector);

    ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
    context.addFilter(GuiceFilter.class, "/*", EnumSet.of(javax.servlet.DispatcherType.REQUEST, javax.servlet.DispatcherType.ASYNC));
    context.addServlet(DefaultServlet.class, "/*");

    return server;
  }
}
