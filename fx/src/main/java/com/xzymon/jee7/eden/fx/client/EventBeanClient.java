package com.xzymon.jee7.eden.fx.client;

import com.xzymon.jee7.eden.ejb.api.EventRemoteInterface;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

public class EventBeanClient {
	public static String invokeEventHelloMessage() throws NamingException {
		EventRemoteInterface event = lookupEventRemoteInterface();
		return event.getHelloMessage();
	}

	public static EventRemoteInterface lookupEventRemoteInterface() throws NamingException{
		final Hashtable props = new Hashtable();
		props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		final Context context = new InitialContext(props);
		String appName = "eden-1.0-SNAPSHOT";
		String moduleName = "ejb-1.0-SNAPSHOT";
		String distinctName = "";
		String beanName = "EventBean";
		String interfaceName = EventRemoteInterface.class.getName();
		String lookupName = String.format("ejb:%1$s/%2$s/%3$s/%4$s!%5$s", appName, moduleName, distinctName, beanName, interfaceName);
		return (EventRemoteInterface) context.lookup(lookupName);
	}
}
