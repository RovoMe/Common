package at.rovo.common.testsuites;

import at.rovo.common.IntegrationTest;
import com.googlecode.junittoolbox.IncludeCategories;
import com.googlecode.junittoolbox.SuiteClasses;
import org.junit.runner.RunWith;

/**
 * Custom JUnit 4 suite which will automatically detect all Java class files whose file name ends with <em>Test</em>
 * located in subdirectories. On a match the class file is checked for the availability of a
 * <code>@Category(IntegrationTest.class)</code> annotation and if available the test class is added to the set of test
 * classes which need to be executed.
 */
@RunWith(ParallelSuite.class)
@SuiteClasses("**/*Test.class")
@IncludeCategories(IntegrationTest.class)
public class ParallelIntegrationTestSuite
{
}
