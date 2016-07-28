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
package org.apache.zeppelin.socket;

import org.apache.zeppelin.utils.SecurityUtils;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;

/**
 * Notebook websocket
 */
public class NotebookSocket extends WebSocketAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(NotebookServer.class);

  private Session connection;
  private NotebookSocketListener listener;
  private HttpServletRequest request;
  private String protocol;
  private String user;
  private HashSet<String> groups;
  private HashSet<String> userAndGroups;
  public NotebookSocket(HttpServletRequest req, String protocol,
      NotebookSocketListener listener) {
    this.listener = listener;
    this.request = req;
    this.protocol = protocol;
    this.user = SecurityUtils.getUser(req);
    this.groups = SecurityUtils.getGroups(req);
    this.userAndGroups = new HashSet<String>();
    userAndGroups.add(this.user);
    userAndGroups.addAll(this.groups);
  }

  @Override
  public void onWebSocketClose(int closeCode, String message) {
    listener.onClose(this, closeCode, message);
  }

  @Override
  public void onWebSocketConnect(Session connection) {
    this.connection = connection;
    listener.onOpen(this);
  }

  @Override
  public void onWebSocketText(String message) {
    listener.onMessage(this, message);
  }


  public HttpServletRequest getRequest() {
    return request;
  }

  public String getProtocol() {
    return protocol;
  }

  public String getUser() { return user; }

  public HashSet<String> getGroups() { return groups; }

  public HashSet<String> getUserAndGroups() { return userAndGroups; }

  public void send(String serializeMessage) throws IOException {
    connection.getRemote().sendString(serializeMessage, null);
  }

}
