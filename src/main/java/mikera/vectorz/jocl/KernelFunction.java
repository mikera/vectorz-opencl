package mikera.vectorz.jocl;

import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clReleaseKernel;

import org.jocl.cl_kernel;

/**
 * Encapsulates an OpenCL Kernel function
 * 
 * Manages a thread-local cache of cl_kernel objects for this kernel function.
 * 
 * @author Mike
 *
 */
public class KernelFunction {
	private final Program program;
	private final String name;

	public KernelFunction(Program program, String name) {
		this.program=program;
		this.name=name;
	}
	
	private final ThreadLocal<KernelWrapper> cache=new ThreadLocal<>();
	
	private class KernelWrapper {
		private final cl_kernel kernel;
		
		public KernelWrapper() {
			this.kernel=clCreateKernel(program.program, name, null);
		}

		@Override
		public void finalize() throws Throwable {
			clReleaseKernel(getKernel());
			super.finalize();
		}
	}

	public cl_kernel getKernel() {
		KernelWrapper k=cache.get();
		if (k==null) {
			k=new KernelWrapper();
			cache.set(k);
		}
		
		return k.kernel;
	}

}
