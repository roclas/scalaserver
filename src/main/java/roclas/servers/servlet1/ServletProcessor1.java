package roclas.servers.servlet1;

/**
 * Created by carlos on 02/06/16.
 */

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.io.File;
import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ServletProcessor1 {
    public static String WEB_ROOT = System.getProperty("user.dir") + File.separator + "deployedServlets/";

    public void process(Request request, Response response) {
        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        URLClassLoader loader = null;
        try {
            // create a URLClassLoader
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(WEB_ROOT);
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            urls[0] = new URL(null, repository, streamHandler);
            ClassLoader currentLoader = getClass().getClassLoader();
            loader = new URLClassLoader(urls,currentLoader);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        Class myClass = null;
        try {
            myClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            System.out.println(e.toString());
        }
        Servlet servlet = null;
        try {
            servlet = (Servlet)myClass.newInstance();
            servlet.service((ServletRequest) request, (ServletResponse) response);
        } catch (Exception e) {
            System.out.println("Exception creating the servlet:"+e.toString());
        } catch (Throwable e) {
            System.out.println(e.toString());
        }
    }
}
