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
public class InnerProductBenchmark extends SimpleBenchmark {
	public double result;
	
	int DIM_SIZE=256;

	public void timeMmulJocl(int runs) {
		JoclMatrix m=JoclMatrix.newMatrix(DIM_SIZE,DIM_SIZE);
		JoclMatrix m2=JoclMatrix.newMatrix(DIM_SIZE,DIM_SIZE);
		JoclMatrix r=JoclMatrix.newMatrix(DIM_SIZE,DIM_SIZE);
		
		for (int i=0; i<runs; i++) {
			r.setInnerProduct(m, m2);
		}
		result=0.0;
	}
	
	public void timeMmulJava(int runs) {
		Matrix m=Matrix.create(DIM_SIZE,DIM_SIZE);
		Matrix m2=Matrix.create(DIM_SIZE,DIM_SIZE);
		Matrix r=Matrix.create(DIM_SIZE,DIM_SIZE);
		
		for (int i=0; i<runs; i++) {
			r.setInnerProduct(m, m2);
		}
		result=0.0;
	}

	public static void main(String[] args) {
		new InnerProductBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
