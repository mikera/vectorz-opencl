package mikera.vectorz.jocl;

import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clReleaseProgram;

import org.jocl.cl_program;

public class Program {
	public final cl_program program;
	
	public Program(JoclContext context, String source) {
		int[] ret=new int[1];
		this.program=clCreateProgramWithSource(context.context,
	            1, new String[]{ source }, null, ret);
		if (ret[0]!=0) throw new Error("Error code creating Program:" +ret[0]);
		int r1 =clBuildProgram(program, 0, null, null, null, null);
		if (r1!=0) throw new Error("Error code building Program:" +r1);
	}

	@Override
	public void finalize() throws Throwable {
		clReleaseProgram(program);
		super.finalize();
	}

}
