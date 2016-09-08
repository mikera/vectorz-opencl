package mikera.vectorz.jocl;

import java.util.Hashtable;

import mikera.vectorz.Op;
import mikera.vectorz.Ops;

/**
 * Static class for Kernel based operators
 * @author Mike
 *
 */
public class KernelOps {

	private static final Program program=new Program(JoclContext.getInstance(), JoclUtils.loadString("/mikera/vectorz/jocl/opkernels.cl"));
	private static final Hashtable<Class<? extends Op>,KernelOp> classMap=new Hashtable<>();
	private static final Hashtable<Op,KernelOp> opMap=new Hashtable<>();
	
    public static KernelOp ABS = registerKernelOp("op_abs",Ops.ABS,true);
    public static KernelOp LOG = registerKernelOp("op_log",Ops.LOG,true);
    public static KernelOp EXP = registerKernelOp("op_exp",Ops.EXP,true);
    public static KernelOp SIN = registerKernelOp("op_sin",Ops.SIN,true);
    public static KernelOp COS = registerKernelOp("op_cos",Ops.COS,true);
    public static KernelOp SQRT = registerKernelOp("op_sqrt",Ops.SQRT,true);
	
	public static KernelOp findSubstitute(Op op) {
		KernelOp kop=opMap.get(op);
		if (kop!=null) return kop;
		kop=classMap.get(op.getClass());
		return kop;
	}
	
	private static KernelOp registerKernelOp(String name, Op javaOp) {
		KernelOp op=KernelOp.create(program,name,javaOp);
		opMap.put(javaOp, op);
		return op;
	}
	
	private static KernelOp registerKernelOp(String name, Op javaOp, boolean registerClass) {
		KernelOp op=registerKernelOp(name,javaOp);
		if (registerClass) classMap.put(javaOp.getClass(), op);
		return op;
	}

}
