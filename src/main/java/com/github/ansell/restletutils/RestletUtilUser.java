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

package com.github.ansell.restletutils;

import java.security.Principal;

import org.restlet.security.User;

/**
 * User part of a security realm. Note the same user can be member of several groups.
 * 
 * @see Realm
 * @see Group
 * @author Jerome Louvel
 */

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class RestletUtilUser extends User implements Principal
{
    
    /** The email. */
    private volatile String email;
    
    /** The first name. */
    private volatile String firstName;
    
    /** The identifier. */
    private volatile String identifier;
    
    /** The last name. */
    private volatile String lastName;
    
    /** The secret. */
    private volatile char[] secret;
    
    /**
     * Default constructor.
     */
    public RestletUtilUser()
    {
        this(null, (char[])null, null, null, null);
    }
    
    /**
     * Constructor.
     * 
     * @param identifier
     *            The identifier (login).
     */
    public RestletUtilUser(final String identifier)
    {
        this(identifier, (char[])null, null, null, null);
    }
    
    /**
     * Constructor.
     * 
     * @param identifier
     *            The identifier (login).
     * @param secret
     *            The identification secret.
     */
    public RestletUtilUser(final String identifier, final char[] secret)
    {
        this(identifier, secret, null, null, null);
    }
    
    /**
     * Constructor.
     * 
     * @param identifier
     *            The identifier (login).
     * @param secret
     *            The identification secret.
     * @param firstName
     *            The first name.
     * @param lastName
     *            The last name.
     * @param email
     *            The email.
     */
    public RestletUtilUser(final String identifier, final char[] secret, final String firstName, final String lastName,
            final String email)
    {
        this.identifier = identifier;
        this.secret = secret;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    /**
     * Constructor.
     * 
     * @param identifier
     *            The identifier (login).
     * @param secret
     *            The identification secret.
     */
    public RestletUtilUser(final String identifier, final String secret)
    {
        this(identifier, secret.toCharArray());
    }
    
    /**
     * Constructor.
     * 
     * @param identifier
     *            The identifier (login).
     * @param secret
     *            The identification secret.
     * @param firstName
     *            The first name.
     * @param lastName
     *            The last name.
     * @param email
     *            The email.
     */
    public RestletUtilUser(final String identifier, final String secret, final String firstName, final String lastName,
            final String email)
    {
        this(identifier, secret.toCharArray(), firstName, lastName, email);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null)
        {
            return false;
        }
        if(!(obj instanceof RestletUtilUser))
        {
            return false;
        }
        final RestletUtilUser other = (RestletUtilUser)obj;
        if(this.email == null)
        {
            if(other.email != null)
            {
                return false;
            }
        }
        else if(!this.email.equals(other.email))
        {
            return false;
        }
        if(this.firstName == null)
        {
            if(other.firstName != null)
            {
                return false;
            }
        }
        else if(!this.firstName.equals(other.firstName))
        {
            return false;
        }
        if(this.identifier == null)
        {
            if(other.identifier != null)
            {
                return false;
            }
        }
        else if(!this.identifier.equals(other.identifier))
        {
            return false;
        }
        if(this.lastName == null)
        {
            if(other.lastName != null)
            {
                return false;
            }
        }
        else if(!this.lastName.equals(other.lastName))
        {
            return false;
        }
        // Do not test the secret when checking for equality or hashCode
        // if(!Arrays.equals(secret, other.secret))
        // {
        // return false;
        // }
        return true;
    }
    
    /**
     * Returns the email.
     * 
     * @return The email.
     */
    @Override
    public String getEmail()
    {
        return this.email;
    }
    
    /**
     * Returns the first name.
     * 
     * @return The first name.
     */
    @Override
    public String getFirstName()
    {
        return this.firstName;
    }
    
    /**
     * Returns the identifier.
     * 
     * @return The identifier.
     */
    @Override
    public String getIdentifier()
    {
        return this.identifier;
    }
    
    /**
     * Returns the last name.
     * 
     * @return The last name.
     */
    @Override
    public String getLastName()
    {
        return this.lastName;
    }
    
    /**
     * Returns the user identifier.
     * 
     * @see #getIdentifier()
     */
    @Override
    public String getName()
    {
        return this.getIdentifier();
    }
    
    /**
     * Returns the secret.
     * 
     * @return The secret.
     */
    @Override
    public char[] getSecret()
    {
        return this.secret;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.email == null) ? 0 : this.email.hashCode());
        result = prime * result + ((this.firstName == null) ? 0 : this.firstName.hashCode());
        result = prime * result + ((this.identifier == null) ? 0 : this.identifier.hashCode());
        result = prime * result + ((this.lastName == null) ? 0 : this.lastName.hashCode());
        // Do not test the secret when checking for equality or hashCode
        // result = prime * result + Arrays.hashCode(secret);
        return result;
    }
    
    /**
     * Sets the email.
     * 
     * @param email
     *            The email.
     */
    @Override
    public void setEmail(final String email)
    {
        this.email = email;
    }
    
    /**
     * Sets the first name.
     * 
     * @param firstName
     *            The first name.
     */
    @Override
    public void setFirstName(final String firstName)
    {
        this.firstName = firstName;
    }
    
    /**
     * Sets the identifier.
     * 
     * @param identifier
     *            The identifier.
     */
    @Override
    public void setIdentifier(final String identifier)
    {
        this.identifier = identifier;
    }
    
    /**
     * Sets the last name.
     * 
     * @param lastName
     *            The last name.
     */
    @Override
    public void setLastName(final String lastName)
    {
        this.lastName = lastName;
    }
    
    /**
     * Sets the secret.
     * 
     * @param secret
     *            The secret.
     */
    @Override
    public void setSecret(final char[] secret)
    {
        this.secret = secret;
    }
    
    @Override
    public String toString()
    {
        return this.getIdentifier();
    }
}
