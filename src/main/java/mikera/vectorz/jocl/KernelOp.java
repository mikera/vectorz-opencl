package mikera.vectorz.jocl;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;

/**
 * Class for Kernel-based operators
 * @author Mike
 *
 */
public class KernelOp extends Op {
	private final Program program;
	private final String function;
	private final Op javaOp;
	
	protected KernelOp(Program program, String function, Op javaOp) {
		this.program=program;
		this.function=function;
		this.javaOp=javaOp;
	}
	
	public static KernelOp create(Program program, String name, Op javaOp) {
		return new KernelOp(program,name,javaOp);
	}
	
	public Op getJavaOp() {
		return javaOp;
	}
	
	@Override
	public double apply(double x) {
		if (javaOp!=null) return javaOp.apply(x);
		throw new IllegalArgumentException("Can't apply KernelOp to non-Jocl value");
	}
	
	@Override
	public void applyTo(AVector v) {
		applyTo(v,0,v.length());
	}

	@Override
	public void applyTo(AVector v, int start, int length) {
		if (v instanceof ADenseJoclVector) {
			((ADenseJoclVector)v).applyOp(this,start,length);
		} else if (javaOp!=null){
			javaOp.applyTo(v,start,length);
		} else {
			throw new IllegalArgumentException("Can't apply KernelOp to non-Jocl vector");			
		}
	}

	/**
	 * Gets a new kernel instance for this operator
	 * @return
	 */
	public KernelFunction getKernel() {
		return new KernelFunction(program,function);
	}
	
	
	@Override
	public String toString() {
		return "KernelOp["+function+"]";
	}


}
