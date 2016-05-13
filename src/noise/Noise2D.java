package noise;

public interface Noise2D {

	public double getValue(double x, double y);
	
	public void setOutputMap(NoiseMap map);
	
	public NoiseMap getOutputMap();
	
}
