package org.multibit.mbm.api.response.hal.user;

import com.google.common.base.Optional;
import com.theoryinpractise.halbuilder.spi.Resource;
import org.multibit.mbm.api.response.hal.BaseBridge;
import org.multibit.mbm.db.dto.ContactMethod;
import org.multibit.mbm.db.dto.ContactMethodDetail;
import org.multibit.mbm.db.dto.User;
import org.multibit.mbm.resources.ResourceAsserts;

import javax.ws.rs.core.UriInfo;
import java.util.Map;
import java.util.Set;

/**
 * <p>Bridge to provide the following to {@link org.multibit.mbm.db.dto.User}:</p>
 * <ul>
 * <li>Creates representations of a User for a Supplier</li>
 * </ul>
 *
 * @since 0.0.1
 */
public class SupplierUserBridge extends BaseBridge<User> {

  private final ClientUserBridge clientUserBridge;

  /**
   * @param uriInfo   The {@link javax.ws.rs.core.UriInfo} containing the originating request information
   * @param principal An optional {@link org.multibit.mbm.db.dto.User} to provide a security principal
   */
  public SupplierUserBridge(UriInfo uriInfo, Optional<User> principal) {
    super(uriInfo, principal);
    clientUserBridge = new ClientUserBridge(uriInfo,principal);
  }

  public Resource toResource(User user) {

    // Build on the minimal representation
    Resource userResource = clientUserBridge.toResource(user);

    // Apply restrictions against the more detailed representation
    ResourceAsserts.assertNotNull(user, "user");
    ResourceAsserts.assertNotNull(user.getId(),"id");

    // Add properties
    userResource.withProperty("username", user.getUsername());

    // Convert the ContactMethodDetails map into primary and secondary property entries
    for (Map.Entry<ContactMethod, ContactMethodDetail> entry : user.getContactMethodMap().entrySet()) {
      String propertyName = entry.getKey().getPropertyNameSingular();
      ContactMethodDetail contactMethodDetail = entry.getValue();
      String primaryDetail = contactMethodDetail.getPrimaryDetail();

      userResource.withProperty(propertyName, primaryDetail);

      // Determine if secondary details should be included
      if (entry.getKey().isSecondaryDetailSupported()) {
        Set<String> secondaryDetails = contactMethodDetail.getSecondaryDetails();
        // TODO Consider if a 1-based field index is the best representation here: array? sub-resource?
        int index = 1;
        for (String secondaryDetail : secondaryDetails) {
          userResource.withProperty(propertyName + index, secondaryDetail);
          index++;
        }
      }
    }

    return userResource;

  }

}
