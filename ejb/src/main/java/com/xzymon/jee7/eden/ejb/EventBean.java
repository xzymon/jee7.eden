package com.xzymon.jee7.eden.ejb;

import com.xzymon.jee7.eden.ejb.api.EventInterface;
import com.xzymon.jee7.eden.ejb.api.EventRemoteInterface;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless
@Remote(EventRemoteInterface.class)
@Local(EventInterface.class)
public class EventBean implements EventInterface {
	private static final String HELLO_MSG = "EventBean says: Hello!";

	@Override
	public void sayHello() {
		System.out.println(getHelloMessage());
	}

	@Override
	public String getHelloMessage() {
		return HELLO_MSG;
	}
}
