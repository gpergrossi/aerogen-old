package noise;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Tester {

	public static void main(String[] args) {
		
		JFrame frame = new JFrame("Noise Tester");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(500, 500));
		frame.setContentPane(panel);
		frame.pack();
		
		Noise2D noise = new FractalNoise2D(8, 1.0/128.0);
		noise.setOutputMap(new RangeMap(0.0, 1.0));
		
		FractalNoise2D noise2 = new FractalNoise2D(1, 1.0/128.0);
		noise2.setOutputMap(NoiseMap.SharpCurve);
		
		BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
		int[] colors = new int[500*500];
		int index = 0;
		for(int y = 0; y < 500; y++) {
			for(int x = 0; x < 500; x++) {
//				double value = noise.getValue(x, y)-noise2.getValue(x, y);
				double value = noise2.getValue(x, y);
//				double p = (y/500.0);
//				value = (value+p*5.0)/6.0;
				if(value < 0.0) value = 0.0;
				if(value > 1.0) value = 1.0;
				if(value < 0.7) value = 0.0;
				if(value >= 0.7) value = 0.7;
				
				Color color = new Color(Color.HSBtoRGB((float)value, 1.0f, 1.0f));
				int blue = color.getBlue();
				int green = color.getGreen();
				int red = color.getRed();
				
				colors[index] = ((0xFF << 24) | (red << 16) | (green << 8) | blue);
				index++;
			}
		}
		image.setRGB(0, 0, 500, 500, colors, 0, 500);
		
		panel.setLayout(null);
		JLabel label = new JLabel(new ImageIcon(image));
		panel.add(label);
		label.setBounds(0, 0, 500, 500);

		frame.setVisible(true);
		
	}

}
