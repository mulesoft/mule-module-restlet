package org.mule.transport.restlet.transformer;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.DiscoverableTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;
import org.mule.transport.http.HttpConstants;
import org.mule.transport.restlet.RestletConnector;
import org.restlet.data.Parameter;
import org.restlet.data.Response;
import org.restlet.util.Series;

public abstract class AbstractResponseTransformer 
    extends AbstractMessageAwareTransformer
    implements DiscoverableTransformer
{

    private int priorityWeighting;
    
    public AbstractResponseTransformer() 
    {
        super();
        
        registerSourceType(Response.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object transform(MuleMessage msg, String encoding) throws TransformerException {
        Response response = (Response) msg.getPayload();
        final Map<String, Object> attributesMap = response.getAttributes();
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
                        msg.setProperty(key, ((Object[]) value)[0]);
                    }
                    else
                    {
                        msg.setProperty(key, value);
                    }
                }
            }
            String realKey;
            Series<Parameter> params = (Series<Parameter>) attributesMap.get(com.noelios.restlet.http.HttpConstants.ATTRIBUTE_HEADERS);
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
                    String value = parameter.getValue();
                    if (value != null)
                    {
                        msg.setProperty(realKey, value);
                    }
                }
            }
        }
        
        return getPayload(response, encoding);
    }

    protected abstract Object getPayload(Response response, String encoding);

    public int getPriorityWeighting() 
    {
        return priorityWeighting;
    }

    public void setPriorityWeighting(int priorityWeighting) 
    {
        this.priorityWeighting = priorityWeighting;
    }


}
