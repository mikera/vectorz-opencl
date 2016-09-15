package mikera.vectorz.jocl;

import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clSetKernelArg;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_mem;

import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.Vector0;

/**
 * Main dense, fully packed OpenCL vector class.
 * 
 * This is the fastest class for vectorz-opencl vector operations.
 * 
 * This class wraps OpenCL device memory as Vectorz vector.
 * Other vectorz-opencl classes use this for underlying storage 
 * 
 * Automatically frees memory object on finalise.
 * 
 * @author Mike
 *
 */
public class JoclVector extends ADenseJoclVector {
	private static final long serialVersionUID = -5687987534975036854L;

	private final cl_mem mem;
	
	private JoclVector(cl_mem mem,int length) {
		super(length);
		this.mem=mem;
	}

	/**
	 * Creates a device vector of the given length 
	 * IMPRTANT NOTE: memory remains uninitialised
	 * @param length
	 */
	private JoclVector(int length) {
		super(length);
		mem=clCreateBuffer(JoclContext.getInstance().context,CL_MEM_READ_WRITE,length*Sizeof.cl_double, null, null);
	}
	
	private JoclVector(double[] data, int offset, int length) {
		this(length);
		setElements(data,offset);
	}
	
	/**
	 * Creates a new uninitialised JoclVector
	 * Initial values are undefined.
	 * @param n
	 * @return
	 */
	public static JoclVector createUninitialised(int n) {
		return new JoclVector(n);
	}
	
	@Override
	public JoclVector getData() {
		return this;
	}

	@Override
	public int getDataOffset() {
		return 0;
	}
	
	public static JoclVector createLength(int length) {
		JoclVector v= new JoclVector(length);
		v.fill(0.0);
		return v;
	}
	
	public static JoclVector create(AVector src) {
		if (src instanceof JoclVector) return create((JoclVector)src);
		int length=src.length();
		double[] srcArray=src.asDoubleArray();
		if (srcArray==null) srcArray=src.toDoubleArray();
		return new JoclVector(srcArray,0,length);
	}
	
	public static JoclVector create(double[] data, int offset, int length) {
		return new JoclVector(data,offset,length);
	}
	
	public static JoclVector create(JoclVector src) {
		return create(src,0,src.length());
	}

	public static JoclVector create(JoclVector src, int offset, int length) {
		src.checkRange(offset, length);
		JoclVector v=new JoclVector(length);
		CL.clEnqueueCopyBuffer(JoclContext.commandQueue(),src.mem,v.mem,offset*Sizeof.cl_double,0,length*Sizeof.cl_double,0,null,null);
		return v;
	}
	
	@Override
	public void setElements(double[] source, int offset) {
		setElements(0,source,offset,this.length);
	}
	
	@Override
	public void setElements(int offset, double[] source, int srcOffset,int length) {
		if ((length+srcOffset)>source.length) throw new IllegalArgumentException("Insufficient elements in source: "+source.length);
		Pointer src=Pointer.to(source).withByteOffset(srcOffset*Sizeof.cl_double);
		CL.clEnqueueWriteBuffer(JoclContext.commandQueue(), mem, CL_TRUE, offset*Sizeof.cl_double, length*Sizeof.cl_double, src, 0, null, null);		
	}
	
	void setElements(int offset, ADenseJoclVector src, int srcOffset,int length) {
		CL.clEnqueueCopyBuffer(JoclContext.commandQueue(),src.getData().mem,this.mem,offset*Sizeof.cl_double,(srcOffset+src.getDataOffset())*Sizeof.cl_double,length*Sizeof.cl_double,0,null,null);
	}
	
	@Override
	public void copyTo(int srcOffset,double[] dest, int destOffset, int length) {
		if (length+destOffset>dest.length) throw new IllegalArgumentException("Insufficient elements in dest: "+dest.length);
		Pointer dst=Pointer.to(dest).withByteOffset(destOffset*Sizeof.cl_double);
		CL.clEnqueueReadBuffer(JoclContext.commandQueue(), mem, CL_TRUE, srcOffset*Sizeof.cl_double, length*Sizeof.cl_double, dst, 0, null, null);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		copyTo(0,dest,offset,length);
	}
	
