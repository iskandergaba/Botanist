package test;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
public class RunAllTests extends TestCase
{
	/**
	 * Bundle test cases into suite
	 * @return Returns the test suite
	 */
	public static Test suite()
	{
		try
		{
			Class[] testClasses = {};
			return new TestSuite(testClasses);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}