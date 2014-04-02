package examples.heat_v2;

public class LowMemoryArray implements java.io.Serializable {
	int x,y;
	double[] array;

	public LowMemoryArray(int x, int y) {
		this.x = x;
		this.y = y;
		array = new double[x*y];
	}

	public double get(int target_x, int target_y) {
//		System.err.println("getting x: " + target_x + ", y: " + target_y + " at: " + (target_x*x + target_y) );
		return array[target_x*y + target_y];
	}

	public void set(int target_x, int target_y, double value) {
		array[target_x*y + target_y] = value;
	}

	public double[] row(int x) {
		double[] column = new double[y];

		int position = 0;
		for (int i = y*x; i < (x+1)*y; i++) {
			column[position] = array[i];
			position++;
		}
		return column;
	}

	public double[] column(int y) {
		double[] row = new double[x];

		int position = 0;
		for (int i = y; i < x*this.y; i += this.y) {
			row[position] = array[i];
			position++;
		}
		return row;
	}
}
