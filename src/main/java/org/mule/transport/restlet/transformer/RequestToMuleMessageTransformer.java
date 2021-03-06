package org.mule.transport.restlet.transformer;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractDiscoverableTransformer;
import org.mule.transformer.types.SimpleDataType;
import org.mule.transport.http.HttpConstants;
import org.mule.transport.restlet.RestletConnector;
import org.mule.transport.restlet.RestletHttpConstants;
import org.restlet.Request;
import org.restlet.data.Parameter;
import org.restlet.util.Series;

public class RequestToMuleMessageTransformer extends AbstractDiscoverableTransformer {

    public RequestToMuleMessageTransformer() {
        super();
        registerSourceType(new SimpleDataType<Object>(Request.class));
        setReturnDataType(new SimpleDataType<Object>(MuleMessage.class));
    }
    

    protected Object doTransform(Object src, String encoding) throws TransformerException {
        Request request = (Request) src;
        
        try {
            DefaultMuleMessage msg = new DefaultMuleMessage(request.getEntity().getStream(),muleContext);
            
            final Map<String, Object> attributesMap = request.getAttributes();
            if (attributesMap != null && attributesMap.size() > 0)
            {
                for (final Map.Entry<String, Object> entry : attributesMap.entrySet())
                {
                    final String key = entry.getKey();
                    final Object value = entry.getValue();
                    if (value != null && !ArrayUtils.contains(RestletConnector.RESTLET_IGNORE_KEYS, key))
                    {
                        if (value.getClass().isArray() && ((Object[]) value).length == 1)
                        {
                            msg.setInboundProperty(key, ((Object[]) value)[0]);
                        }
                        else
                        {
                            msg.setInboundProperty(key, value);
                        }
                    }
                }
                
                String realKey;
                Series<Parameter> params = (Series<Parameter>) attributesMap.get(RestletHttpConstants.ATTRIBUTE_HEADERS);
                if (params != null) 
                {
                    for (final Parameter parameter : params)
                    {
                        final String key = parameter.getName();
                        realKey = key;
                        if (key.startsWith(HttpConstants.X_PROPERTY_PREFIX))
                        {
                            realKey = key.substring(2);
                        }
                        msg.setInboundProperty(realKey, parameter.getValue());
                    }
                }
            }
            return msg;
        } catch (IOException e) {
            throw new TransformerException(this, e);
        }
    }

}
