package com.xzymon.jee7.eden.mdb;

import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.jms.Message;
import javax.jms.MessageListener;


@MessageDriven(name = "UniversalMessagingListener",
		activationConfig = {
				@ActivationConfigProperty(propertyName = "connectionFactory", propertyValue = "java:global/remoteJMS/QueueConnectionFactory"),
				@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:global/remoteJMS/xzymon/path/xqueue"),
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
				@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
		}
)
public class UniversalMessagingListener implements MessageListener, MessageDrivenBean {
	private static final Logger LOGGER = Logger.getLogger(UniversalMessagingListener.class);

	private MessageDrivenContext mdbContext;

	@Override
	public void onMessage(Message message) {
		LOGGER.info("onMessage!");
		System.out.println("Message read");
	}

	@PostConstruct
	public void ejbCreate() {
		LOGGER.info("ejbCreate()");
	}

	@Override
	@Resource
	public void setMessageDrivenContext(MessageDrivenContext ctx) throws EJBException {
		LOGGER.info("setMessageDrivenContext()");
		this.mdbContext = ctx;
	}

	@PreDestroy
	public void ejbRemove() throws EJBException {
		LOGGER.info("ejbRemove()");
	}
}
