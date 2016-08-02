package at.rovo.common.testsuites;

import at.rovo.common.ParallelScheduler;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

/**
 * Customized JUnit 4 suite which runs multiple test classes in parallel.
 */
public class ParallelSuite extends WildcardPatternSuite
{
    /**
     * Called reflectively on classes annotated with <code>@RunWith(ParallelSuite.class)</code>
     *
     * @param klass
     *         The root class
     * @param builder
     *         Builds runners for classes in the suite
     *
     * @throws InitializationError
     */
    public ParallelSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError
    {
        super(klass, builder);
        setScheduler(new ParallelScheduler());
    }
}
