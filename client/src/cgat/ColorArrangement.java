package cgat;

import java.awt.*;

public class ColorArrangement {
	protected double rangeMin = 0;		// 数値範囲
	protected double rangeMax = 100;		// 数値範囲
	protected int levelMax = 1;				// 分割数

	protected Color colorMin = Color.BLACK;		// 色範囲
	protected Color colorMax = Color.WHITE;		// 色値範囲
	protected int diffRed   = 0;
	protected int diffGreen = 0;
	protected int diffBlue  = 0;

	
	protected ColorArrangement() {
	}
	
	public ColorArrangement(double vMin, double vMax, int lev) {
		setRange(vMin, vMax, lev, Color.BLACK, Color.WHITE);
		
	}

	public ColorArrangement(double vMin, double vMax, int lev, Color cMin, Color cMax) {
		setRange(vMin, vMax, lev, cMin, cMax);
		
	}

	public void setRange(double vMin, double vMax, int lev, Color cMin, Color cMax) {
		setRange(vMin, vMax);
		setLevelMax(lev);
		setColorRange(cMin, cMax);
		
		//
		setupColorArrangement();
	}
	
	protected void setupColorArrangement() {
		int redMin = colorMin.getRed();
		int greenMin = colorMin.getGreen();
		int blueMin = colorMin.getBlue();
		int redMax = colorMax.getRed();
		int greenMax = colorMax.getGreen();
		int blueMax = colorMax.getBlue();
	
	
		diffRed   = (redMax - redMin) / levelMax;
		diffGreen = (greenMax - greenMin) / levelMax;
		diffBlue  = (blueMax - blueMin) / levelMax;
	}

	
	protected void setRange(double min, double max) {
		rangeMin = min;
		rangeMax = max;
	}

	protected void setColorRange(Color min, Color max) {
		colorMin = min;
		colorMax = max;
	}

	public double getRangeMin() {
		return rangeMin;
	}

	public double getRangeMax() {
		return rangeMax;
	}

	protected void setLevelMax(int lev) {
		levelMax = lev;
	}

	public double getLevelMax() {
		return levelMax;
	}

	public int getLevel(double val) {
		double dif = (rangeMax - rangeMin) / levelMax;

		int lev = (int)((val - rangeMin) / dif);
		if (lev < 0) {
			lev = 0;
		}
		else if (levelMax < lev) {
			lev = levelMax;
		}
		
		return lev;
	}

	public Color getColor(double val) {
		Color c;
		int lev = getLevel(val);
		
		//
		int r = colorMin.getRed() + diffRed * lev;
		int g = colorMin.getGreen() + diffGreen * lev;
		int b = colorMin.getBlue() + diffBlue * lev;

		c = new Color(r, g, b);
		
		return c;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

	}

}