	@Override
	public boolean isView() {
		return false;
	}

	@Override
	public void fill(double value) {
		fillRange(0,length,value);
	}
	
	@Override
	public void fillRange(int offset, int length, double value) {
		double[] pattern=new double[]{value};
		long n=length;
		CL.clEnqueueFillBuffer(JoclContext.commandQueue(), mem, Pointer.to(pattern), Sizeof.cl_double, offset*Sizeof.cl_double, n*Sizeof.cl_double, 0,null,null);
	}

	@Override
	public double unsafeGet(int i) {
		double[] result=new double[1];
		Pointer dst=Pointer.to(result);
		CL.clEnqueueReadBuffer(JoclContext.commandQueue(), mem, CL_TRUE, i*Sizeof.cl_double, Sizeof.cl_double, dst, 0, null, null);
		return result[0];
	}

	@Override
	public void unsafeSet(int i, double value) {
		double[] buff=new double[1];
		buff[0]=value;
		Pointer src=Pointer.to(buff);
		CL.clEnqueueWriteBuffer(JoclContext.commandQueue(), mem, CL_TRUE, i*Sizeof.cl_double, Sizeof.cl_double, src, 0, null, null);
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		return unsafeGet(i);
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		unsafeSet(i,value);
	}
	
	@Override
	public void add(AVector a) {
		checkSameLength(a);
		if (a instanceof ADenseJoclVector) {
			add(0,(ADenseJoclVector) a,0,length);
		} else {
			add(0,JoclVector.create(a),0,length);
		}	
	}
	
	@Override
	public void add(int offset, AVector a, int srcOffset, int length) {
		checkRange(offset,length);
		if (a instanceof ADenseJoclVector) {
			add(0,(ADenseJoclVector) a,0,length);
		} else {
			add(0,JoclVector.create(a),0,length);
		}	
	}
	
	public void add(int offset, ADenseJoclVector src,int srcOffset, int length) {
		src.checkRange(srcOffset,length);
		KernelFunction kernel=Kernels.getKernel("add");
		applyKernel(kernel,offset,src,srcOffset,length);
	}
	
	@Override
	public void divide(AVector a) {
		checkSameLength(a);
		if (a instanceof ADenseJoclVector) {
			divide(0,(ADenseJoclVector) a,0,length);
		} else {
			divide(0,JoclVector.create(a),0,length);
		}	
	}
	
	public void divide(int offset, ADenseJoclVector src,int srcOffset, int length) {
		src.checkRange(srcOffset,length);
		KernelFunction kernel=Kernels.getKernel("div");
		applyKernel(kernel,offset,src,srcOffset,length);
	}
	
	@Override
	public void sub(AVector a) {
		checkSameLength(a);
		if (a instanceof ADenseJoclVector) {
			sub(0,(ADenseJoclVector) a,0,length);
		} else {
			sub(0,JoclVector.create(a),0,length);
		}	
	}
	
	public void sub(int offset, ADenseJoclVector src,int srcOffset, int length) {
		src.checkRange(srcOffset,length);
		KernelFunction kernel=Kernels.getKernel("sub");
		applyKernel(kernel,offset,src,srcOffset,length);
	}
	
	@Override
	public void multiply(AVector a) {
		checkSameLength(a);
		if (a instanceof ADenseJoclVector) {
			multiply(0,(ADenseJoclVector) a,0,length);
		} else {
			multiply(0,JoclVector.create(a),0,length);
		}	
	}
	
	public void multiply(int offset, ADenseJoclVector src,int srcOffset, int length) {
		src.checkRange(srcOffset,length);
		KernelFunction kernel=Kernels.getKernel("mul");
		applyKernel(kernel,offset,src,srcOffset,length);
	}
	
	@Override
	public void scaleAdd(double factor, double constant) {
		scaleAdd(0,length,factor,constant);
	}
	
	@Override
	public void scaleAdd(double factor, AVector v) {
		multiply(factor);
		add(v);
	}
	
	@Override
	public void multiply(double factor) {
		scaleAdd(0,length,factor,0.0);
	}
	
