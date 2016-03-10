package test.transfers.resource;

import com.google.inject.Inject;
import test.transfers.model.Account;
import test.transfers.service.AccountService;
import test.transfers.service.TransferService;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.math.BigDecimal;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/transfer")
@Singleton
public class TransferResource {
  @Inject
  TransferService transferService;

  @Inject
  AccountService accountService;

  @POST
  @Produces(APPLICATION_JSON)
  public Account transfer(@FormParam("from") Long from, @FormParam("to") Long to, @FormParam("amount") BigDecimal amount) {
    transferService.transfer(from, to, amount);
    return accountService.getAccount(from);
  }
}
