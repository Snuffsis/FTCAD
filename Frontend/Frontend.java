package Frontend;

import Shared.Message;
import Shared.TcpConnection;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Stack;

public class Frontend
{
	private ServerSocket _servSock;
	private ArrayList<TcpConnection> _clientConnections = new ArrayList<>();
	private ArrayList<TcpConnection> _backupConnections = new ArrayList<>();
	private long _state = 0L;
	private TcpConnection _masterConnection = null;
	private Stack<Message> _msgStack = new Stack<>();

	public static void main(String[] args)
	{
		int port = Integer.parseInt(args[0]);

		Frontend front = new Frontend(port);
	}

	public Frontend(int port)
	{
		System.out.println("Frontend started");
		try
		{
			this._servSock = new ServerSocket(port);
			this._servSock.setSoTimeout(5);
		}
		catch (IOException e)
		{

			System.err.println("Cannot Create Socket");
		}
		running();
	}

	private void running()
	{
		while (true)
		{
			if (!this._servSock.isClosed())
			{
				newConnection();
			}

			resendMessages();

			if (this._masterConnection != null && !this._masterConnection.isRun())
			{
				election();
			}

			ListIterator<TcpConnection> li = this._backupConnections.listIterator();
			while (li.hasNext())
			{
				TcpConnection tcp = li.next();
				if (tcp != null && tcp.getRemove())
				{
					li.remove();
				}
			}

			if (this._masterConnection != null && this._masterConnection.getRemove())
			{
				this._masterConnection.setRemove(false);
			}

			li = this._clientConnections.listIterator();
			while (li.hasNext())
			{
				TcpConnection tcp = li.next();
				if (tcp.getRemove())
				{
					li.remove();
				}
			}
		}
	}

	private void newConnection()
	{
		try
		{
			TcpConnection connect = new TcpConnection(this._servSock.accept());
			(new Thread((Runnable)connect)).start();
			Message msg = null;
			while (msg == null)
			{
				do
				{
				}
				while (!connect.gotMessage());

				msg = connect.getMsg();
			}

			String str;
			switch ((str = msg.getType()).hashCode())
			{
			case -1396673086:
				if (!str.equals("backup"))
				{
					break;
				}

				if (this._state < msg.getState())
				{
					this._state = msg.getState();
					if (this._masterConnection != null)
					{
						this._masterConnection = connect;
					}
					else
					{

						this._backupConnections.add(this._masterConnection);
						this._masterConnection = connect;
						connect.setRemove(true);
					}
				}
				else if (this._state == 0L && msg.getState() == 0L)
				{
					this._state = 1L;
					this._masterConnection = connect;
				}
				else

				{
					this._backupConnections.add(connect);
					System.out.println("add things");
				}
				return;
			case -1357712437:
				if (!str.equals("client"))
					break;
				this._clientConnections.add(connect);
				if (this._masterConnection != null)
					this._masterConnection.sendMessage(new Message("full"));
				return;
			}
			System.err.println("wrong type " + msg.getType());

		}
		catch (IOException iOException)
		{
		}
	}

	private void resendMessages()
	{
		for (TcpConnection tcpConnection : this._backupConnections)
		{
			if (tcpConnection != null && tcpConnection.gotMessage())
			{
				Message msg = tcpConnection.getMsg();
				if (this._state < msg.getState())
				{
					this._state = msg.getState();
				}

				if (msg != null)
				{
					String str;
					switch ((str = msg.getType()).hashCode())
					{
					case -1396673086:
						if (!str.equals("backup"))
							break;
						continue;
					case -1357712437:
						if (!str.equals("client"))
							break;
						continue;
					case 94756344:
						if (!str.equals("close"))
							break;
						System.out.println("Removing old");
						tcpConnection.setRemove(true);
						continue;
					}

					this._msgStack.add(msg);
				}
			}
		}

		if (this._masterConnection != null && this._masterConnection.gotMessage())
		{

			this._msgStack.add(this._masterConnection.getMsg());
		}

		for (TcpConnection tcpConnection : this._clientConnections)
		{
			if (tcpConnection.gotMessage())
			{
				Message msg = tcpConnection.getMsg();
				String str;
				switch ((str = msg.getType()).hashCode())
				{
				case -1396673086:
					if (!str.equals("backup"))
						break;
					continue;
				case -1357712437:
					if (!str.equals("client"))
						break;
					continue;
				case 94756344:
					if (!str.equals("close"))
						break;
					System.out.println();
					tcpConnection.setRemove(true);
					continue;
				}

				this._msgStack.push(msg);
			}
		}

		while (!this._msgStack.isEmpty())
		{
			Message msg = this._msgStack.pop();
			msg.setState(this._state + 1L);

			try
			{
				this._masterConnection.sendMessage(msg);
			}
			catch (IOException iOException)
			{
			}

			if (!msg.getType().equals("full"))
			{
				for (TcpConnection tcpConnection : this._backupConnections)
				{
					if (tcpConnection != null && !tcpConnection.getRemove())
					{
						try
						{
							tcpConnection.sendMessage(msg);
						}
						catch (IOException iOException)
						{
						}
					}
				}

				for (TcpConnection tcpConnection : this._clientConnections)
				{
					if (!tcpConnection.getRemove())
					{
						try
						{
							tcpConnection.sendMessage(msg);
						}
						catch (IOException iOException)
						{
						}
					}
				}

				continue;
			}
			if (this._masterConnection != null && this._masterConnection.isRun())
			{
				try
				{
					this._masterConnection.sendMessage(msg);
				}
				catch (IOException e)
				{

					e.printStackTrace();
				}
			}
		}
	}

	private void election()
	{
		this._state = 0L;
		System.out.println("Election has started");
		ListIterator<TcpConnection> li = this._backupConnections.listIterator();
		while (li.hasNext())
		{
			TcpConnection tcp = li.next();
			li.remove();
		}

		this._masterConnection = null;
		System.out.println("Election is done");
	}
}