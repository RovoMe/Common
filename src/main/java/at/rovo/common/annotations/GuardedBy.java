package at.rovo.common.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.locks.Lock;

/**
 * Documents that a field or method should be accessed only with a specific lock held. The lock argument identifies the
 * lock that should be held when accessing the annotated field or method. Possible values are:
 * <table>
 *     <tr>
 *         <td><code>this</code></td>
 *         <td>Intrinsic lock on the current object referred by this</td>
 *     </tr>
 *     <tr>
 *         <td><code>fieldName</code></td>
 *         <td>The lock associated with the object referenced by the named field, either an intrinsic lock, for fields
 *             that do not refer to a {@link Lock}, or an explicit lock, for fields that refer to a {@link Lock}</td>
 *     </tr>
 *     <tr>
 *         <td><code>ClassName.fieldName</code></td>
 *         <td>Like <code>fieldName</code> but referencing a lock object held in a static field of another class</td>
 *     </tr>
 *     <tr>
 *         <td><code>methodName()</code></td>
 *         <td>The lock object that is returned by calling the named method</td>
 *     </tr>
 *     <tr>
 *         <td><code>ClassName.class</code></td>
 *         <td>The class literal object for the named class</td>
 *     </tr>
 * </table>
 * <p>
 * This annotation should to be applied on either on the field or on the method which requires the lock to guard it. The
 * annotation is only visible within the source and it is based on the suggested annotation by Goetz et all in their
 * book <em>Java concurrency in practice</em> to document the concurrency behavior of code.
 *
 * @author Roman Vottner
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface GuardedBy
{
    /**
     * The name of the lock used guarding the annotated field.
     *
     * @return The name of the lock object which guards this field
     */
    String value();
}
