package Client;

import Shared.Message;
import Shared.TcpConnection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class Cad implements MouseListener
{
	private static GUI gui = new GUI(750, 600);
	private InetAddress ADDRESS;
	private int PORT = 25000;
	private TcpConnection connection = null;
	private GObject current = null;
	private LinkedList<GObject> objectList = new LinkedList<>();
	private boolean connected = false;
	private Message lastMessage = null;

	public static void main(String[] args)
	{
		System.out.println("MAIN 0");
		Cad c = new Cad();
		System.out.println("MAIN 1");
		gui.addMouseListener(c);
		System.out.println("MAIN 2");
		gui.addToListener();
		System.out.println("MAIN 3");
		c.go();
	}

	private Cad()
	{
		try
		{
			this.ADDRESS = InetAddress.getByName("localhost");
		}
		catch (UnknownHostException e)
		{

			e.printStackTrace();
		}
	}

	private void connect()
	{
		System.out.println("CONNECT 0");
		try
		{
			this.connection = new TcpConnection(this.ADDRESS, this.PORT);
			this.connected = true;
			System.out.println("CONNECT 1");
			this.connection.start();
			Message message = new Message("client");
			System.out.println("CONNECT 2");
			try
			{
				this.connection.sendMessage(message);
			}
			catch (IOException iOException)
			{
			}

			System.out.println("CONNECT 3");
			if (this.lastMessage != null)
			{
				this.connection.sendMessage(this.lastMessage);
				this.lastMessage = null;
			}
			System.out.println("CONNECT 4");
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
	}

	private void go()
	{
		while (true)
		{
			while (this.connected)
			{

				while (this.connected)
				{
					Message message = receiveMessage();
					if (message != null)
					{
						if (message.getType().equalsIgnoreCase("incremental"))
						{
							gui.addToObjectList(message.getObject());
							gui.repaint();
						}
						else if (message.getType().equalsIgnoreCase("fullist"))
						{
							gui.setObjectList(message.getObjectList());
							gui.repaint();
						}
						else
						{

							System.out.println("invalid message");
						}
					}
					try
					{
						Thread.sleep(5L);
					}
					catch (InterruptedException e)
					{

						e.printStackTrace();
					}
				}
			}
			connect();
			try
			{
				Thread.sleep(5L);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		}
	}

	private Message receiveMessage()
	{
		if (this.connection.gotMessage())
		{
			Message msg = this.connection.getMsg();
			System.out.println("receiveMessage type: " + msg.getType());
			return msg;
		}
		return null;
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == 1)
		{
			if (e.getX() > 0 && e.getY() > 91)
			{
				this.current = new GObject(gui.getTemplate().getShape(), gui.getTemplate().getColor(), e.getX(),
						e.getY(), 0, 0);
				gui.setCurrent(this.current);
			}
			else
			{
				this.current = null;
				gui.setCurrent(this.current);
			}
		}
		gui.repaint();
	}

	public void mouseClicked(MouseEvent e)
	{
		if (e.getButton() == 3 && this.objectList.size() > 0)
		{
			System.out.println("removed last object");
			this.objectList = gui.getObjectList();
			this.objectList.removeLast();
			gui.setObjectList(this.objectList);

			Message message = new Message("fullist", this.objectList);
			try
			{
				this.connection.sendMessage(message);
			}
			catch (IOException e1)
			{

				this.lastMessage = new Message("fullist", this.objectList);
				this.connected = false;
			}
		}
		gui.repaint();
	}

	public void mouseReleased(MouseEvent e)
	{
		if (this.current != null)
		{
			this.objectList = gui.getObjectList();

			gui.setObjectList(this.objectList);

			Message msg = new Message("incremental", this.current);
			try
			{
				this.connection.sendMessage(msg);
			}
			catch (IOException e1)
			{

				this.lastMessage = new Message("incremental", this.current);
				this.connected = false;
			}
			System.out.println("send incremental");
			this.current = null;
			gui.setCurrent(this.current);
		}
		gui.repaint();
	}
}