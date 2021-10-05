package com.xzymon.jee7.eden.mdb;

import javax.jms.Message;
import javax.jms.MessageListener;

public class UniversalMessagingListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		System.out.println("Message read");
	}
}
