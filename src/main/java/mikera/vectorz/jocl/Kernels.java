package mikera.vectorz.jocl;

public class Kernels {
	private static final Program program;
	
	public static KernelFunction getKernel(String kernelName) {
		return new KernelFunction(program,kernelName);
	}

	static {	
        program=new Program(JoclContext.getInstance(), JoclUtils.loadString("/mikera/vectorz/jocl/kernels.cl"));
	}
	
	public static void main(String[] args) {
		System.out.println(program);
	}
}
