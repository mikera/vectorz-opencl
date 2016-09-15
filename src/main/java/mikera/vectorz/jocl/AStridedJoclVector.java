package mikera.vectorz.jocl;

import mikera.arrayz.impl.IDense;
import mikera.vectorz.impl.ASizedVector;

public abstract class AStridedJoclVector extends ASizedVector implements IDense, IJoclArray {
	private static final long serialVersionUID = 146088993789740610L;

	public abstract JoclVector getData();
	
	public abstract int getDataOffset();

	public abstract int getStride();
	
	public AStridedJoclVector(int length) {
		super(length);
	}

}
