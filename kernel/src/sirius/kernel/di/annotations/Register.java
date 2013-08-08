package sirius.kernel.di.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a given class as "self registering".
 * <p/>
 * If a non empty name is given, the part is registered with the given name and
 * for the given classes. Otherwise, the part is directly registered without any
 * id.
 * <p/>
 * Classes wearing this annotations must have a zero args constructor.
 * <p/>
 * Implementations of the OCM are encouraged to create an appropriate
 * {@link com.scireum.ocm.model.ClassLoadAction} for this annotation.
 *
 * @author aha
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Register {

    /**
     * Unique name of the part.
     */
    String name() default "";

    /**
     * Classes for which the part is registered.
     */
    Class<?>[] classes() default {};
}
