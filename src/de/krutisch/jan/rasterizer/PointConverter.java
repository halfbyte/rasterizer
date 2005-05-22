/*
 * Created on 22.05.2005
 *
 */
package de.krutisch.jan.rasterizer;

/**
 * @author Jan Krutisch
 * This class is a general purpose Point to meter/inch and back converter used to
 * convert user readable values into Point (as used by the iText lib)
 *
 */
public class PointConverter {
	
	public final static int M = 1;
	public final static int CM = 2;
	public final static int MM = 3;
	public final static int INCH = 4;
	public final static int YARD = 5;
	
	private final static float M_IN_POINT = 2834.64567f;
	private final static float INCH_IN_POINT = 72;
	private final static float YARD_IN_POINT = 2592;
	
	/**
	 * Converts from normal length units to PS-Points (72 Pt / Inch)
	 * @param value Value that should be converted to Point
	 * @param type one of PointConverter.M/CM/MM/INCH/YARD (unit of "value")
	 * @return length in Point
	 */
	public static float toPoint(float value, int type) {
		float result = 0f;
		switch(type) {
			case 1:
				result = value * M_IN_POINT;
				break;
			case 2:
				result = value / 100f * M_IN_POINT;
				break;
			case 3:
				result = value / 1000f * M_IN_POINT;
				break;
			case 4:
				result = value * INCH_IN_POINT;
				break;
			case 5:
				result = value * YARD_IN_POINT;
				break;
			default:
				result = value;
					
		}
		
		return result;
	}
	/**
	 * Converts from PS-Points (72pt/inch) to normal length units
	 * @param value Value in Pt that should be converted given Unit
	 * @param type one of PointConverter.M/CM/MM/INCH/YARD (unit to be converted to)
	 * @return length in given Unit
	 */
	public static float fromPoint(float value,int type) {
		float result = 0f;
		switch(type) {
		case 1:
			result = value / M_IN_POINT;
			break;
		case 2:
			result = value / M_IN_POINT * 100f;
			break;
		case 3:
			result = value / M_IN_POINT * 1000f;
			break;
		case 4:
			result = value / INCH_IN_POINT;
			break;
		case 5:
			result = value / YARD_IN_POINT;
			break;
		default:
			result = value;
				
		}
		
		return result;
		
	}
	
}
