package org.mule.transport.restlet.hello;

import java.util.HashMap;
import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.transport.http.HttpConnector;

public class HelloTest extends FunctionalTestCase {
    
    public void testApplication() throws Exception {
        MuleClient client = new MuleClient(muleContext);
        
        Map<String,Object> props = new HashMap<String, Object>();
        props.put(HttpConnector.HTTP_METHOD_PROPERTY, "GET");
        props.put(HelloWorldResource.X_CUSTOM_HEADER, "foo");
        
//        HashMap<String, String> customHeaders = new HashMap<String,String>();
//        customHeaders.put("Authorization", "Basic " + Base64.encode("admin:admin".getBytes(), true));
//        props.put(HttpConnector.HTTP_CUSTOM_HEADERS_MAP_PROPERTY, customHeaders);
       
        MuleMessage result = client.send("http://localhost:63081/bar/foo", "test", props);
        assertEquals("Wrong message:"+result.getPayloadAsString(),"hello, world", result.getPayloadAsString());
//        assertEquals("foo", result.getStringProperty(HelloWorldResource.X_CUSTOM_HEADER, ""));
        
        result = client.send("http://localhost:63081/bar/foo?name=Dan", "test", props);
        assertEquals("hello Dan", result.getPayloadAsString());
        
        result = client.send("http://localhost:63081/bar/foo?name=Mr. XML", "test", props);
        assertEquals("<hello>Mr. XML</hello>", result.getPayloadAsString());
        assertEquals("application/xml", result.getInboundProperty("Content-Type"));
        
        // try custom status codes
        props.put(HelloWorldResource.X_STATUS_HEADER, "201");
        
        result = client.send("http://localhost:63081/bar/foo", "test", props);
        assertEquals("hello, world", result.getPayloadAsString());
//        assertEquals("201", result.getInboundProperty(HttpConnector.HTTP_STATUS_PROPERTY));
        

        props.put(HttpConnector.HTTP_METHOD_PROPERTY, "HEAD");
        props.put(HelloWorldResource.X_STATUS_HEADER, "204");
        result = client.send("http://localhost:63081/bar/foo", "test", props);
//        assertEquals("foo", result.getStringProperty(HelloWorldResource.X_CUSTOM_HEADER, ""));
//        assertEquals("204", result.getInboundProperty(HttpConnector.HTTP_STATUS_PROPERTY));
    }

    @Override
    protected String getConfigResources() {
        return "hello-conf.xml";
    }
    @Override
    protected void initTestTimeoutSecs() {
    	this.testTimeoutSecs = Integer.MAX_VALUE;
    }
    
    
}
