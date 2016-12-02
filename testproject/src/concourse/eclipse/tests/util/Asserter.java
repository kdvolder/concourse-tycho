package concourse.eclipse.tests.util;

/**
 * An {@link Asserter} provides a single method which is expected to check some
 * stuff during a test (i.e. typically execute a bunch of asserts).
 *
 * @author Kris De Volder
 */
@FunctionalInterface
public interface Asserter {
	/**
	 * Called on to check some conditions during a test. Should return normally
	 * to indicate 'success' and throw an exception of any type to indicate
	 * 'failure'.
	 */
	void execute() throws Exception;
}