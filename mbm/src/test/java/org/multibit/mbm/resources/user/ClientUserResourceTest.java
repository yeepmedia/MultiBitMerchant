package org.multibit.mbm.resources.user;

import com.google.common.base.Optional;
import org.junit.Test;
import org.multibit.mbm.api.hal.HalMediaType;
import org.multibit.mbm.api.request.user.WebFormAuthenticationRequest;
import org.multibit.mbm.auth.Authority;
import org.multibit.mbm.db.DatabaseLoader;
import org.multibit.mbm.db.dao.RoleDao;
import org.multibit.mbm.db.dao.UserDao;
import org.multibit.mbm.core.model.Role;
import org.multibit.mbm.core.model.User;
import org.multibit.mbm.test.BaseJerseyHmacResourceTest;
import org.multibit.mbm.test.FixtureAsserts;

import javax.ws.rs.core.MediaType;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Verifies the user resource can be accessed by an authenticated Customer
 */
public class ClientUserResourceTest extends BaseJerseyHmacResourceTest {

  private final UserDao userDao=mock(UserDao.class);
  private final RoleDao roleDao=mock(RoleDao.class);

  private final ClientUserResource testObject=new ClientUserResource();

  @Override
  protected void setUpResources() {

    // Create the User for authenticated access
    User clientUser = setUpClientHmacAuthenticator();
    clientUser.setId(1L);

    // Create the supporting Role
    Role customerRole = DatabaseLoader.buildCustomerRole();
    Role publicRole = DatabaseLoader.buildPublicRole();
    User aliceUser = DatabaseLoader.buildAliceCustomer(customerRole);
    User bobUser = DatabaseLoader.buildBobCustomer(customerRole);

    // Configure mocks
    when(userDao.getByCredentials(anyString(), anyString())).thenReturn(Optional.of(aliceUser));
    when(userDao.saveOrUpdate(any(User.class))).thenReturn(bobUser);
    when(roleDao.getByName(Authority.ROLE_PUBLIC.name())).thenReturn(Optional.of(publicRole));

    // Bind mocks
    testObject.setUserDao(userDao);
    testObject.setRoleDao(roleDao);

    // Configure resources
    addSingleton(testObject);

  }

  @Test
  public void clientRegisterAnonymousUserAsHalJson() throws Exception {

    // Arrange

    // Act
    String actualResponse = configureAsClient("/client/user/anonymous")
      .accept(HalMediaType.APPLICATION_HAL_JSON)
      .post(String.class);

    // Assert
    FixtureAsserts.assertStringMatchesJsonFixture("Client register their anonymous User as HAL+JSON", actualResponse, "/fixtures/hal/user/expected-client-register-anonymous-user.json");

  }

  @Test
  public void clientAuthenticateUserAsHalJson() throws Exception {

    // Arrange
    WebFormAuthenticationRequest authenticateUserRequest = new WebFormAuthenticationRequest(
      "alice",
      "alice1"
    );

    // Act
    String actualResponse = configureAsClient("/client/user/authenticate")
      .accept(HalMediaType.APPLICATION_HAL_JSON)
      .entity(authenticateUserRequest, MediaType.APPLICATION_JSON_TYPE)
      .post(String.class);

    // Assert
    FixtureAsserts.assertStringMatchesJsonFixture("Client authenticate their User as HAL+JSON", actualResponse, "/fixtures/hal/user/expected-client-authenticate-user.json");

  }

}

