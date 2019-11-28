package com.xl.gccpage;

import android.os.*;

public class GetInfo extends Thread
{
	private String url;
	private String user;
	//GetInfoListener listener;
	Handler handler;
	public void run()
	{
		String text =null;
		if(url.startsWith("https://") || url.startsWith("HTTPS://"))
		text = HttpUtil.HttpsPost(url,null,null);
		else
		text = HttpUtil.get(url);
		Message m=new Message();
		m.what=1;
		m.obj=text;
		handler.sendMessage(m);
	}
	
	public GetInfo(String url,final GetInfoListener listener)
	{
		this.url=url;
		//this.user=user;
		//this.listener = listener;
		this.handler=new Handler()
		{
			public void handleMessage(android.os.Message msg) 
			{
				if(msg.what==1)
				{
					if(listener!=null)
						listener.onGetText((String)msg.obj);
				}
			}
		};
	}
}
