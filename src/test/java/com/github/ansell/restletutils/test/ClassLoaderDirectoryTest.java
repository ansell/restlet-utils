/**
 * 
 */
package com.github.ansell.restletutils.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ServerResource;

import com.github.ansell.restletutils.ClassLoaderDirectory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class ClassLoaderDirectoryTest
{
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.ClassLoaderDirectory#ClassLoaderDirectory(org.restlet.Context, org.restlet.data.Reference, java.lang.ClassLoader)}
     * .
     * 
     * @throws Exception
     */
    @Ignore
    @Test
    public void testClassLoaderDirectory() throws Exception
    {
        final OasRestletExampleTestApplication testApp = new OasRestletExampleTestApplication();
        testApp.start();
        
        final ClassLoaderDirectory dir = testApp.getDirectory();
        
        final Request request = new Request(Method.GET, "/top/resourcetestfolder/scripting/test.js");
        
        final Response response = new Response(request);
        
        final ServerResource find = dir.find(request, response);
        
        Assert.assertNotNull(find);
        
        find.setRequest(request);
        find.setResponse(response);
        
        find.setConditional(false);
        
        Assert.assertNotNull(find.handle());
        
        Assert.assertEquals(Status.SUCCESS_OK, find.getStatus());
    }
    
    /**
     * Test method for
     * {@link org.restlet.resource.Finder#create(java.lang.Class, org.restlet.Request, org.restlet.Response)}
     * .
     */
    @Ignore
    @Test
    public void testCreateClassOfQextendsServerResourceRequestResponse()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.restlet.resource.Finder#create(org.restlet.Request, org.restlet.Response)}.
     */
    @Ignore
    @Test
    public void testCreateRequestResponse()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.restlet.resource.Finder#find(org.restlet.Request, org.restlet.Response)}.
     */
    @Ignore
    @Test
    public void testFind()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.restlet.resource.Directory#getComparator()}.
     */
    @Ignore
    @Test
    public void testGetComparator()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.restlet.Restlet#getContext()}.
     */
    @Ignore
    @Test
    public void testGetContext()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.restlet.resource.Directory#getIndexName()}.
     */
    @Ignore
    @Test
    public void testGetIndexName()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.restlet.resource.Directory#getIndexRepresentation(org.restlet.representation.Variant, org.restlet.data.ReferenceList)}
     * .
     */
    @Ignore
    @Test
    public void testGetIndexRepresentation()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.restlet.resource.Directory#getIndexVariants(org.restlet.data.ReferenceList)}.
     */
    @Ignore
    @Test
    public void testGetIndexVariants()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.restlet.resource.Directory#getRootRef()}.
     */
    @Ignore
    @Test
    public void testGetRootRef()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.ClassLoaderDirectory#handle(org.restlet.Request, org.restlet.Response)}
     * .
     */
    @Ignore
    @Test
    public void testHandleRequestResponse()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.restlet.resource.Directory#isDeeplyAccessible()}.
     */
    @Ignore
    @Test
    public void testIsDeeplyAccessible()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.restlet.resource.Directory#isListingAllowed()}.
     */
    @Ignore
    @Test
    public void testIsListingAllowed()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.restlet.resource.Directory#isModifiable()}.
     */
    @Ignore
    @Test
    public void testIsModifiable()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.restlet.resource.Directory#isNegotiatingContent()}.
     */
    @Ignore
    @Test
    public void testIsNegotiatingContent()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.restlet.resource.Directory#setAlphaComparator()}.
     */
    @Ignore
    @Test
    public void testSetAlphaComparator()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.restlet.resource.Directory#setAlphaNumComparator()}.
     */
    @Ignore
    @Test
    public void testSetAlphaNumComparator()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
}
