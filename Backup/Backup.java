package Backup;

import Client.GObject;
import Shared.Message;
import Shared.TcpConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;

public class Backup
{
	private LinkedList<GObject> Gobjectlist = new LinkedList<>();
	private TcpConnection tcp;
	private boolean running = true;
	private boolean master = false;
	private long state = 0L;
	private boolean connected = false;

	public static void main(String[] args)
	{
		Backup backup = new Backup();
	}

	public Backup()
	{
		while (true)
		{
			if (!this.connected)
			{
				try
				{
					this.tcp = new TcpConnection(InetAddress.getByName("localhost"), 25000);
					this.connected = true;
					this.tcp.start();
					Message msg = new Message("backup");
					msg.setState(this.state++);

					this.tcp.sendMessage(msg);
					System.out.println("connted");
				}
				catch (IOException e1)
				{
					System.err.println("den funkar");
				}
			}

			if (this.connected)
			{

				if (this.tcp.gotMessage())
				{
					receiveObject();
				}
				if (!this.tcp.isRun())
				{
					this.connected = false;
					System.out.println("not connected");
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

	public void receiveObject()
	{
		try
		{
			if (this.tcp.gotMessage())
			{
				Message msg, msg1, message = this.tcp.getMsg();

				String str;
				switch ((str = message.getType()).hashCode())
				{
				case -511232101:
					if (!str.equals("fullist"))
					{
						break;
					}
					storeList(message.getObjectList());
					this.state = message.getState();
					return;
				case 3154575:
					if (!str.equals("full"))
						break;
					msg = new Message("fullist", this.Gobjectlist);
					msg.setState(this.state++);
					this.tcp.sendMessage(msg);
				case 109757585:
					if (!str.equals("state"))
						break;
					msg1 = new Message("state");
					msg1.setState(this.state);
					this.tcp.sendMessage(msg1);
					return;
				case 1085372378:
					if (!str.equals("incremental"))
						break;
					this.state++;
					storeObject(message.getObject());
					return;
				}
				System.out.println(message.getType());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void storeObject(GObject gObject)
	{
		this.Gobjectlist.add(gObject);
	}

	public void storeList(LinkedList<GObject> ll)
	{
		this.Gobjectlist = ll;
	}
}
