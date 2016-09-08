package mikera.vectorz.jocl;

import org.junit.Test;

import mikera.vectorz.AScalar;

public class TestJoclScalar  {

	public void doGenericTests(AScalar m) {
		new mikera.arrayz.TestArrays().testArray(m);
	}
	
	@Test public void g_JoclScalar() {
		JoclScalar s=JoclScalar.create(2);
		
		doGenericTests(s);
	}

}
