package org.multibit.mbm.resources.user;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.yammer.metrics.annotation.Timed;
import org.multibit.mbm.api.hal.HalMediaType;
import org.multibit.mbm.api.response.hal.user.ClientUserBridge;
import org.multibit.mbm.api.response.hal.user.CustomerUserBridge;
import org.multibit.mbm.auth.Authority;
import org.multibit.mbm.auth.annotation.RestrictedTo;
import org.multibit.mbm.core.model.*;
import org.multibit.mbm.db.dao.UserDao;
import org.multibit.mbm.resources.BaseResource;
import org.multibit.mbm.utils.DateUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * <p>Resource to provide the following to application:</p>
 * <ul>
 * <li>Provision of REST endpoints to manage operations by a Customer</li>
 * </ul>
 *
 * @since 0.0.1
 */
@Component
@Path("/customer/user")
@Produces({HalMediaType.APPLICATION_HAL_JSON, HalMediaType.APPLICATION_HAL_XML})
public class CustomerUserResource extends BaseResource {

  @Resource(name = "hibernateUserDao")
  private UserDao userDao;

  /**
   * @param customerUser The authenticated Customer
   *
   * @return A HAL representation of the result
   */
  @GET
  @Timed
  public Response retrieveOwnUser(
    @RestrictedTo({Authority.ROLE_CUSTOMER})
    User customerUser) {

    CustomerUserBridge bridge = new CustomerUserBridge(uriInfo, Optional.of(customerUser));

    return ok(bridge, customerUser);

  }

  /**
   * @param customerUser The Customer User
   *
   * @return A HAL representation of the result
   */
  @DELETE
  @Timed
  public Response deregisterUser(
    @RestrictedTo({Authority.ROLE_CUSTOMER})
    User customerUser) {

    Preconditions.checkNotNull(customerUser);

    // Remove all identifying information from the User
    // but leave the entity available for audit purposes
    // We leave the secret key in case the user has been
    // accidentally deleted and the user wants to be
    // reinstated
    customerUser.setApiKey("");
    customerUser.setContactMethodMap(Maps.<ContactMethod, ContactMethodDetail>newHashMap());
    customerUser.setUsername("");
    customerUser.setPasswordDigest("");
    customerUser.setPasswordResetAt(DateUtils.nowUtc());
    customerUser.setLocked(true);
    customerUser.setDeleted(true);
    customerUser.setReasonForDelete("Customer deregistered");
    customerUser.setUserFieldMap(Maps.<UserField, UserFieldDetail>newHashMap());

    // Persist the User with cascade for the Customer
    User persistentUser = userDao.saveOrUpdate(customerUser);

    // Provide a minimal representation to the client
    // so that they can see their secret key as a last resort
    // manual recovery option
    ClientUserBridge bridge = new ClientUserBridge(uriInfo, Optional.of(customerUser));
    URI location = uriInfo.getAbsolutePathBuilder().path(persistentUser.getApiKey()).build();

    return created(bridge, persistentUser, location);

  }

  public void setUserDao(UserDao userDao) {
    this.userDao = userDao;
  }
}