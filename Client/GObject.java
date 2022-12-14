package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

public class GObject implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Shape s;
	private Color c;
	private int x;
	private int y;
	private int width;
	private int height;

	public GObject(Shape s, Color c, int x, int y, int width, int height)
	{
		this.s = s;
		this.c = c;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setShape(Shape s)
	{
		this.s = s;
	}

	public void setColor(Color c)
	{
		this.c = c;
	}

	public void setCoordinates(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public void setDimensions(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public Shape getShape()
	{
		return this.s;
	}

	public Color getColor()
	{
		return this.c;
	}

	public int getX()
	{
		return this.x;
	}

	public int getY()
	{
		return this.y;
	}

	public void draw(Graphics g)
	{
		g.setColor(this.c);
		int drawX = this.x, drawY = this.y, drawWidth = this.width, drawHeight = this.height;

		if (this.width < 0)
		{
			drawX = this.x + this.width;
			drawWidth = -this.width;
		}

		if (this.height < 0)
		{
			drawY = this.y + this.height;
			drawHeight = -this.height;
		}

		if (this.s.toString().compareTo(Shape.OVAL.toString()) == 0)
		{
			g.drawOval(drawX, drawY, drawWidth, drawHeight);
		}
		else if (this.s.toString().compareTo(Shape.RECTANGLE.toString()) == 0)
		{
			g.drawRect(drawX, drawY, drawWidth, drawHeight);
		}
		else if (this.s.toString().compareTo(Shape.LINE.toString()) == 0)
		{
			g.drawLine(this.x, this.y, this.x + this.width, this.y + this.height);
		}
		else if (this.s.toString().compareTo(Shape.FILLED_RECTANGLE.toString()) == 0)
		{
			g.fillRect(drawX, drawY, drawWidth, drawHeight);
		}
		else if (this.s.toString().compareTo(Shape.FILLED_OVAL.toString()) == 0)
		{
			g.fillOval(drawX, drawY, drawWidth, drawHeight);
		}
	}
}
