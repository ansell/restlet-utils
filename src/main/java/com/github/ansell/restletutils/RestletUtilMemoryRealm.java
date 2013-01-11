/**
 * 
 */
package com.github.ansell.restletutils;

/**
 * Copyright 2005-2012 Restlet S.A.S.
 * 
 * The contents of this file are subject to the terms of one of the following open source licenses:
 * Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL 1.0 (the "Licenses"). You can select the
 * license that you prefer but you may not use this file except in compliance with one of these
 * Licenses.
 * 
 * You can obtain a copy of the Apache 2.0 license at http://www.opensource.org/licenses/apache-2.0
 * 
 * You can obtain a copy of the LGPL 3.0 license at http://www.opensource.org/licenses/lgpl-3.0
 * 
 * You can obtain a copy of the LGPL 2.1 license at http://www.opensource.org/licenses/lgpl-2.1
 * 
 * You can obtain a copy of the CDDL 1.0 license at http://www.opensource.org/licenses/cddl1
 * 
 * You can obtain a copy of the EPL 1.0 license at http://www.opensource.org/licenses/eclipse-1.0
 * 
 * See the Licenses for the specific language governing permissions and limitations under the
 * Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less limitations,
 * transferable or non-transferable, directly at http://www.restlet.com/products/restlet-framework
 * 
 * Restlet is a registered trademark of Restlet S.A.S.
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.engine.security.RoleMapping;
import org.restlet.security.Enroler;
import org.restlet.security.Group;
import org.restlet.security.LocalVerifier;
import org.restlet.security.Realm;
import org.restlet.security.Role;
import org.restlet.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security realm based on a memory model. The model is composed of root groups, users and mapping
 * to associated roles.
 * 
 * Patched for OAS to create a user from the realm instead of just creating a new user using the
 * identifier.
 * 
 * @author Jerome Louvel
 */
public class RestletUtilMemoryRealm extends Realm
{
    
    /**
     * Enroler based on the default security model.
     */
    private class DefaultEnroler implements Enroler
    {
        
        @Override
        public void enrole(final ClientInfo clientInfo)
        {
            final User user = RestletUtilMemoryRealm.this.findUser(clientInfo.getUser().getIdentifier());
            
            if(user != null)
            {
                // Find all the inherited groups of this user
                final Set<Group> userGroups = RestletUtilMemoryRealm.this.findGroups(user);
                
                // Add roles specific to this user
                final Set<Role> userRoles = RestletUtilMemoryRealm.this.findRoles(user);
                
                for(final Role role : userRoles)
                {
                    clientInfo.getRoles().add(role);
                }
                
                if(clientInfo.isAuthenticated())
                {
                    clientInfo.getRoles().add(RestletUtilRoles.AUTHENTICATED.getRole());
                }
                
                // Add roles common to group members
                final Set<Role> groupRoles = RestletUtilMemoryRealm.this.findRoles(userGroups);
                
                for(final Role role : groupRoles)
                {
                    clientInfo.getRoles().add(role);
                }
            }
        }
    }
    
    /**
     * Verifier based on the default security model. It looks up users in the mapped organizations.
     */
    private class DefaultVerifier extends LocalVerifier
    {
        @Override
        protected User createUser(final String identifier, final Request request, final Response response)
        {
            final User checkUser = RestletUtilMemoryRealm.this.findUser(identifier);
            
            if(checkUser == null)
            {
                RestletUtilMemoryRealm.this.log.error("Cannot create a user for the given identifier: {}", identifier);
                throw new IllegalArgumentException("Cannot create a user for the given identifier");
            }
            
            final User result =
                    new User(identifier, (char[])null, checkUser.getFirstName(), checkUser.getLastName(),
                            checkUser.getEmail());
            
            return result;
        }
        
        @Override
        public char[] getLocalSecret(final String identifier)
        {
            char[] result = null;
            final User user = RestletUtilMemoryRealm.this.findUser(identifier);
            
            if(user != null)
            {
                result = user.getSecret();
            }
            
            return result;
        }
    }
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    /** The modifiable list of role mappings. */
    private final List<RoleMapping> roleMappings;
    
