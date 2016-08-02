package at.rovo.common;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * JUnit 4 customized runner which enables parallel execution of test classes or methods
 */
public class ParallelRunner extends BlockJUnit4ClassRunner
{
    /**
     * Creates a ParallelRunner to run {@code klass}
     *
     * @param klass
     *         The JUnit suite or test class to execute
     *
     * @throws InitializationError
     *         if the test class is malformed.
     */
    public ParallelRunner(Class<?> klass) throws InitializationError
    {
        super(klass);
        setScheduler(new ParallelScheduler());
    }
}
