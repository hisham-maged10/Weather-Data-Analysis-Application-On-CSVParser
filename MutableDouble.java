/**
 * @author ${hisham_maged10}
 *https://github.com/hisham-maged10
 * ${DesktopApps}
 */
public class MutableDouble
{
	private double doubleValue;
	public MutableDouble()
	{
		this(0.0);	
	}
	public MutableDouble(double doubleValue)
	{
		this.doubleValue=doubleValue;
	}
	
	public void setDoubleValue(double doubleValue)
	{
		this.doubleValue=doubleValue;
	}
	public double getDoubleValue()
	{
		return this.doubleValue;
	}
}