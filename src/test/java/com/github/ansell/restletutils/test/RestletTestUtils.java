package com.github.ansell.restletutils.test;

import java.util.List;

import org.junit.Assert;
import org.restlet.Response;
import org.restlet.data.ChallengeRequest;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class RestletTestUtils
{
    
    // NOTE: These constants are defined to match users added in ApplicationUtils.setupApplication
    public static final char[] TEST_PASSWORD = "testPassword".toCharArray();
    
    public static final String TEST_USERNAME = "testUser";
    
    public static final char[] TEST_ADMIN_PASSWORD = "testAdminPassword".toCharArray();
    
    public static final String TEST_ADMIN_USERNAME = "testAdminUser";
    
    public static Representation doTestAuthenticatedRequest(final ClientResource clientResource,
            final Method requestMethod, final Object inputRepresentation, final MediaType requestMediaType,
            final Status expectedResponseStatus, final boolean requiresAdminPrivileges)
    {
        Representation result = null;
        
        try
        {
            if(requestMethod.equals(Method.DELETE))
            {
                result = clientResource.delete(requestMediaType);
            }
            else if(requestMethod.equals(Method.PUT))
            {
                result = clientResource.put(inputRepresentation, requestMediaType);
            }
            else if(requestMethod.equals(Method.GET))
            {
                result = clientResource.get(requestMediaType);
            }
            else if(requestMethod.equals(Method.POST))
            {
                result = clientResource.post(inputRepresentation, requestMediaType);
            }
            else
            {
                throw new RuntimeException("Did not recognise request method: " + requestMethod.toString());
            }
            Assert.fail("Expected to receive an authentication challenge");
        }
        catch(final ResourceException re)
        {
            Assert.assertNotNull(re.getStatus());
            
            Assert.assertEquals(Status.CLIENT_ERROR_UNAUTHORIZED.getCode(), re.getStatus().getCode());
        }
        
        // add the challenge response to complete the authentication on the client side
        if(requiresAdminPrivileges)
        {
            clientResource.setChallengeResponse(RestletTestUtils.getTestChallengeResponseAdmin(clientResource));
        }
        else
        {
            clientResource.setChallengeResponse(RestletTestUtils.getTestChallengeResponse(clientResource));
        }
        
        if(requestMethod.equals(Method.DELETE))
        {
            result = clientResource.delete(requestMediaType);
        }
        else if(requestMethod.equals(Method.PUT))
        {
            result = clientResource.put(inputRepresentation, requestMediaType);
        }
        else if(requestMethod.equals(Method.GET))
        {
            result = clientResource.get(requestMediaType);
        }
        else if(requestMethod.equals(Method.POST))
        {
            result = clientResource.post(inputRepresentation, requestMediaType);
        }
        else
        {
            throw new RuntimeException("Did not recognise request method: " + requestMethod.toString());
        }
        
        Assert.assertEquals(expectedResponseStatus.getCode(), clientResource.getResponse().getStatus().getCode());
        
        return result;
    }
    
    public static ChallengeResponse getTestChallengeResponse(final ClientResource clientResource)
    {
        return RestletTestUtils.getTestChallengeResponse(clientResource.getChallengeRequests(),
                ChallengeScheme.HTTP_DIGEST, clientResource.getResponse(), RestletTestUtils.TEST_USERNAME,
                RestletTestUtils.TEST_PASSWORD);
    }
    
    public static ChallengeResponse getTestChallengeResponse(final List<ChallengeRequest> list,
            final ChallengeScheme httpDigest, final Response response, final String userName, final char[] password)
    {
        ChallengeRequest c1 = null;
        for(final ChallengeRequest challengeRequest : list)
        {
            if(ChallengeScheme.HTTP_DIGEST.equals(challengeRequest.getScheme()))
            {
                c1 = challengeRequest;
                break;
            }
        }
        
        // 2- Create the Challenge response used by the client to authenticate its requests.
        final ChallengeResponse challengeResponse = new ChallengeResponse(c1, response, userName, password);
        
        return challengeResponse;
    }
    
    public static ChallengeResponse getTestChallengeResponseAdmin(final ClientResource clientResource)
    {
        return RestletTestUtils.getTestChallengeResponse(clientResource.getChallengeRequests(),
                ChallengeScheme.HTTP_DIGEST, clientResource.getResponse(), RestletTestUtils.TEST_ADMIN_USERNAME,
                RestletTestUtils.TEST_ADMIN_PASSWORD);
    }
    
    public RestletTestUtils()
    {
        super();
    }
    
}