package mikera.vectorz.jocl;

import org.junit.Test;

import mikera.vectorz.AVector;

public class TestJoclVector  {

	public void doGenericTests(AVector m) {
		new mikera.vectorz.TestVectors().doGenericTests(m);
	}
	
	@Test public void g_JoclVector3() {
		JoclSubVector m=JoclSubVector.newVector(3);	
		doGenericTests(m);
	}
	
	@Test public void g_JoclVectorSubVector() {
		AVector m=JoclSubVector.newVector(10).subVector(3, 3);	
		doGenericTests(m);
	}
	
	@Test public void g_DeviceVector3() {
		JoclVector m=JoclVector.createLength(3);
		
		doGenericTests(m);
	}

}
