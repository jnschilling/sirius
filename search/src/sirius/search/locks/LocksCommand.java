/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.search.locks;

import sirius.kernel.cache.Cache;
import sirius.kernel.cache.CacheManager;
import sirius.kernel.commons.Value;
import sirius.kernel.di.std.Part;
import sirius.kernel.di.std.Register;
import sirius.kernel.nls.NLS;
import sirius.web.health.console.Command;

/**
 * Provides an administrative command to show and kill locks.
 *
 * @author Andreas Haufler (aha@scireum.de)
 * @since 2014/11
 */
@Register(name = "locks", framework = "search.locks")
public class LocksCommand implements Command {

    @Part
    private LockManager lm;

    @Override
    public void execute(Output output, String... params) throws Exception {
        if (params.length > 0) {
                    output.apply("Unlocking: %s", params[0]);
            lm.killLock(params[0]);
        } else {
            output.line("Use locks <name> to forcefully kill that lock...");
        }
        output.blankLine();
        output.apply("%-28s %15s %15s %19s", "NAME", "NODE", "SECTION", "SINCE");
        output.separator();
        for (LockInfo li : lm.getLocks()) {
            output.apply("%-28s %15s %15s %19s", li.getId(), li.getCurrentOwnerNode(), li.getCurrentOwnerSection(),
                         NLS.toUserString(li.getLockedSince()));
        }
        output.separator();
    }

    @Override
    public String getName() {
        return "locks";
    }

    @Override
    public String getDescription() {
        return "Lists all currently held locks managed by the LockManager";
    }
}
