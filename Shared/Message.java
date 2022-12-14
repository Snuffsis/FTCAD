package Shared;

import Client.GObject;
import java.io.Serializable;
import java.util.LinkedList;
import org.json.simple.JSONObject;

public class Message implements Serializable
{
	private static final long serialVersionUID = 1L;
	private JSONObject _jsn = new JSONObject();

	public Message()
	{
	}

	public Message(String type)
	{
		this._jsn.put("type", type);
	}

	public Message(String type, GObject object)
	{
		this._jsn.put("type", type);
		this._jsn.put("object", object);
	}

	public Message(String type, LinkedList<GObject> objectList)
	{
		this._jsn.put("type", type);
		this._jsn.put("objectList", objectList);
	}

	public void setState(long state)
	{
		this._jsn.put("state", Long.valueOf(state));
	}

	public long getState()
	{
		return ((Long)this._jsn.get("state")).longValue();
	}

	public GObject getObject()
	{
		return (GObject)this._jsn.get("object");
	}

	public String getType()
	{
		return (String)this._jsn.get("type");
	}

	public LinkedList<GObject> getObjectList()
	{
		return (LinkedList<GObject>)this._jsn.get("objectList");
	}
}