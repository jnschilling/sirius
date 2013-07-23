package sirius.web.dispatch;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import sirius.kernel.di.annotations.Register;
import sirius.kernel.commons.Strings;
import sirius.web.WebContext;
import sirius.web.WebDispatcher;

/**
 * Created with IntelliJ IDEA.
 * User: aha
 * Date: 14.07.13
 * Time: 21:14
 * To change this template use File | Settings | File Templates.
 */
@Register
public class NotFoundDispatcher implements WebDispatcher {
    @Override
    public int getPriority() {
        return 999;
    }

    @Override
    public boolean dispatch(WebContext ctx) throws Exception {
        ctx.respond().error(HttpResponseStatus.NOT_FOUND,
                      Strings.apply("No dispatcher found for: %s", ctx.getRequest().getUri()));
        return true;
    }
}
