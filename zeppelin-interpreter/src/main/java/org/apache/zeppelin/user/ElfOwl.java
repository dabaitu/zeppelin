package org.apache.zeppelin.user;

import java.util.Set;

/**
 * Created by hfreire on 6/8/17.
 */
public class ElfOwl {
  private static final String ADMIN_GROUP = "realtimecompute-team";

  public static boolean isSuperUser(Set<String> roles) {
    return roles.contains(ADMIN_GROUP) ||
        roles.contains("coremetrics-team"); // TODO(IQ-447) Remove;
  }

}
