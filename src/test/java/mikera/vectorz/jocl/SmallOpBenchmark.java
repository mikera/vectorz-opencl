package mikera.vectorz.jocl;

import java.util.Arrays;

import mikera.matrixx.Matrix;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.RangeVector;
import mikera.vectorz.util.DoubleArrays;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

/**
 * Caliper based benchmarks for sublist iteration
 * 
 * See debate at: http://stackoverflow.com/questions/17302130/enhanced-for-loop/17302215
 * 
 * @author Mike
 */
@SuppressWarnings("unused")
public class SmallOpBenchmark extends SimpleBenchmark {
	double result;
	

	public void timeAddAt(int runs) {
		JoclVector v=JoclVector.create(Vector.of(1,2,3,4));
		for (int i=0; i<runs; i++) {
			v.addAt(2,1.0);
		}
		result=v.get(2);
		if (result!=3.0+runs) throw new Error("Wrong result: "+ result);
	}
	
	public void timeAdd(int runs) {
		JoclVector v=JoclVector.create(Vector.of(1,2,3,4));
		JoclVector v2=JoclVector.create(Vector.of(1,1,1,1));
		for (int i=0; i<runs; i++) {
			v.add(v2);
		}
		result=v.get(2);
		if (result!=3.0+runs) throw new Error("Wrong result: "+ result);
	}

	
	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

	public static void main(String[] args) {
		new SmallOpBenchmark().run();
	}
}
