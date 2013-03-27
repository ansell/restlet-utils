/**
 * 
 */
package com.github.ansell.restletutils;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.restlet.security.Role;

/**
 * Standard Oas Roles that are likely to be used in applications.
 * 
 * NOTE: Users should only rely on OasRole, not the StandardOASRoles enumeration.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public enum RestletUtilRoles implements RestletUtilRole
{
    ADMIN("Administrator", "An administrator of the Ontology Annotation Services Application",
            "http://purl.org/oas/ontology/roles/administrator"),
    
    AUTHENTICATED("Authenticated User", "A user of the Ontology Annotation Services Application",
            "http://purl.org/oas/ontology/roles/authenticated");
    
    public static RestletUtilRole getRoleByName(final String name)
    {
        for(final RestletUtilRole nextRole : RestletUtilRoles.values())
        {
            if(nextRole.getRole().getName().equals(name))
            {
                return nextRole;
            }
        }
        
        return null;
    }
    
    public static RestletUtilRole getRoleByUri(final URI nextUri)
    {
        for(final RestletUtilRole nextRole : RestletUtilRoles.values())
        {
            if(nextRole.getURI().equals(nextUri))
            {
                return nextRole;
            }
        }
        
        return null;
    }
    
    public static List<Role> getRoles()
    {
        final List<Role> result = new ArrayList<Role>(RestletUtilRoles.values().length);
        
        for(final RestletUtilRole nextRole : RestletUtilRoles.values())
        {
            // WARNING: After Restlet-2.1RC5 Roles will only be considered equal if they are the
            // same java object, so this must not create a new Role each time
            result.add(nextRole.getRole());
        }
        
        return result;
    }
    
    private final Role role;
    
    private final URI uri;
    
    RestletUtilRoles(final String roleName, final String description, final String uriString)
    {
        this.role = new Role(roleName, description);
        this.uri = ValueFactoryImpl.getInstance().createURI(uriString);
    }
    
    /**
     * @return the description
     */
    @Override
    public String getDescription()
    {
        return this.role.getDescription();
    }
    
    /**
     * @return the name
     */
    @Override
    public String getName()
    {
        return this.role.getName();
    }
    
    @Override
    public Role getRole()
    {
        return this.role;
    }
    
    @Override
    public URI getURI()
    {
        return this.uri;
    }
    
}
