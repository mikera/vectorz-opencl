package mikera.vectorz.jocl;

import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jocl.Pointer;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Utility function class for vectorz-opencl
 * @author Mike
 *
 */
public class JoclUtils {
	public static String loadString(String filePath) {
		try {
			Path path=Paths.get(System.class.getResource(filePath).toURI());
			return new String(Files.readAllBytes(path));
		} catch (Throwable e) {
			throw new Error(e);
		} 
	}

	public static Pointer intPointer(int a, int b) {
//		IntBuffer buffer=ByteBuffer.allocateDirect(8).asIntBuffer();
//		buffer.put(0, a);
//		buffer.put(1, b);
		IntBuffer buffer=IntBuffer.wrap(new int[]{a,b});
		return Pointer.to(buffer);
	}

	public static Pointer intPointer(int a) {
		IntBuffer buffer=IntBuffer.wrap(new int[]{a});
		return Pointer.to(buffer);
	}

	public static ADenseJoclVector coerce(AVector a) {
		if (a instanceof ADenseJoclVector) return (ADenseJoclVector) a;
		return JoclVector.create(a);
	}

	public static JoclMatrix coerce(AMatrix a) {
		if (a instanceof JoclMatrix) return (JoclMatrix) a;
		return JoclMatrix.create(a);
	}

}
