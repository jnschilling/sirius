package sirius.web.health;

import sirius.kernel.di.std.Register;
import sirius.web.http.WebContext;
import sirius.web.controller.BasicController;
import sirius.web.controller.Controller;
import sirius.web.controller.Routed;

/**
 * Created with IntelliJ IDEA.
 * User: aha
 * Date: 27.07.13
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
@Register(classes = Controller.class)
public class SystemController extends BasicController {

    @Routed("/system/console")
    public void console(WebContext ctx) {
        ctx.respondWith().cached().template("/view/system/console.html");
    }

}