package mikera.vectorz.jocl;

import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clReleaseKernel;

import org.jocl.cl_kernel;

public class Kernel {
	public final cl_kernel kernel;
	
	public Kernel(Program program, String name) {
		this.kernel=clCreateKernel(program.program, name, null);
	}

	@Override
	public void finalize() throws Throwable {
		clReleaseKernel(kernel);
		super.finalize();
	}

}
