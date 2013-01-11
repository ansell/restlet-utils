package com.github.ansell.restletutils.test;

import org.restlet.Restlet;
import org.restlet.data.LocalReference;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ansell.restletutils.ClassLoaderDirectory;
import com.github.ansell.restletutils.CompositeClassLoader;

/**
 * This class handles all requests from clients to the OAS Web Service.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class OasRestletExampleTestApplication extends org.restlet.Application
{
    private static final Logger log = LoggerFactory.getLogger(OasRestletExampleTestApplication.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = OasRestletExampleTestApplication.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = OasRestletExampleTestApplication.log.isDebugEnabled();
    private static final boolean _INFO = OasRestletExampleTestApplication.log.isInfoEnabled();
    private ClassLoaderDirectory directory;
    
    // /**
    // * The Freemarker template configuration.
    // */
    // private Configuration freemarkerConfiguration;
    //
    /**
     * Default Constructor.
     * 
     * Adds the necessary file protocols and sets up the template location.
     */
    public OasRestletExampleTestApplication()
    {
        // List of protocols required by the application
        this.getConnectorService().getClientProtocols().add(Protocol.HTTP);
        this.getConnectorService().getClientProtocols().add(Protocol.CLAP);
        
        // Define extensions for RDF and Javascript
        // getMetadataService().addExtension("rdf", MediaType.APPLICATION_RDF_XML, true);
        // getMetadataService().addExtension("json", MediaType.APPLICATION_JSON, true);
        // getMetadataService().addExtension("ttl", MediaType.APPLICATION_RDF_TURTLE, true);
        // getMetadataService().addExtension("n3", MediaType.TEXT_RDF_N3, true);
        // getMetadataService().addExtension("nt", MediaType.TEXT_RDF_NTRIPLES, true);
        //
        // this.getMetadataService().addExtension("js", MediaType.TEXT_JAVASCRIPT, true);
        
        // Automagically tunnel client preferences for extensions through the
        // tunnel
        // getTunnelService().setExtensionsTunnel(true);
    }
    
    /**
     * Create the necessary connections between the application and its handlers.
     */
    @Override
    public Restlet createInboundRoot()
    {
        if(OasRestletExampleTestApplication._INFO)
        {
            OasRestletExampleTestApplication.log.info("entering createInboundRoot");
        }
        
        final Router router = new Router(this.getContext());
        
        // For the original source see:
        // https://gist.github.com/602451
        final LocalReference localReference =
                LocalReference.createClapReference(LocalReference.CLAP_THREAD, "resourcetestfolder/");
        
        final CompositeClassLoader customClassLoader = new CompositeClassLoader();
        customClassLoader.addClassLoader(Thread.currentThread().getContextClassLoader());
        customClassLoader.addClassLoader(Router.class.getClassLoader());
        
        this.setDirectory(new ClassLoaderDirectory(this.getContext(), localReference, customClassLoader));
        
        this.getDirectory().setListingAllowed(true);
        
        final String resourcesPath = "/top";
        
        if(OasRestletExampleTestApplication._INFO)
        {
            OasRestletExampleTestApplication.log.info("attaching resource handler to path=" + resourcesPath);
        }
        
        router.attach(resourcesPath, this.getDirectory());
        
        return router;
    }
    
    /**
     * @return the directory
     */
    public ClassLoaderDirectory getDirectory()
    {
        return this.directory;
    }
    
    /**
     * @param directory
     *            the directory to set
     */
    public void setDirectory(final ClassLoaderDirectory directory)
    {
        this.directory = directory;
    }
}
