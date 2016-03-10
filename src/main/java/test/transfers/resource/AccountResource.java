package test.transfers.resource;

import com.google.inject.Inject;
import test.transfers.model.Account;
import test.transfers.service.AccountService;
import javax.inject.Singleton;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.math.BigDecimal;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/account")
@Singleton
@Produces(APPLICATION_JSON)
public class AccountResource {
  @Inject
  AccountService accountService;

  @GET
  @Path("/{id}")
  public Account get(@PathParam("id") Long id) {
    return accountService.getAccount(id);
  }

  @POST
  public Account create(@DefaultValue("0") @FormParam("balance") BigDecimal balance) {
    return accountService.createAccount(balance);
  }
}
