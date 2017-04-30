package zipkin;

import eu.kielczewski.example.initializer.AppInitializer;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.AnnotationConfiguration.ClassInheritanceMap;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.client.RestTemplate;

public class EmbedServerWithAnnotationConfigurationTests {

    private Server server;
    private WebAppContext context;

    @Before
    public void setup() {
        server = new Server(8081);

        context = new WebAppContext();
        ClassInheritanceMap classMap = new ClassInheritanceMap();
        ConcurrentHashSet<String> impl = new ConcurrentHashSet<>();
        impl.add(AppInitializer.class.getName());
        classMap.put(WebApplicationInitializer.class.getName(), impl);

        context.setAttribute(AnnotationConfiguration.CLASS_INHERITANCE_MAP, classMap);
        context.setConfigurations(new Configuration[]{new AnnotationConfiguration()});

        context.setContextPath("/");
        server.setHandler(context);

        try {
            server.start();
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to start server.", e);
        }
    }

    @After
    public void tearDown() throws Exception {
        Thread.sleep(2000);
        server.stop();
        server.join();
    }

    @Test
    public void test() {
        String result = new RestTemplate().getForObject("http://localhost:8081/", String.class);
    }
}
