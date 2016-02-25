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

import com.google.common.collect.Sets;
import com.twitter.common_internal.elfowl.Cookie;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.apache.zeppelin.utils.SecurityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

/**
 * This filter checks if the elfowl cookie groups field contains coremetrics-team
 *
 */
public class AdminAuthenticationFilter implements Filter {

  private static final String ADMIN_GROUP = "coremetrics-team";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
          throws IOException, ServletException {
    HttpServletRequest servReq = (HttpServletRequest) request;
    HashSet<String> groups = SecurityUtils.getGroups(servReq);
    if (groups.contains(ADMIN_GROUP)) {
      filterChain.doFilter(request, response);
    } else {
      HttpServletResponse resp = ((HttpServletResponse) response);
      resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only members of coremetrics-team are allowed to perform this operation");
    };
  }

  @Override
  public void destroy() {}

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}
}
