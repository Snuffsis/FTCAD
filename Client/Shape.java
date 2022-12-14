package Client;

import java.io.Serializable;

final class Shape implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String type;

	private Shape(String type)
	{
		this.type = type;
	}

	public String toString()
	{
		return this.type;
	}

	public static Shape OVAL = new Shape("OVAL");
	public static Shape RECTANGLE = new Shape("RECTANGLE");
	public static Shape LINE = new Shape("LINE");
	public static Shape FILLED_RECTANGLE = new Shape("FILLED_RECTANGLE");
	public static Shape FILLED_OVAL = new Shape("FILLED_OVAL");
}