package org.apache.zeppelin.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.subject.support.DelegatingSubject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by hfreire on 6/7/17.
 */
public class ElfOwlSecurityManager extends DefaultWebSecurityManager implements SecurityManager {
  private final SimpleAccountRealm realm;
  private HashMap<String, HashSet<String>> usersAndRoles = new HashMap<>();
  private HashMap<String, String> usersAndTokens = new HashMap<>();
  private HashSet<String> emptySet = new HashSet<>();
  private Collection<Realm> realms;

  public ElfOwlSecurityManager() {
    // this only exists to bridge the shiro getRoles to this impl
    realms = new ArrayList();
    realm = new SimpleAccountRealm();
    realms.add(realm);
  }

  @Override
  public Subject login(Subject subject, AuthenticationToken authenticationToken)
      throws AuthenticationException {
    return createSubject(
        subject.getPrincipal().toString(),
        new HashSet<>(),
        null
    );
  }

  @Override
  public void logout(Subject subject) {
    usersAndRoles.remove(subject.getPrincipal());
  }

  @Override
  public Subject createSubject(SubjectContext subjectContext) {
    return new DelegatingSubject(
        subjectContext.resolvePrincipals(),
        subjectContext.isAuthenticated(),
        subjectContext.getHost(),
        subjectContext.getSession(),
        this);
  }

  @Override
  public boolean hasRole(PrincipalCollection principalCollection, String s) {
    return usersAndRoles.getOrDefault(
        principalCollection.getPrimaryPrincipal().toString(),
        emptySet
    ).contains(s);
  }

  @Override
  public boolean[] hasRoles(PrincipalCollection principalCollection, List<String> list) {
    boolean[] res = new boolean[list.size()];
    for (int i = 0; i < list.size(); i++) {
      res[i] = hasRole(principalCollection, list.get(i));
    }
    return res;
  }

  @Override
  public boolean hasAllRoles(PrincipalCollection principalCollection,
                             Collection<String> collection) {
    return usersAndRoles.getOrDefault(
        principalCollection.getPrimaryPrincipal().toString(),
        emptySet
    ).containsAll(collection);
  }

  @Override
  public void checkRole(PrincipalCollection principalCollection, String s)
      throws AuthorizationException {
    if (!hasRole(principalCollection, s)) {
      throw new AuthorizationException();
    }
  }

  @Override
  public void checkRoles(PrincipalCollection principalCollection,
                         Collection<String> collection) throws AuthorizationException {
    if (!hasAllRoles(principalCollection, collection)){
      throw new AuthorizationException();
    }
  }

  @Override
  public void checkRoles(PrincipalCollection principalCollection,
                         String... strings) throws AuthorizationException {
    checkRoles(principalCollection, Arrays.asList(strings));
  }

  @Override
  public Collection<Realm> getRealms() {
    return realms;
  }

  public void setRoles(String user, HashSet<String> roles) {
    usersAndRoles.put(user, roles);
  }

  public Subject createSubject(String user, HashSet<String> groups, SubjectContext subjectContext) {
    setRoles(user, groups);
    return new DelegatingSubject(
        new SimplePrincipalCollection(user, "elfowl"), // integrate w/ LdapRealm here?
        true,
        subjectContext.getHost(),
        subjectContext.getSession(),
        this);
  }

  public HashSet<String> getRoles(Subject subject) {
    return usersAndRoles.getOrDefault(
        subject.getPrincipal().toString(),
        emptySet
    );
  }


  @Override
  public Session start(SessionContext sessionContext) {
    throw new NotImplementedException();
  }

  @Override
  public Session getSession(SessionKey sessionKey) throws SessionException {
    throw new NotImplementedException();
  }

  @Override
  public AuthenticationInfo authenticate(AuthenticationToken authenticationToken)
      throws AuthenticationException {
    throw new NotImplementedException();
  }

  @Override
  public boolean isPermitted(PrincipalCollection principalCollection, String s) {
    throw new NotImplementedException();
  }

  @Override
  public boolean isPermitted(PrincipalCollection principalCollection, Permission permission) {
    throw new NotImplementedException();
  }

  @Override
  public boolean[] isPermitted(PrincipalCollection principalCollection, String... strings) {
    throw new NotImplementedException();
  }

  @Override
  public boolean[] isPermitted(PrincipalCollection principalCollection, List<Permission> list) {
    throw new NotImplementedException();
  }

  @Override
  public boolean isPermittedAll(PrincipalCollection principalCollection, String... strings) {
    throw new NotImplementedException();
  }

  @Override
  public boolean isPermittedAll(PrincipalCollection principalCollection,
                                Collection<Permission> collection) {
    throw new NotImplementedException();
  }

  @Override
  public void checkPermission(PrincipalCollection principalCollection,
                              String s) throws AuthorizationException {
    throw new NotImplementedException();

  }

  @Override
  public void checkPermission(PrincipalCollection principalCollection,
                              Permission permission) throws AuthorizationException {
    throw new NotImplementedException();

  }

  @Override
  public void checkPermissions(PrincipalCollection principalCollection,
                               String... strings) throws AuthorizationException {
    throw new NotImplementedException();

  }

  @Override
  public void checkPermissions(PrincipalCollection principalCollection,
                               Collection<Permission> collection) throws AuthorizationException {
    throw new NotImplementedException();

  }
}
