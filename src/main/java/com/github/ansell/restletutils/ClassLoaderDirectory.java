package com.github.ansell.restletutils;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Reference;
import org.restlet.resource.Directory;

/**
 * Original source: https://gist.github.com/1945499
 * 
 * Makes it possible to use the class loader as a restlet directory
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ClassLoaderDirectory extends Directory
{
    
    private ClassLoader _cl;
    
    public ClassLoaderDirectory(final Context context, final Reference rootLocalReference, final ClassLoader cl)
    {
        
        super(context, rootLocalReference);
        this._cl = cl;
    }
    
    @Override
    public void handle(final Request request, final Response response)
    {
        final ClassLoader saveCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this._cl);
        super.handle(request, response);
        Thread.currentThread().setContextClassLoader(saveCL);
    }
    
}