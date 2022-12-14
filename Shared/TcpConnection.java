package Shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class TcpConnection extends Thread
{
	private Socket _socket;
	private ObjectOutputStream _out;
	private ObjectInputStream _in;
	private Message msg = new Message();
	private boolean _gotMessage = false;
	private boolean _run = true;
	private boolean _remove = false;
	private Semaphore sem = new Semaphore(0);

	public TcpConnection(InetAddress adr, int port) throws IOException
	{
		this._socket = new Socket(adr, port);

		this._out = new ObjectOutputStream(this._socket.getOutputStream());
		this._in = new ObjectInputStream(this._socket.getInputStream());
	}

	public TcpConnection(Socket socket) throws IOException
	{
		this._socket = socket;

		this._out = new ObjectOutputStream(this._socket.getOutputStream());
		this._in = new ObjectInputStream(this._socket.getInputStream());
	}

	public synchronized void sendMessage(Message msg) throws IOException
	{
		this._out.writeObject(msg);
	}

	public synchronized boolean gotMessage()
	{
		return this._gotMessage;
	}

	public synchronized boolean isRun()
	{
		return this._run;
	}

	public synchronized Message getMsg()
	{
		this.sem.release();

		this._gotMessage = false;
		return this.msg;
	}

	public synchronized void setRun(boolean run)
	{
		this._run = run;
	}

	public synchronized boolean getRemove()
	{
		return this._remove;
	}

	public synchronized void setRemove(boolean remove)
	{
		this._remove = remove;
	}

	public synchronized Message readObject() throws IOException, ClassNotFoundException
	{
		return (Message)this._in.readObject();
	}

	public void run()
	{
		while (this._socket.isConnected() && this._run)
		{
			try
			{
				this.msg = (Message)this._in.readObject();
				this._gotMessage = true;
				if (this.msg.getType().equals("close"))
				{
					this._run = false;
					this._socket.close();
				}
				try
				{
					this.sem.acquire();
				}
				catch (InterruptedException interruptedException)
				{
				}

			}
			catch (ClassNotFoundException e)
			{

				this._run = false;
				try
				{
					this._socket.close();
				}
				catch (IOException iOException)
				{
				}

			}
			catch (IOException e)
			{

				this._run = false;
				try
				{
					this._socket.close();
				}
				catch (IOException iOException)
				{
				}
			}
		}

		this.msg = new Message("close");
		this._gotMessage = true;
		try
		{
			this.sem.acquire();
		}
		catch (InterruptedException e)
		{

			e.printStackTrace();
		}
		try
		{
			this._socket.close();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
	}
}