	@Override
	public void negate() {
		scaleAdd(0,length,-1.0,0.0);
	}
	
	@Override
	public void add(double value) {
		scaleAdd(0,length,1.0,value);
	}
	
	@Override
	public void sub(double value) {
		scaleAdd(0,length,1.0,-value);
	}
	
	void scaleAdd(int offset,int length, double factor, double constant) {
		checkRange(offset,length);
		KernelFunction kernel=Kernels.getKernel("scaleAdd_scalar");
		applyKernel(kernel,offset,length,factor,constant);
	}
	
	@Override
	public void addAt(int i, double v) {
		KernelFunction kernel=Kernels.getKernel("addAt");
		applyKernel(kernel,i,v);
	}
	
	private void applyKernel(KernelFunction kernel,int offset, ADenseJoclVector src,int srcOffset, int length) {
		clSetKernelArg(kernel.getKernel(), 0, Sizeof.cl_mem, pointer()); // target
		clSetKernelArg(kernel.getKernel(), 1, Sizeof.cl_mem, src.getData().pointer()); // source
		clSetKernelArg(kernel.getKernel(), 2, Sizeof.cl_int, Pointer.to(new int[]{offset})); // source
		clSetKernelArg(kernel.getKernel(), 3, Sizeof.cl_int, Pointer.to(new int[]{srcOffset+src.getDataOffset()})); // source
		
		long global_work_size[] = new long[]{length};
        
		clEnqueueNDRangeKernel(JoclContext.commandQueue(), kernel.getKernel(), 1, null,
				global_work_size, null, 0, null, null);		
	}
	
	private void applyKernel(KernelFunction kernel,int offset, int length, double a, double b) {
		clSetKernelArg(kernel.getKernel(), 0, Sizeof.cl_mem, pointer()); // target
		clSetKernelArg(kernel.getKernel(), 1, Sizeof.cl_int, Pointer.to(new int[]{offset})); // offset
		clSetKernelArg(kernel.getKernel(), 2, Sizeof.cl_double, Pointer.to(new double[]{a})); 
		clSetKernelArg(kernel.getKernel(), 3, Sizeof.cl_double, Pointer.to(new double[]{b}));
		
		long global_work_size[] = new long[]{length};
        
		clEnqueueNDRangeKernel(JoclContext.commandQueue(), kernel.getKernel(), 1, null,
				global_work_size, null, 0, null, null);		
	}
	
	private static final long[] SINGLE_ELEMENT_WORK_SIZE = new long[]{1};
	
	private void applyKernel(KernelFunction kernel,int offset, double a) {
		clSetKernelArg(kernel.getKernel(), 0, Sizeof.cl_mem, pointer()); // target
		clSetKernelArg(kernel.getKernel(), 1, Sizeof.cl_int, Pointer.to(new int[]{offset})); // offset
		clSetKernelArg(kernel.getKernel(), 2, Sizeof.cl_double, Pointer.to(new double[]{a})); 
		
		clEnqueueNDRangeKernel(JoclContext.commandQueue(), kernel.getKernel(), 1, null,
				SINGLE_ELEMENT_WORK_SIZE, null, 0, null, null);		
	}
	
	@Override
	public void applyOp(KernelOp op, int start, int length) {
		checkRange(start,length);
		KernelFunction kernel=op.getKernel();
		clSetKernelArg(kernel.getKernel(), 0, Sizeof.cl_mem, pointer()); // target
		clSetKernelArg(kernel.getKernel(), 1, Sizeof.cl_int, Pointer.to(new int[]{start})); // offset
		long global_work_size[] = new long[]{length};
		clEnqueueNDRangeKernel(JoclContext.commandQueue(), kernel.getKernel(), 1, null,
				global_work_size, null, 0, null, null);	
	}


	public Pointer pointer() {
		return Pointer.to(mem);
	}
	
	@Override
	public void scaleAdd(double factor, AVector b, double bfactor, double constant) {
		if (b instanceof ADenseJoclVector) {
			scaleAdd(0,factor,(ADenseJoclVector)b,0,bfactor,constant,length);
		} else {
			scaleAdd(0,factor,JoclVector.create(b),0,bfactor,constant,length);
		}	
	}
	
