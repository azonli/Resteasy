package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.servlet.http.HttpEvent;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JBossWebAsyncHttpRequest extends HttpServletInputMessage
{
   protected HttpEvent event;

   public JBossWebAsyncHttpRequest(HttpServletRequest httpServletRequest, HttpResponse httpResponse, HttpHeaders httpHeaders, InputStream inputStream, UriInfo uriInfo, String httpMethodName, SynchronousDispatcher synchronousDispatcher, HttpEvent event)
   {
      super(httpServletRequest, httpResponse, httpHeaders, inputStream, uriInfo, httpMethodName, synchronousDispatcher);
      this.event = event;
   }

   @Override
   public void initialRequestThreadFinished()
   {
   }

   @Override
   public AsynchronousResponse createAsynchronousResponse(long l)
   {
      suspended = true;
      return new AsynchronousResponse()
      {
         public void setResponse(Response response)
         {
            try
            {
               dispatcher.asynchronousDelivery(JBossWebAsyncHttpRequest.this, httpResponse, response);
            }
            finally
            {
               try
               {
                  event.close();
               }
               catch (IOException ignored)
               {
               }
            }
         }
      };
   }


}