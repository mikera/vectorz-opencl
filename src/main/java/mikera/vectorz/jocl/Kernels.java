package mikera.vectorz.jocl;

public class Kernels {
	private static final Program program;
	
	public static Kernel getKernel(String kernelName) {
		return new Kernel(program,kernelName);
	}

	static {	
        program=new Program(JoclContext.getInstance(), JoclUtils.loadString("/mikera/vectorz/jocl/kernels.cl"));
	}
	
	public static void main(String[] args) {
		System.out.println(program);
	}
}
