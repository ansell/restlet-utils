package com.github.ansell.restletutils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Original source: https://gist.github.com/1945491
 * 
 * Chained classloaders that are necessary to use restlet to pull resources out of a jar file
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class CompositeClassLoader extends ClassLoader
{
    private List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
    
    public void addClassLoader(final ClassLoader cl)
    {
        this.classLoaders.add(cl);
    }
    
    @Override
    public URL getResource(final String name)
    {
        for(final ClassLoader cl : this.classLoaders)
        {
            final URL resource = cl.getResource(name);
            
            if(resource != null)
            {
                return resource;
            }
        }
        
        return null;
    }
    
    @Override
    public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException
    {
        for(final ClassLoader cl : this.classLoaders)
        {
            try
            {
                return cl.loadClass(name);
            }
            catch(final ClassNotFoundException ex)
            {
                
            }
        }
        
        throw new ClassNotFoundException(name);
    }
    
}