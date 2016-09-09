package mikera.vectorz.jocl;

import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;

public class JoclScalar extends AScalar implements IJoclArray {
	private static final long serialVersionUID = 6370756521551217245L;

	private final JoclVector data;
	private int offset;
	
	private JoclScalar(JoclVector src, int offset) {
		data=src;
		this.offset=offset;
	}
	
	public JoclScalar() {
		data=JoclVector.createLength(1);
	}
	
	public static JoclScalar create(double v) {
		return new JoclScalar(JoclVector.create(new double[]{v}, 0, 1),0);
	}
	
	public static JoclScalar wrap(JoclVector joclVector, int position) {
		return new JoclScalar(joclVector,position);
	}
	
	@Override
	public boolean isView() {
		return true;
	}

	@Override
	public double get() {
		return data.unsafeGet(offset);
	}

	@Override
	public void set(double value) {
		data.unsafeSet(offset,value);
	}

	@Override
	public AScalar exactClone() {
		return new JoclScalar(JoclVector.create(data, offset, 1),0);
	}

	@Override
	public AVector asVector() {
		if ((offset==0)&&(data.length()==1)) return data;
		return data.subVector(offset, 1);
	}

}
