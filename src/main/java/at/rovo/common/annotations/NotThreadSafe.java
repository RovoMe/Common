package at.rovo.common.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the annotated class is not thread safe. Special care needs to be taken if this class is shared among
 * multiple threads.
 * <p>
 * This annotation needs to be applied on the class and is only visible within the source and it is based on the
 * suggested annotation by Goetz et all in their book <em>Java concurrency in practice</em> to document the concurrency
 * behavior of code.
 *
 * @author Roman Vottner
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface NotThreadSafe
{
}
