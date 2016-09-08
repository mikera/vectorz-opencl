package mikera.vectorz.jocl;

import java.util.Arrays;

import org.jocl.CL;
import org.jocl.Sizeof;

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
public class CLOpsBenchmark extends SimpleBenchmark {
	double result;
	
	int DIM_SIZE=256;

	public void timeGetKernel(int runs) {
		KernelFunction k=Kernels.getKernel("add");;
		for (int i=0; i<runs; i++) {
			k=Kernels.getKernel("add");
		}
		result=k.hashCode();
	}
	
	public void timeSetKernelArg(int runs) {
		KernelFunction k=Kernels.getKernel("add");
		JoclVector v=JoclVector.createLength(10);
		for (int i=0; i<runs; i++) {
			CL.clSetKernelArg(k.getKernel(), 0, Sizeof.cl_mem, v.pointer());
		}
		result=k.hashCode();
	}
	
	public static void main(String[] args) {
		new CLOpsBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