    /** The modifiable list of root groups. */
    private final List<Group> rootGroups;
    
    /** The modifiable list of users. */
    private final List<User> users;
    
    /**
     * Constructor.
     */
    public RestletUtilMemoryRealm()
    {
        this.setVerifier(new DefaultVerifier());
        this.setEnroler(new DefaultEnroler());
        this.rootGroups = new CopyOnWriteArrayList<Group>();
        this.roleMappings = new CopyOnWriteArrayList<RoleMapping>();
        this.users = new CopyOnWriteArrayList<User>();
    }
    
    /**
     * Recursively adds groups where a given user is a member.
     * 
     * @param user
     *            The member user.
     * @param userGroups
     *            The set of user groups to update.
     * @param currentGroup
     *            The current group to inspect.
     * @param stack
     *            The stack of ancestor groups.
     * @param inheritOnly
     *            Indicates if only the ancestors groups that have their "inheritRoles" property
     *            enabled should be added.
     */
    private void addGroups(final User user, final Set<Group> userGroups, final Group currentGroup,
            final List<Group> stack, final boolean inheritOnly)
    {
        if((currentGroup != null) && !stack.contains(currentGroup))
        {
            stack.add(currentGroup);
            
            if(currentGroup.getMemberUsers().contains(user))
            {
                userGroups.add(currentGroup);
                
                // Add the ancestor groups as well
                boolean inherit = !inheritOnly || currentGroup.isInheritingRoles();
                Group group;
                
                for(int i = stack.size() - 2; inherit && (i >= 0); i--)
                {
                    group = stack.get(i);
                    userGroups.add(group);
                    inherit = !inheritOnly || group.isInheritingRoles();
                }
            }
            
            for(final Group group : currentGroup.getMemberGroups())
            {
                this.addGroups(user, userGroups, group, stack, inheritOnly);
            }
        }
    }
    
    /**
     * Finds the set of groups where a given user is a member. Note that inheritable ancestors
     * groups are also returned.
     * 
     * @param user
     *            The member user.
     * @return The set of groups.
     */
    public Set<Group> findGroups(final User user)
    {
        return this.findGroups(user, true);
    }
    
    /**
     * Finds the set of groups where a given user is a member.
     * 
     * @param user
     *            The member user.
     * @param inheritOnly
     *            Indicates if only the ancestors groups that have their "inheritRoles" property
     *            enabled should be added.
     * @return The set of groups.
     */
    public Set<Group> findGroups(final User user, final boolean inheritOnly)
    {
        final Set<Group> result = new HashSet<Group>();
        List<Group> stack;
        
        // Recursively find user groups
        for(final Group group : this.getRootGroups())
        {
            stack = new ArrayList<Group>();
            this.addGroups(user, result, group, stack, inheritOnly);
        }
        
        return result;
    }
    
    /**
     * Finds the roles mapped to given user group.
     * 
     * @param userGroup
     *            The user group.
     * @return The roles found.
     */
    public Set<Role> findRoles(final Group userGroup)
    {
        final Set<Role> result = new HashSet<Role>();
        
        Object source;
        for(final RoleMapping mapping : this.getRoleMappings())
        {
            source = mapping.getSource();
            
            if((userGroup != null) && userGroup.equals(source))
            {
                result.add(mapping.getTarget());
            }
        }
        
        return result;
    }
    
    /**
     * Finds the roles mapped to given user groups.
     * 
     * @param userGroups
     *            The user groups.
     * @return The roles found.
     */
    public Set<Role> findRoles(final Set<Group> userGroups)
    {
        final Set<Role> result = new HashSet<Role>();
        
        Object source;
        for(final RoleMapping mapping : this.getRoleMappings())
        {
            source = mapping.getSource();
            
            if((userGroups != null) && userGroups.contains(source))
            {
                result.add(mapping.getTarget());
            }
        }
        
        return result;
    }
    
