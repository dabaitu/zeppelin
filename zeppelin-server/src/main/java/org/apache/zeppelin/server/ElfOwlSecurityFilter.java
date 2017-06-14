/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zeppelin.server;

import java.io.IOException;
import java.util.HashSet;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Sets;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.subject.support.DefaultWebSubjectContext;

import com.twitter.common_internal.elfowl.Cookie;

/**
 * Bridges elfowl cookes to the security realm of Shiro
 */
public class ElfOwlSecurityFilter implements Filter {

  private static ElfOwlSecurityManager securityManager = new ElfOwlSecurityManager();

  public static ElfOwlSecurityManager getSecurityManager() {
    return securityManager;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    HttpServletRequest servReq = (HttpServletRequest) request;

    DefaultWebSubjectContext ctx = new DefaultWebSubjectContext();
    ctx.setServletRequest(request);
    ctx.setServletResponse(response);

    Subject subject = getSecurityManager().createSubject(
        getUser(servReq),
        getGroups(servReq),
        ctx
    );

    // update the user roles based on what the cookie gives us
    org.apache.shiro.util.ThreadContext.bind(securityManager);
    org.apache.shiro.util.ThreadContext.bind(subject);

    filterChain.doFilter(request, response);
  }

  @Override
  public void destroy() {}

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  private static Cookie extractCookie(HttpServletRequest request) {
    javax.servlet.http.Cookie[] cookies = request.getCookies();
    String elfOwlCookieValue = null;
    for (javax.servlet.http.Cookie cookie: cookies) {
      if (cookie.getName().equals("_elfowl")) {
        elfOwlCookieValue = cookie.getValue();
      }
    }
    Cookie.Session session = new Cookie.Session(
        Cookie.Environment.PRODUCTION, request.getHeader("user-agent"));
    Cookie cookie = Cookie.fromBase64(session, elfOwlCookieValue);
    return cookie;
  }

  private static String getUser(HttpServletRequest request) {
    Cookie cookie = extractCookie(request);
    return cookie.getUser();
  }

  private static HashSet<String> getGroups(HttpServletRequest request) {
    Cookie cookie = extractCookie(request);
    return Sets.newHashSet(cookie.getGroups().iterator());
  }
}
