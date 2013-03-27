/**
 * 
 */
package com.github.ansell.restletutils;

import org.openrdf.model.URI;
import org.restlet.security.Role;

/**
 * An interface used to encapsulate roles for OAS users.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public interface RestletUtilRole
{
    
    /**
     * 
     * @return A description for the role.
     */
    String getDescription();
    
    /**
     * 
     * @return The unique name for this role that can be used in systems that do not accept URIs.
     */
    String getName();
    
    /**
     * 
     * NOTE: Restlet do not support multiple identities for roles across applications, so we must
     * resort to a single role in order to use this system.
     * 
     * @return The single Restlet Role object used for this role in all applications.
     */
    Role getRole();
    
    /**
     * 
     * @return A unique URI for this role.
     */
    URI getURI();
    
    /**
     * 
     * @return True if this role can be assigned to users and false if it is only assigned by the
     *         system based on some internal state.
     */
    boolean isAssignable();
    
}
