/**
 * 
 */
package com.github.ansell.restletutils.test;

import info.aduna.iteration.Iterations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.data.Parameter;
import org.restlet.security.Enroler;
import org.restlet.security.Group;
import org.restlet.security.Role;
import org.restlet.security.User;
import org.restlet.security.Verifier;
import org.restlet.util.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ansell.restletutils.RestletUtilRoles;
import com.github.ansell.restletutils.RestletUtilSesameRealm;
import com.github.ansell.restletutils.RestletUtilUser;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class RestletUtilSesameRealmTest
{
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    private Repository testRepository;
    private URI testUserManagementContext;
    private RestletUtilSesameRealm testRealm;
    private URI testReplacementUserManagementContext1;
    private URI testReplacementUserManagementContext2;
    private Group testGroupNotInheriting;
    private Group testGroupInheriting;
    private Role testRoleAdmin;
    private Role testRoleAuthenticated;
    private RestletUtilUser testUserAuthenticated;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testRepository = new SailRepository(new MemoryStore());
        this.testRepository.initialize();
        
        this.testUserManagementContext =
                this.testRepository.getValueFactory().createURI("urn:oas:test:usermanagementgraph");
        this.testReplacementUserManagementContext1 =
                this.testRepository.getValueFactory().createURI("urn:oas:test:usermanagementgraph:replacement");
        this.testReplacementUserManagementContext2 =
                this.testRepository.getValueFactory().createURI("urn:oas:test:usermanagementgraph:otherreplacement");
        
        this.testGroupNotInheriting =
                new Group("testGroupNotInheritingName", "Description for non-inheriting test group", false);
        this.testGroupInheriting = new Group("testGroupInheritingName", "Description for inheriting test group", true);
        
        this.testUserAuthenticated =
                new RestletUtilUser("testUser1", "mySecret", "Mr Test", "User", "test_user1@example.org");
        
        this.testRoleAdmin = RestletUtilRoles.ADMIN.getRole();
        this.testRoleAuthenticated = RestletUtilRoles.AUTHENTICATED.getRole();
        
        this.testRealm = new RestletUtilSesameRealm(this.testRepository, this.testUserManagementContext);
        this.testRealm.start();
        
        Assert.assertTrue(this.testRealm.isStarted());
        
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testRealm.stop();
        Assert.assertTrue(this.testRealm.isStopped());
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.RestletUtilSesameRealm#findUser(java.lang.String)}.
     */
    @Test
    public final void testDeleteUser() throws Exception
    {
        this.testRealm.addUser(this.testUserAuthenticated);
        
        final RepositoryConnection conn = this.testRepository.getConnection();
        
        try
        {
            if(this.log.isDebugEnabled())
            {
                for(final Statement nextStatement : Iterations.asList(conn.getStatements(null, null, null, true)))
                {
                    this.log.debug("nextStatement: {}", nextStatement);
                }
            }
            
            Assert.assertEquals(6, conn.size());
            Assert.assertEquals(6, conn.size(this.testUserManagementContext));
        }
        finally
        {
            conn.close();
        }
        
        final URI findUser = this.testRealm.deleteUser(this.testUserAuthenticated);
        
        Assert.assertNotNull(findUser);
        
        final RepositoryConnection secondConn = this.testRepository.getConnection();
        
        try
        {
            if(this.log.isDebugEnabled())
            {
                for(final Statement nextStatement : Iterations.asList(secondConn.getStatements(null, null, null, true)))
                {
                    this.log.debug("nextStatement: {}", nextStatement);
                }
            }
            
            Assert.assertEquals(0, secondConn.size());
            Assert.assertEquals(0, secondConn.size(this.testUserManagementContext));
        }
        finally
        {
            secondConn.close();
        }
        
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.RestletUtilSesameRealm#findGroups(org.restlet.security.User)}
     * .
     */
    @Test
    public final void testFindGroupsUser()
    {
        final Set<Group> beforeGroups = this.testRealm.findGroups(this.testUserAuthenticated);
        
        Assert.assertEquals(0, beforeGroups.size());
        
        this.testRealm.addUser(this.testUserAuthenticated);
        
        this.testGroupNotInheriting.getMemberUsers().add(this.testUserAuthenticated);
        
        this.testRealm.addRootGroup(this.testGroupNotInheriting);
        
        final Set<Group> afterGroups = this.testRealm.findGroups(this.testUserAuthenticated);
        
        Assert.assertEquals(1, afterGroups.size());
        
        final Group afterGroup = afterGroups.iterator().next();
        
        Assert.assertEquals(this.testGroupNotInheriting, afterGroup);
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.RestletUtilSesameRealm#findGroups(org.restlet.security.User, boolean)}
     * .
     */
    @Ignore
    @Test
    public final void testFindGroupsUserBoolean()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.RestletUtilSesameRealm#findRoles(org.restlet.security.Group)}
     * .
     */
    @Test
    public final void testFindRolesGroup()
    {
        final Set<Role> beforeRoles = this.testRealm.findRoles(this.testGroupNotInheriting);
        
        Assert.assertEquals(0, beforeRoles.size());
        
        this.testRealm.addRootGroup(this.testGroupNotInheriting);
        
        this.testRealm.map(this.testGroupNotInheriting, this.testRoleAdmin);
        
        final Set<Role> afterRoles = this.testRealm.findRoles(this.testGroupNotInheriting);
        
        Assert.assertEquals(1, afterRoles.size());
        
        final Role afterRole = afterRoles.iterator().next();
        
        Assert.assertEquals(this.testRoleAdmin, afterRole);
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.RestletUtilSesameRealm#findRoles(java.util.Set)}.
     */
    @Test
    public final void testFindRolesSetOfGroup()
    {
        final Set<Group> setOfGroup =
                new HashSet<Group>(Arrays.asList(this.testGroupInheriting, this.testGroupNotInheriting));
        
        final Set<Role> beforeRoles = this.testRealm.findRoles(setOfGroup);
        
        Assert.assertEquals(0, beforeRoles.size());
        
        this.testRealm.addRootGroup(this.testGroupNotInheriting);
        this.testRealm.addRootGroup(this.testGroupInheriting);
        
        this.testRealm.map(this.testGroupInheriting, this.testRoleAuthenticated);
        
        final Set<Role> afterRoles = this.testRealm.findRoles(setOfGroup);
        
        Assert.assertEquals(1, afterRoles.size());
        
        final Role afterRole = afterRoles.iterator().next();
        
        Assert.assertEquals(this.testRoleAuthenticated, afterRole);
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.RestletUtilSesameRealm#findRoles(org.restlet.security.User)}
     * .
     */
    @Test
    public final void testFindRolesUser()
    {
        final Set<Role> beforeRoles = this.testRealm.findRoles(this.testUserAuthenticated);
        
        Assert.assertEquals(0, beforeRoles.size());
        
        this.testRealm.addUser(this.testUserAuthenticated);
        
        this.testRealm.map(this.testUserAuthenticated, this.testRoleAuthenticated);
        
        final Set<Role> afterRoles = this.testRealm.findRoles(this.testUserAuthenticated);
        
        Assert.assertEquals(1, afterRoles.size());
        
        final Role role = afterRoles.iterator().next();
        
        Assert.assertEquals(role, this.testRoleAuthenticated);
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.RestletUtilSesameRealm#findUser(java.lang.String)}.
     */
    @Test
    public final void testFindUser() throws Exception
    {
        this.testRealm.addUser(this.testUserAuthenticated);
        
        final RepositoryConnection conn = this.testRepository.getConnection();
        
        try
        {
            Assert.assertEquals(6, conn.size());
            Assert.assertEquals(6, conn.size(this.testUserManagementContext));
            
            if(this.log.isDebugEnabled())
            {
                for(final Statement nextStatement : Iterations.asList(conn.getStatements(null, null, null, true)))
                {
                    this.log.debug("nextStatement: {}", nextStatement);
                }
            }
        }
        finally
        {
            conn.close();
        }
        
        final User findUser = this.testRealm.findUser(this.testUserAuthenticated.getIdentifier());
        
        Assert.assertNotNull(findUser);
    }
    
    /**
     * Test method for {@link org.restlet.security.Realm#getEnroler()}.
     */
    @Test
    public final void testGetEnroler()
    {
        Assert.assertNotNull(this.testRealm.getEnroler());
    }
    
    /**
     * Test method for {@link org.restlet.security.Realm#getName()}.
     */
    @Test
    public final void testGetName()
    {
        Assert.assertNull(this.testRealm.getName());
        
        this.testRealm.setName("Testing realm 1357");
        
        Assert.assertEquals("Testing realm 1357", this.testRealm.getName());
        
        this.testRealm.setName(null);
        
        Assert.assertNull(this.testRealm.getName());
    }
    
    /**
     * Test method for {@link org.restlet.security.Realm#getParameters()}.
     */
    @Test
    public final void testGetParameters()
    {
        Assert.assertNotNull(this.testRealm.getParameters());
        
        Assert.assertEquals(0, this.testRealm.getParameters().size());
    }
    
    /**
     * Test method for {@link com.github.ansell.restletutils.RestletUtilSesameRealm#getRootGroups()}
     * .
     */
    @Test
    public final void testGetRootGroups()
    {
        Assert.assertTrue(this.testRealm.getRootGroups().isEmpty());
        
        this.testRealm.addRootGroup(this.testGroupNotInheriting);
        
        Assert.assertFalse(this.testRealm.getRootGroups().isEmpty());
    }
    
    /**
     * Test method for {@link org.restlet.security.Realm#getVerifier()}.
     */
    @Test
    public final void testGetVerifier()
    {
        Assert.assertNotNull(this.testRealm.getVerifier());
    }
    
    /**
     * Test method for {@link org.restlet.security.Realm#isStarted()}.
     */
    @Test
    public final void testIsStarted()
    {
        Assert.assertTrue(this.testRealm.isStarted());
    }
    
    /**
     * Test method for {@link org.restlet.security.Realm#isStopped()}.
     * 
     * @throws Exception
     */
    @Test
    public final void testIsStopped() throws Exception
    {
        this.testRealm.stop();
        Assert.assertTrue(this.testRealm.isStopped());
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.RestletUtilSesameRealm#map(org.restlet.security.Group, org.restlet.security.Role)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public final void testMapGroupRole() throws Exception
    {
        this.testRealm.addRootGroup(this.testGroupNotInheriting);
        
        final RepositoryConnection conn = this.testRepository.getConnection();
        
        try
        {
            Assert.assertEquals(5, conn.size());
            Assert.assertEquals(5, conn.size(this.testUserManagementContext));
        }
        finally
        {
            conn.close();
        }
        
        this.testRealm.map(this.testGroupNotInheriting, this.testRoleAdmin);
        
        final RepositoryConnection conn2 = this.testRepository.getConnection();
        
        try
        {
            Assert.assertEquals(8, conn2.size());
            Assert.assertEquals(8, conn2.size(this.testUserManagementContext));
        }
        finally
        {
            conn2.close();
        }
        
        final Set<Role> roles = this.testRealm.findRoles(this.testGroupNotInheriting);
        
        Assert.assertEquals(1, roles.size());
        Assert.assertTrue(roles.contains(this.testRoleAdmin));
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.RestletUtilSesameRealm#map(org.restlet.security.User, org.restlet.security.Role)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public final void testMapUserRole() throws Exception
    {
        this.testRealm.addUser(this.testUserAuthenticated);
        
        final RepositoryConnection conn = this.testRepository.getConnection();
        
        try
        {
            Assert.assertEquals(6, conn.size());
            Assert.assertEquals(6, conn.size(this.testUserManagementContext));
            
            if(this.log.isDebugEnabled())
            {
                for(final Statement nextStatement : Iterations.asList(conn.getStatements(null, null, null, true)))
                {
                    this.log.debug("nextStatement: {}", nextStatement);
                }
            }
        }
        finally
        {
            conn.close();
        }
        
        this.testRealm.map(this.testUserAuthenticated, this.testRoleAuthenticated);
        
        final RepositoryConnection conn2 = this.testRepository.getConnection();
        
        try
        {
            Assert.assertEquals(9, conn2.size());
            Assert.assertEquals(9, conn2.size(this.testUserManagementContext));
        }
        finally
        {
            conn2.close();
        }
        
        final Set<Role> roles = this.testRealm.findRoles(this.testUserAuthenticated);
        
        Assert.assertEquals(1, roles.size());
        Assert.assertTrue(roles.contains(this.testRoleAuthenticated));
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.RestletUtilSesameRealm#OasSesameRealm(org.openrdf.repository.Repository, org.openrdf.model.URI[])}
     * .
     */
    @Test
    public final void testOasSesameRealm()
    {
        // at minimum, the enroler and verifier should both return with defaults when the realm is
        // active
        Assert.assertNotNull(this.testRealm.getEnroler());
        Assert.assertNotNull(this.testRealm.getVerifier());
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.RestletUtilSesameRealm#setContexts(org.openrdf.model.URI[])}
     * .
     */
    @Test
    public final void testSetContexts()
    {
        // This context is given in the constructor in setUp
        final URI[] beforeSetContexts = this.testRealm.getContexts();
        Assert.assertNotNull(beforeSetContexts);
        Assert.assertEquals(1, beforeSetContexts.length);
        Assert.assertEquals(this.testUserManagementContext, beforeSetContexts[0]);
        
        this.testRealm.setContexts(this.testReplacementUserManagementContext1,
                this.testReplacementUserManagementContext2);
        
        final URI[] afterSetContexts = this.testRealm.getContexts();
        Assert.assertNotNull(afterSetContexts);
        Assert.assertEquals(2, afterSetContexts.length);
        Assert.assertEquals(this.testReplacementUserManagementContext1, afterSetContexts[0]);
        Assert.assertEquals(this.testReplacementUserManagementContext2, afterSetContexts[1]);
        
    }
    
    /**
     * Test method for {@link org.restlet.security.Realm#setEnroler(org.restlet.security.Enroler)}.
     */
    @Test
    public final void testSetEnroler()
    {
        this.testRealm.setEnroler(new Enroler()
            {
                @Override
                public void enrole(final ClientInfo clientInfo)
                {
                    
                }
            });
    }
    
    /**
     * Test method for {@link org.restlet.security.Realm#setName(java.lang.String)}.
     */
    @Test
    public final void testSetName()
    {
        Assert.assertNull(this.testRealm.getName());
        
        this.testRealm.setName("Testing realm 1357");
        
        Assert.assertEquals("Testing realm 1357", this.testRealm.getName());
        
        this.testRealm.setName(null);
        
        Assert.assertNull(this.testRealm.getName());
    }
    
    /**
     * Test method for {@link org.restlet.security.Realm#setParameters(org.restlet.util.Series)}.
     */
    @Test
    public final void testSetParameters()
    {
        Assert.assertEquals(0, this.testRealm.getParameters().size());
        
        final List<Parameter> list = new ArrayList<Parameter>(1);
        
        list.add(new Parameter("testName", "123"));
        
        this.testRealm.setParameters(new Series<Parameter>(Parameter.class, list));
        
        Assert.assertEquals(1, this.testRealm.getParameters().size());
        
        final Parameter parameter = this.testRealm.getParameters().get(0);
        Assert.assertEquals("testName", parameter.getName());
        Assert.assertEquals("123", parameter.getValue());
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.RestletUtilSesameRealm#setRepository(org.openrdf.repository.Repository)}
     * .
     */
    @Test
    public final void testSetRepository()
    {
        Assert.assertNotNull(this.testRealm.getRepository());
        
        this.testRealm.setRepository(null);
        
        Assert.assertNull(this.testRealm.getRepository());
        
        this.testRealm.setRepository(new SailRepository(new MemoryStore()));
        
        Assert.assertNotNull(this.testRealm.getRepository());
    }
    
    /**
     * Test method for {@link org.restlet.security.Realm#setVerifier(org.restlet.security.Verifier)}
     * .
     */
    @Test
    public final void testSetVerifier()
    {
        this.testRealm.setVerifier(new Verifier()
            {
                @Override
                public int verify(final Request request, final Response response)
                {
                    return 0;
                }
            });
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.RestletUtilSesameRealm#unmap(org.restlet.security.Group, org.restlet.security.Role)}
     * .
     */
    @Test
    public final void testUnmapGroupRole()
    {
        this.testRealm.addRootGroup(this.testGroupNotInheriting);
        
        this.testRealm.map(this.testGroupNotInheriting, this.testRoleAdmin);
        
        final Set<Role> findRoles = this.testRealm.findRoles(this.testGroupNotInheriting);
        
        Assert.assertFalse(findRoles.isEmpty());
        
        this.testRealm.unmap(this.testGroupNotInheriting, this.testRoleAdmin);
        
        final Set<Role> findRoles2 = this.testRealm.findRoles(this.testGroupNotInheriting);
        
        Assert.assertTrue(findRoles2.isEmpty());
        
    }
    
    /**
     * Test method for
     * {@link com.github.ansell.restletutils.RestletUtilSesameRealm#unmap(org.restlet.security.User, org.restlet.security.Role)}
     * .
     */
    @Test
    public final void testUnmapUserRole()
    {
        this.testRealm.addUser(this.testUserAuthenticated);
        
        this.testRealm.map(this.testUserAuthenticated, this.testRoleAdmin);
        
        final Set<Role> findRoles = this.testRealm.findRoles(this.testUserAuthenticated);
        
        Assert.assertFalse(findRoles.isEmpty());
        
        this.testRealm.unmap(this.testUserAuthenticated, this.testRoleAdmin);
        
        final Set<Role> findRoles2 = this.testRealm.findRoles(this.testUserAuthenticated);
        
        Assert.assertTrue(findRoles2.isEmpty());
        
    }
    
}
