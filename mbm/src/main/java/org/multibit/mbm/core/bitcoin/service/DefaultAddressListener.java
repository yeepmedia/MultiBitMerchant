package org.multibit.mbm.core.bitcoin.service;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAddressListener implements AddressListener {

  private static final Logger log = LoggerFactory.getLogger(DefaultAddressListener.class);

  private Address address;
  private Long id;

  public DefaultAddressListener(Address address, Long id) {
    this.address = address;
    this.id = id;
  }

  @Override
  public void onCoinsReceived(Address address, Transaction transaction) {
// TODO Re-implement this
//    if (this.address.equals(address)) {
//      AlertMessage alertMessage = new AlertMessage();
//      alertMessage.setText("Payment received for " + address.toString());
//      try {
//        BroadcastService.INSTANCE.broadcast(id, alertMessage);
//      } catch (IOException e) {
//        log.error(e.getClass().getName() + " " + e.getMessage());
//      }
//    } else {
//      // wrongly wired up
//      log.warn("Address listener for address " + address.toString() + " is wrongly wired up");
//    }
  }

  @Override
  public void onPendingCoinsReceived(Address address, Transaction tx) {
// TODO Re-implement this
//    if (this.address.equals(address)) {
//      AlertMessage alertMessage = new AlertMessage();
//      alertMessage.setText("Payment received for " + address.toString());
//      try {
//        BroadcastService.INSTANCE.broadcast(id, alertMessage);
//      } catch (IOException e) {
//        log.error(e.getClass().getName() + " " + e.getMessage());
//      }
//    } else {
//      // wrongly wired up
//      log.warn("Address listener for address " + address.toString() + " is wrongly wired up");
//    }
  }

  public long getId() {
    return id;
  }
}
