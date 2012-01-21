package cmpe275.hems.junitTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class HemsTest {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for cmpe275.hems.junitTest");
		//$JUnit-BEGIN$

		//$JUnit-END$
		return suite;
	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

}
