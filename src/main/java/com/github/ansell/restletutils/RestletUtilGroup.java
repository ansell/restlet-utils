/**
 * 
 */
package com.github.ansell.restletutils;

import org.restlet.security.Group;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class RestletUtilGroup extends Group
{
    
    /**
     * 
     */
    public RestletUtilGroup()
    {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param name
     * @param description
     */
    public RestletUtilGroup(final String name, final String description)
    {
        super(name, description);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param name
     * @param description
     * @param inheritingRoles
     */
    public RestletUtilGroup(final String name, final String description, final boolean inheritingRoles)
    {
        super(name, description, inheritingRoles);
        // TODO Auto-generated constructor stub
    }
    
}
