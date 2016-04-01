package org.apache.zeppelin.server;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handler to convert http requests to https
 */
public class SecureSchemeHandler extends AbstractHandler
{
  private static final Logger LOG = LoggerFactory.getLogger(SecureSchemeHandler.class);

  private int securePort = 443;

  public SecureSchemeHandler(int securePort) {
    this.securePort = securePort;
  }

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request,
                     HttpServletResponse response) throws IOException, ServletException
  {

    if (baseRequest.isSecure())
    {
      return; // all done
    }

    if ( securePort > 0)
    {
      LOG.info("Before {}", baseRequest.getRequestURL());
      baseRequest.setScheme("https");
      baseRequest.setServerPort(securePort);
      LOG.info("After {}", baseRequest.getRequestURL());
      response.setContentLength(0);
      response.sendRedirect(baseRequest.getRequestURL().toString());
    }
    else
    {
      response.sendError(HttpStatus.FORBIDDEN_403, "!Secure");
    }

    baseRequest.setHandled(true);
  }
}
