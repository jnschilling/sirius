/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.kernel.di.std;

import com.google.common.collect.Lists;
import sirius.kernel.di.AnnotationProcessor;
import sirius.kernel.di.MutableGlobalContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Handles the {@link sirius.kernel.di.std.PriorityParts} annotation.
 *
 * @author Andreas Haufler (aha@scireum.de)
 * @see sirius.kernel.di.AnnotationProcessor
 * @see sirius.kernel.di.std.PriorityParts
 * @since 2013/08
 */
@Register
public class PriorityPartsAnnotationProcessor implements AnnotationProcessor {
    @Override
    public Class<? extends Annotation> getTrigger() {
        return PriorityParts.class;
    }

    @Override
    public void handle(MutableGlobalContext ctx, Object object, Field field) throws Exception {
        PriorityParts parts = field.getAnnotation(PriorityParts.class);
        if (Collection.class.isAssignableFrom(field.getType())) {
            if (!Priorized.class.isAssignableFrom(parts.value())) {
                throw new IllegalArgumentException(
                        "PriorityParts annotations may only be used with classes implementing 'Priorized'");
            }
            List<Priorized> result = Lists.newArrayList(ctx.getParts(parts.value()));
            Collections.sort(result, new Comparator<Priorized>() {
                @Override
                public int compare(Priorized o1, Priorized o2) {
                    if (o1 == o2) {
                        return 0;
                    }
                    if (o2 == null) {
                        return -1;
                    }
                    if (o1 == null) {
                        return 1;
                    }
                    return o1.getPriority() - o2.getPriority();
                }
            });
            field.set(object, result);
        } else {
            throw new IllegalArgumentException(
                    "Only fields of type Collection or List are allowed whe using @PriorityParts.");
        }
    }
}