	double scaleAdd(int thisOffset,double factor, ADenseJoclVector v, int vOffset, double vFactor, double constant, int length) {
		KernelFunction kernel=Kernels.getKernel("scaleAdd_vector");
		double[] res=new double[1];
		clSetKernelArg(kernel.getKernel(), 0, Sizeof.cl_mem, pointer()); // this
		clSetKernelArg(kernel.getKernel(), 1, Sizeof.cl_int, Pointer.to(new int[]{thisOffset})); // this offset
		clSetKernelArg(kernel.getKernel(), 2, Sizeof.cl_double, Pointer.to(new double[]{factor})); // this factor
		clSetKernelArg(kernel.getKernel(), 3, Sizeof.cl_mem, v.getData().pointer()); // source
		clSetKernelArg(kernel.getKernel(), 4, Sizeof.cl_int, Pointer.to(new int[]{vOffset+v.getDataOffset()})); // voffset
		clSetKernelArg(kernel.getKernel(), 5, Sizeof.cl_double, Pointer.to(new double[]{vFactor})); // this factor
		clSetKernelArg(kernel.getKernel(), 6, Sizeof.cl_double, Pointer.to(new double[]{constant})); // this factor
		long global_work_size[] = new long[]{length};
		clEnqueueNDRangeKernel(JoclContext.commandQueue(), kernel.getKernel(), 1, null,
				global_work_size, null, 0, null, null);	
		
		return res[0];
	}

	@Override
	public double dotProduct(AVector v) {
		if (v instanceof ADenseJoclVector) {
			return dotProduct((ADenseJoclVector) v);
		} else {
			checkSameLength(v);
			return v.dotProduct(getElements(),0);
		}
	}
	
	public double dotProduct(ADenseJoclVector v) {
		int n=checkSameLength(v);
		return dotProduct(0,v,0,n);
	}
		
	private static final long[] SINGLE_ELEMENT_WORK_SIZE2 = new long[]{1,1};
	double dotProduct(int thisOffset,ADenseJoclVector v, int vOffset, int length) {
		KernelFunction kernel=Kernels.getKernel("dotProduct");
		double[] res=new double[1];
		clSetKernelArg(kernel.getKernel(), 0, Sizeof.cl_double, Pointer.to(res)); // result
		clSetKernelArg(kernel.getKernel(), 1, Sizeof.cl_mem, pointer()); // this
		clSetKernelArg(kernel.getKernel(), 2, Sizeof.cl_mem, v.getData().pointer()); // target
		clSetKernelArg(kernel.getKernel(), 3, Sizeof.cl_int, Pointer.to(new int[]{thisOffset})); // this offset
		clSetKernelArg(kernel.getKernel(), 4, Sizeof.cl_int, Pointer.to(new int[]{vOffset+v.getDataOffset()})); // voffset
		clSetKernelArg(kernel.getKernel(), 5, Sizeof.cl_int, Pointer.to(new int[]{length})); // length
		clSetKernelArg(kernel.getKernel(), 6, Sizeof.cl_int, Pointer.to(new int[]{1})); // stride
		clSetKernelArg(kernel.getKernel(), 7, Sizeof.cl_int, Pointer.to(new int[]{length})); // row step
		clEnqueueNDRangeKernel(JoclContext.commandQueue(), kernel.getKernel(), 1, null,
				SINGLE_ELEMENT_WORK_SIZE2, null, 0, null, null);	
		
		return res[0];
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		return toVector().dotProduct(data,offset);
	}
	
	@Override
	public AVector subVector(int offset, int length) {
		checkRange(offset,length);
		if (length==0) return Vector0.INSTANCE;
		if (length==this.length) return this;
		return JoclSubVector.wrap(this, offset, length);
	}
	
	@Override
	public AScalar slice(int position) {
		checkIndex(position);
		return JoclScalar.wrap(this,position);
	}

	@Override
	public JoclVector exactClone() {
		return create(this);
	}
	
	@Override
	public JoclVector clone() {
		return create(this);
	}

	@Override
	public void finalize() throws Throwable {
		clReleaseMemObject(mem);
		super.finalize();
	}

}