    /**
     * Finds the roles mapped to a given user.
     * 
     * @param user
     *            The user.
     * @return The roles found.
     */
    public Set<Role> findRoles(final User user)
    {
        final Set<Role> result = new HashSet<Role>();
        
        Object source;
        for(final RoleMapping mapping : this.getRoleMappings())
        {
            source = mapping.getSource();
            
            if((user != null) && user.equals(source))
            {
                result.add(mapping.getTarget());
            }
        }
        
        return result;
    }
    
    /**
     * Finds a user in the organization based on its identifier.
     * 
     * @param userIdentifier
     *            The identifier to match.
     * @return The matched user or null.
     */
    public User findUser(final String userIdentifier)
    {
        User result = null;
        User user;
        
        for(int i = 0; (result == null) && (i < this.getUsers().size()); i++)
        {
            user = this.getUsers().get(i);
            
            if(user.getIdentifier().equals(userIdentifier))
            {
                result = user;
            }
        }
        
        return result;
    }
    
    /**
     * Returns the modifiable list of role mappings.
     * 
     * @return The modifiable list of role mappings.
     */
    private List<RoleMapping> getRoleMappings()
    {
        return this.roleMappings;
    }
    
    /**
     * Returns the modifiable list of root groups.
     * 
     * @return The modifiable list of root groups.
     */
    public List<Group> getRootGroups()
    {
        return this.rootGroups;
    }
    
    /**
     * Returns the modifiable list of users.
     * 
     * @return The modifiable list of users.
     */
    public List<User> getUsers()
    {
        return this.users;
    }
    
    /**
     * Maps a group defined in a component to a role defined in the application.
     * 
     * @param group
     *            The source group.
     * @param role
     *            The target role.
     */
    public void map(final Group group, final Role role)
    {
        this.getRoleMappings().add(new RoleMapping(group, role));
    }
    
    /**
     * Maps a user defined in a component to a role defined in the application.
     * 
     * @param user
     *            The source user.
     * @param role
     *            The target role.
     */
    public void map(final User user, final Role role)
    {
        this.getRoleMappings().add(new RoleMapping(user, role));
    }
    
    /**
     * Sets the modifiable list of root groups. This method clears the current list and adds all
     * entries in the parameter list.
     * 
     * @param rootGroups
     *            A list of root groups.
     */
    public void setRootGroups(final List<Group> rootGroups)
    {
        synchronized(this.getRootGroups())
        {
            if(rootGroups != this.getRootGroups())
            {
                this.getRootGroups().clear();
                
                if(rootGroups != null)
                {
                    this.getRootGroups().addAll(rootGroups);
                }
            }
        }
    }
    
    /**
     * Sets the modifiable list of users. This method clears the current list and adds all entries
     * in the parameter list.
     * 
     * @param users
     *            A list of users.
     */
    public void setUsers(final List<User> users)
    {
        synchronized(this.getUsers())
        {
            if(users != this.getUsers())
            {
                this.getUsers().clear();
                
                if(users != null)
                {
                    this.getUsers().addAll(users);
                }
            }
        }
    }
    
    /**
     * Unmaps a group defined in a component from a role defined in the application.
     * 
     * @param group
     *            The source group.
     * @param role
     *            The target role.
     */
    public void unmap(final Group group, final Role role)
    {
        this.unmap((Object)group, role);
    }
    
    /**
     * Unmaps an element (user, group or organization) defined in a component from a role defined in
     * the application.
     * 
     * @param group
     *            The source group.
     * @param role
     *            The target role.
     */
    private void unmap(final Object source, final Role role)
    {
        RoleMapping mapping;
        for(int i = this.getRoleMappings().size(); i >= 0; i--)
        {
            mapping = this.getRoleMappings().get(i);
            
            if(mapping.getSource().equals(source) && mapping.getTarget().equals(role))
            {
                this.getRoleMappings().remove(i);
            }
        }
    }
    
    /**
     * Unmaps a user defined in a component from a role defined in the application.
     * 
     * @param user
     *            The source user.
     * @param role
     *            The target role.
     */
    public void unmap(final User user, final Role role)
    {
        this.unmap((Object)user, role);
    }
    
}
