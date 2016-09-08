package mikera.vectorz.jocl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mikera.matrixx.Matrix22;

public class TestJoclMatrix  {

	@Test public void testGetSet() {
		JoclMatrix m=JoclMatrix.create(new Matrix22(1,2,3,4));
		
		assertEquals(4.0,m.get(1,1),0.0);
		
		m.set(1,0,2.0);
		assertEquals(2.0,m.get(1,0),0.0);
		
		m.add(new Matrix22(1,2,3,3));
		assertEquals(7.0,m.get(1,1),0.0);
	}
	
	public void doGenericTests(JoclMatrix m) {
		new mikera.matrixx.TestMatrices().doGenericTests(m);
	}
	
	@Test public void g_JoclMatrix() {
		JoclMatrix m=JoclMatrix.newMatrix(2,2);
		
		doGenericTests(m);
	}

}
