package mikera.vectorz.jocl;

import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clReleaseKernel;

import org.jocl.cl_kernel;

public class Kernel {
	private final cl_kernel kernel;
	
	public Kernel(Program program, String name) {
		this.kernel=clCreateKernel(program.program, name, null);
	}

	@Override
	public void finalize() throws Throwable {
		clReleaseKernel(getKernel());
		super.finalize();
	}

	public cl_kernel getKernel() {
		return kernel;
	}

}
