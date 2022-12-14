package Client;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.swing.JButton;
import javax.swing.JFrame;

public class GUI extends JFrame implements WindowListener, ActionListener, MouseMotionListener
{
	private static final long serialVersionUID = 1L;
	JButton ovalButton = new JButton("Oval");
	JButton rectangleButton = new JButton("Rect");
	JButton lineButton = new JButton("Line");
	JButton filledOvalButton = new JButton("Filled oval");
	JButton filledRectangleButton = new JButton("Filled Rect");
	JButton redButton = new JButton("Red");
	JButton blueButton = new JButton("Blue");
	JButton greenButton = new JButton("Green");
	JButton whiteButton = new JButton("White");
	JButton pinkButton = new JButton("Pink");

	private GObject template = new GObject(Shape.OVAL, Color.RED, 363, 65, 25, 25);
	private GObject current = null;

	private LinkedList<GObject> objectList = new LinkedList<>();

	public GUI(int xpos, int ypos)
	{
		setSize(xpos, ypos);
		setTitle("FTCAD");

		Container pane = getContentPane();
		pane.setBackground(Color.BLACK);

		pane.add(this.ovalButton);
		pane.add(this.rectangleButton);
		pane.add(this.lineButton);
		pane.add(this.filledOvalButton);
		pane.add(this.filledRectangleButton);
		pane.add(this.redButton);
		pane.add(this.blueButton);
		pane.add(this.greenButton);
		pane.add(this.whiteButton);
		pane.add(this.pinkButton);

		pane.setLayout(new FlowLayout());
		setVisible(true);
	}

	public void addToListener()
	{
		addWindowListener(this);

		addMouseMotionListener(this);

		this.ovalButton.addActionListener(this);
		this.rectangleButton.addActionListener(this);
		this.lineButton.addActionListener(this);
		this.filledOvalButton.addActionListener(this);
		this.filledRectangleButton.addActionListener(this);
		this.redButton.addActionListener(this);
		this.blueButton.addActionListener(this);
		this.greenButton.addActionListener(this);
		this.whiteButton.addActionListener(this);
		this.pinkButton.addActionListener(this);
	}

	public void windowActivated(WindowEvent e)
	{
		repaint();
	}

	public void windowClosed(WindowEvent e)
	{
		System.exit(0);
	}

	public void windowClosing(WindowEvent e)
	{
		System.exit(0);
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
		repaint();
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowOpened(WindowEvent e)
	{
		repaint();
	}

	public void setObjectList(LinkedList<GObject> objectList)
	{
		this.objectList = objectList;
	}

	public LinkedList<GObject> getObjectList()
	{
		return this.objectList;
	}

	public void addToObjectList(GObject object)
	{
		if (object != null)
			this.objectList.addLast(object);
	}

	public GObject getTemplate()
	{
		return this.template;
	}

	public GObject getCurrent()
	{
		return this.current;
	}

	public void setCurrent(GObject current)
	{
		this.current = current;
	}

	public void mouseMoved(MouseEvent e)
	{
	}

	public void mouseDragged(MouseEvent e)
	{
		if (this.current != null && e.getX() > 0 && e.getY() > 91)
		{
			this.current.setDimensions(e.getX() - this.current.getX(), e.getY() - this.current.getY());
		}
		repaint();
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.ovalButton)
		{
			this.template.setShape(Shape.OVAL);
		}
		else if (e.getSource() == this.rectangleButton)
		{
			this.template.setShape(Shape.RECTANGLE);
		}
		else if (e.getSource() == this.lineButton)
		{
			this.template.setShape(Shape.LINE);
		}
		else if (e.getSource() == this.filledOvalButton)
		{
			this.template.setShape(Shape.FILLED_OVAL);
		}
		else if (e.getSource() == this.filledRectangleButton)
		{
			this.template.setShape(Shape.FILLED_RECTANGLE);
		}
		else if (e.getSource() == this.redButton)
		{
			this.template.setColor(Color.RED);
		}
		else if (e.getSource() == this.blueButton)
		{
			this.template.setColor(Color.BLUE);
		}
		else if (e.getSource() == this.greenButton)
		{
			this.template.setColor(Color.GREEN);
		}
		else if (e.getSource() == this.whiteButton)
		{
			this.template.setColor(Color.WHITE);
		}
		else if (e.getSource() == this.pinkButton)
		{
			this.template.setColor(Color.PINK);
		}
		repaint();
	}

	public void update(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 60, (getSize()).width, (getSize()).height - 60);
		createBufferStrategy(2);
		this.template.draw(g);

		for (ListIterator<GObject> itr = this.objectList.listIterator(); itr.hasNext();)
		{
			((GObject)itr.next()).draw(g);
		}

		if (this.current != null)
		{
			this.current.draw(g);
		}
	}

	public void paint(Graphics g)
	{
		super.paint(g);

		update(g);
	}
}