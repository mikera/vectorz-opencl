package mikera.vectorz.jocl;

import java.util.Iterator;

import mikera.arrayz.impl.IDense;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.impl.IndexedElementVisitor;
import mikera.vectorz.util.DoubleArrays;

/**
 * Abstract base class for dense Jocl vectors
 * @author Mike
 *
 */
public abstract class ADenseJoclVector extends AStridedJoclVector implements IDense, IJoclArray {
	private static final long serialVersionUID = -6022914163576354860L;

	protected ADenseJoclVector(int length) {
		super(length);
	}
	
	@Override
	public final int getStride() {
		return 1;
	}
	
	/**
	 * Coerces this vector to a JoclSubVector. 
	 * 
	 * May return the same object if it ias already a JoclSubVector
	 * @return
	 */
	public JoclSubVector asJoclSubVector() {
		if (this instanceof JoclSubVector) {
			return (JoclSubVector)this;
		} else {
			return JoclSubVector.wrap(getData(), getDataOffset(), length);
		}
	}
	
	@Override
	public Iterator<Double> iterator() {
		// Convert to Java data in order to have an efficient iterator.
		return toVector().iterator();
	}
	
	@Override
	public JoclVector dense() {
		return clone();
	}
	
	@Override
	public JoclVector clone() {
		return JoclVector.create(this);
	}
	
	@Override
	public void set(AVector src) {
		if (src instanceof ADenseJoclVector) {
			set((ADenseJoclVector)src);
		} else {
			checkSameLength(src);
			setElements(src.getElements(),0);
		}
	}
	
	@Override
	public void addMultiple(AVector src, double factor) {
		scaleAdd(1.0,src,factor,0.0);
	}
	
	@Override
	public void addMultiple(int offset, AVector src, int srcOffset, int length, double factor) {
		subVector(offset,length).addMultiple(src.subVector(srcOffset,length), length);
	}
	
	public void set(ADenseJoclVector src) {
		checkSameLength(src);
		getData().setElements(getDataOffset(), src, 0, length);
	}
	
	@Override
	public void applyOp(Op op) {
		if (op instanceof KernelOp) {
			// fast path for KernelOps
			applyOp((KernelOp) op,0,length);
			return;
		}
		
		KernelOp kop=KernelOps.findSubstitute(op);
		if (kop!=null) {
			// use substitute kernel op
			applyOp(kop,0,length);
			return;
		} else {
			// execute in Java array, best we can do...
			double[] xs=getElements();
			if (xs.length!=length) throw new Error("Unexpected length");
			op.applyTo(xs);
			setElements(xs);
		}	
	}
	
	protected abstract void applyOp(KernelOp op, int start, int length);
	
	@Override
	public boolean equals(AVector v) {
		if (v==this) return true;
		if (v.length()!=length) return false;
		return v.equalsArray(getElements(),0);
	}
	
	@Override
	public double visitNonZero(IndexedElementVisitor elementVisitor) {
		return toVector().visitNonZero(elementVisitor);
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return DoubleArrays.equals(data, offset, getElements(), 0, length);
	}
}
