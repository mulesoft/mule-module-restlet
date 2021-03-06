/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.restlet;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.transformer.TransformerException;
import org.mule.transport.AbstractMessageDispatcher;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.Request;
import org.restlet.Response;

/**
 * <code>RestletMessageDispatcher</code> TODO document
 * 
 * @author <a href="mailto:keithnaas@biglots.com">keithnaas@biglots.com</a>
 */
public class RestletMessageDispatcher extends AbstractMessageDispatcher
{
    private final Client client;

    public RestletMessageDispatcher(final OutboundEndpoint endpoint)
    {
        super(endpoint);

        final RestletConnector cnn = (RestletConnector) endpoint.getConnector();
        final Protocol protocol = Protocol.HTTP;

        client = new Client(protocol);
    }

    @Override
    public void doConnect() throws Exception
    {
        client.start();
    }

    @Override
    public void doDisconnect() throws Exception
    {
        client.stop();
    }

    @Override
    public void doDispatch(final MuleEvent event) throws Exception
    {
        client.handle(getRequest(event));
    }

    @Override
    public MuleMessage doSend(final MuleEvent event) throws Exception
    {
        final Response response = client.handle(getRequest(event));

        // TODO: redirects?

        return createResponseMessage(response,event.getMuleContext());
    }

    

    protected MuleMessage createResponseMessage(final Response response,MuleContext muleContext)
    {
        return new DefaultMuleMessage(response,muleContext);
    }

    protected Request getRequest(final MuleEvent event) throws TransformerException
    {
        return (Request) event.transformMessage(Request.class);
    }

    @Override
    public void doDispose()
    {}

}
