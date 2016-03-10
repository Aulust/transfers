package test.transfers;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import test.transfers.model.Account;
import javax.ws.rs.core.MultivaluedMap;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class ResourceTest extends TestBase {
  @BeforeClass
  public static void prepare() throws Exception {
    Server server = injector.getInstance(Server.class);
    server.start();
  }

  @AfterClass
  public static void clean() throws Exception {
    injector.getInstance(Server.class).stop();
  }

  @Test
  public void testAccountCreation() {
    Client client = Client.create(new DefaultClientConfig());
    WebResource resource = client.resource("http://localhost:19999");

    MultivaluedMap<String, String> params = new MultivaluedMapImpl();
    params.add("balance", "100");

    Account accountCreated = resource.path("account").post(Account.class, params);
    Account accountFetched = resource.path("account/" + accountCreated.getAccountId()).get(Account.class);

    assertEquals(accountCreated.getBalance(), accountFetched.getBalance());
  }

  @Test
  public void testTransfer() {
    Client client = Client.create(new DefaultClientConfig());
    WebResource resource = client.resource("http://localhost:19999");

    MultivaluedMap<String, String> params = new MultivaluedMapImpl();
    params.add("balance", "100");

    Account aliceAccount = resource.path("account").post(Account.class, params);
    Account bobAccount = resource.path("account").post(Account.class, params);

    params = new MultivaluedMapImpl();
    params.add("from", aliceAccount.getAccountId().toString());
    params.add("to", bobAccount.getAccountId().toString());
    params.add("amount", "30");

    Account accountAfterTransfer = resource.path("transfer").post(Account.class, params);

    assertEquals(aliceAccount.getBalance().subtract(new BigDecimal(30)), accountAfterTransfer.getBalance());
  }
}
