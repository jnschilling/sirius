/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.web.dispatch;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.serversass.Generator;
import sirius.kernel.Sirius;
import sirius.kernel.commons.PriorityCollector;
import sirius.kernel.commons.Strings;
import sirius.kernel.commons.Tuple;
import sirius.kernel.di.std.Register;
import sirius.kernel.health.Log;
import sirius.web.http.WebContext;
import sirius.web.http.WebDispatcher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Dispatches all URLs below <code>/assets</code>.
 * <p>
 * All assets are fetched from the classpath and should be located in the <tt>resources</tt> source root (below the
 * <tt>assets</tt> directory).
 * </p>
 * <p>
 * This dispatcher tries to support caching as well as zero-copy delivery of static files if possible.
 * </p>
 *
 * @author Andreas Haufler (aha@scireum.de)
 * @since 2013/11
 */
@Register
public class AssetsDispatcher implements WebDispatcher {

    @Override
    public int getPriority() {
        return PriorityCollector.DEFAULT_PRIORITY;
    }

    @Override
    public boolean preDispatch(WebContext ctx) throws Exception {
        return false;
    }

    @Override
    public boolean dispatch(WebContext ctx) throws Exception {
        if (!ctx.getRequest().getUri().startsWith("/assets") || HttpMethod.GET != ctx.getRequest().getMethod()) {
            return false;
        }
        String uri = ctx.getRequestedURI();
        if (uri.startsWith("/assets/dynamic")) {
            uri = uri.substring(16);
            Tuple<String, String> pair = Strings.split(uri, "/");
            uri = "/assets/" + pair.getSecond();
        }
        URL url = getClass().getResource(uri);
        if (url == null) {
            url = getClass().getResource("/assets/defaults" + uri.substring(7));
        }
        if (url == null && uri.endsWith(".css")) {
            String scssUri = uri.substring(0, uri.length() - 4) + ".scss";
            url = getClass().getResource(scssUri);
            if (url != null) {
                handleSASS(ctx, uri, scssUri, url);
                return true;
            }
        }
        if (url == null) {
            ctx.respondWith().error(HttpResponseStatus.NOT_FOUND);
        } else if ("file".equals(url.getProtocol())) {
            ctx.respondWith().file(new File(url.toURI()));
        } else {
            ctx.respondWith().resource(url.openConnection());
        }
        return true;
    }

    private static final Log SASS_LOG = Log.get("sass");

    private static class SIRIUSGenerator extends Generator {
        @Override
        public void debug(String message) {
            SASS_LOG.FINE(message);
        }

        @Override
        public void warn(String message) {
            SASS_LOG.WARN(message);
        }
    }

    private void handleSASS(WebContext ctx, String cssUri, String scssUri, URL url) throws IOException {
        URLConnection connection = url.openConnection();
        String cacheKey = cssUri.substring(1).replaceAll("[^a-zA-Z0-9\\-_\\.]", "_");
        File file = new File(new File("web-cache"), cacheKey);

        if (!file.exists() || file.lastModified() < connection.getLastModified()) {
            if (Sirius.isDev()) {
                SASS_LOG.INFO("Compiling: " + scssUri);
            }
            SIRIUSGenerator gen = new SIRIUSGenerator();
            gen.importStylesheet(scssUri);
            gen.compile();
            FileWriter writer = new FileWriter(file, false);
            writer.write(gen.toString());
            writer.close();
        }

        ctx.respondWith().named(cssUri.substring(cssUri.lastIndexOf("/") + 1)).file(file);
    }
}
