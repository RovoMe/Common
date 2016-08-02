package at.rovo.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.runners.model.RunnerScheduler;

/**
 * JUnit 4 execution scheduler which executes tests classes or test methods in parallel depending on the available
 * number of execution cores
 */
public class ParallelScheduler implements RunnerScheduler
{
    private ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public void schedule(Runnable childStatement)
    {
        threadPool.submit(childStatement);
    }

    @Override
    public void finished()
    {
        try
        {
            threadPool.shutdown();
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Got interrupted", e);
        }
    }
}
