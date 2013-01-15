/**
 * 
 */
package com.github.ansell.restletutils;

import java.util.concurrent.ConcurrentMap;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.engine.header.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.routing.Filter;
import org.restlet.util.Series;

/**
 * Filter for Restlet-2.1 that adds an "Access-Control-Allow-Origin: *" header to every request.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 *         Source:
 *         http://restlet-discuss.1400322.n2.nabble.com/How-to-set-response-header-td7270489.html
 */
public class CrossOriginResourceSharingFilter extends Filter
{
    
    /*
     * (non-Javadoc)
     * 
     * @see org.restlet.routing.Filter#afterHandle(org.restlet.Request, org.restlet.Response)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void afterHandle(final Request request, final Response response)
    {
        super.afterHandle(request, response);
        
        final ConcurrentMap<String, Object> responseAttributes = response.getAttributes();
        
        Series<Header> responseHeaders = (Series<Header>)responseAttributes.get(HeaderConstants.ATTRIBUTE_HEADERS);
        
        if(responseHeaders == null)
        {
            responseHeaders = new Series<Header>(Header.class);
            Series<Header> putIfAbsent =
                    (Series<Header>)responseAttributes.putIfAbsent(HeaderConstants.ATTRIBUTE_HEADERS, responseHeaders);
            
            if(putIfAbsent != null)
            {
                responseHeaders = putIfAbsent;
            }
        }
        
        // Allow all origins
        responseHeaders.add("Access-Control-Allow-Origin", "*");
    }
    
}
