package com.xzymon.jee7.eden.utils;

import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.*;

public class JMSHelper  {
	private static final Logger LOGGER = Logger.getLogger(JMSHelper.class);
	private ConnectionFactory connectionFactory;
	private static final boolean isDebug = LOGGER.isDebugEnabled();
	private static final boolean isTrace = LOGGER.isTraceEnabled();

	public static String DESTINATION_TYPE_QUEUE = "queue";
	public static String DESTINATION_TYPE_TOPIC = "topic";

	static public enum DestinationType {
		QUEUE,
		TOPIC;

		static public DestinationType parse(String destinationTypeName) throws IllegalArgumentException {
			JMSHelper.DestinationType destinationTypeLocal = QUEUE;
			if ("topic".equalsIgnoreCase(destinationTypeName)) {
				destinationTypeLocal = JMSHelper.DestinationType.TOPIC;
			} else if ("queue".equalsIgnoreCase(destinationTypeName)) {
				destinationTypeLocal = JMSHelper.DestinationType.QUEUE;
			} else {
				throw new IllegalArgumentException("value for destinationTypeName not valid - " + ((null != destinationTypeName) ? destinationTypeName : "null"));
			}
			return destinationTypeLocal;
		}
	}

	static public enum JMSHeadersType {
		JMS_PAYLOAD,
		JMS_MESSAGEID,
		JMS_CORRELATIONID("JMSCorrelationID"),
		JMS_DELIVERYMODE,
		JMS_PRIORITY,
		JMS_DESTINATION,
		JMS_TIMESTAMP,
		JMS_EXPIRATION,
		JMS_TYPE,
		JMS_REPLYTO,
		JMS_REDELIVERED;

		private String fieldName = "";

		JMSHeadersType() {
			this("");
		}

		JMSHeadersType(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldName() {
			return fieldName;
		}
	}

	private JMSHelper(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public void cleanup() {
		connectionFactory = null;
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}


	public Destination lookupTopicDestination(String destinationName) throws JMSException {
		return lookupDestination(destinationName, DestinationType.TOPIC);
	}

	public Destination lookupQueueDestination(String destinationName) throws JMSException {
		return lookupDestination(destinationName, DestinationType.QUEUE);
	}

	public Destination lookupDestination(String destinationName, String destinationType) throws JMSException {
		return lookupDestination(destinationName, JMSHelper.DestinationType.parse(destinationType));
	}

	public Destination lookupDestination(String destinationName, DestinationType destinationType) throws JMSException {
		Connection connection = null;
		Session session = null;
		Destination destinationLocal = null;

		if (null == connectionFactory)
			throw new JMSException("connection factory is null...can't do anything...");

		try {
			connection = connectionFactory.createConnection(); // Create connection
			boolean transacted = false;
			session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE); // Create Session

			if (!"".equals(destinationName)) {
				if (null != destinationType) {
					if (destinationType == DestinationType.QUEUE)
						destinationLocal = session.createQueue(destinationName);
					else if (destinationType == DestinationType.TOPIC)
						destinationLocal = session.createTopic(destinationName);
				} else {
					throw new JMSException("destination type should be defined if using the destinationName construct");
				}
			} else {
				throw new JMSException("destination name is null...can't do anything...");
			}
		} finally {
			if (null != session)
				session.close();

			if (null != connection)
				connection.close();
		}
		return destinationLocal;
	}

	public static Object lookupJNDI(Hashtable<String, String> jndiEnv, final String lookupName) throws NamingException {
		Object jndiLookup = null;
		if (null == jndiEnv) {
			throw new IllegalArgumentException("jndi params not defined.");
		}

		if (isDebug) {
			for (String key : jndiEnv.keySet()) {
				LOGGER.debug(String.format("Context: %s - %s", key.toString(), jndiEnv.get(key)));
			}
		}

		if (null == lookupName)
			throw new IllegalArgumentException("lookupName not defined.");

		// Lookup Connection Factory
		Context namingContext = new InitialContext(jndiEnv);

		if (isDebug)
			LOGGER.debug("Context Created : " + namingContext.toString());

		jndiLookup = namingContext.lookup(lookupName);

		if (null != jndiLookup) {
			if (isDebug)
				LOGGER.debug("Lookup JNDI Success - Impl Class: {}" + jndiLookup.getClass().getName());
		} else {
			throw new NamingException("Lookup JNDI failed - object name could not be found");
		}

		return jndiLookup;
	}

	public static String generateCorrelationID() {
		return UUID.randomUUID().toString();
	}

	public static Object getMessagePayload(Message msg) throws JMSException {
		String messagePayload = null;
		if (null != msg) {
			if (msg instanceof TextMessage) {
				messagePayload = ((TextMessage) msg).getText();
			} else if (msg instanceof MapMessage) {
				throw new UnsupportedOperationException("Not implemented yet");
			} else if (msg instanceof BytesMessage) {
				throw new UnsupportedOperationException("Not implemented yet");
			} else if (msg instanceof ObjectMessage) {
				throw new UnsupportedOperationException("Not implemented yet");
			} else if (msg instanceof StreamMessage) {
				throw new UnsupportedOperationException("Not implemented yet");
			}
		}

		return messagePayload;
	}

	public static Map<String, Object> getMessageProperties(Message msg) throws JMSException {
		Map<String, Object> props = null;
		if (null != msg) {
			props = new HashMap();
			Enumeration txtMsgPropertiesEnum = msg.getPropertyNames();
			while (txtMsgPropertiesEnum.hasMoreElements()) {
				String propName = (String) txtMsgPropertiesEnum.nextElement();
				props.put(propName, msg.getObjectProperty(propName));
			}
		}

		return props;
	}

	public static String getMessagePropertiesAsString(Message msg, String delimiter) throws JMSException {
		return getMessagePropertiesAsString(JMSHelper.getMessageProperties(msg), delimiter);
	}

	public static String getMessagePropertiesAsString(Map<String, Object> messageProperties, String delimiter) throws JMSException {
		String messagePropertiesStr = "";

		//transform the property map into a string
		if (null != messageProperties) {
			for (Map.Entry<String, Object> header : messageProperties.entrySet()) {
				messagePropertiesStr += String.format("[%s%s%s]", header.getKey(), (null != delimiter) ? delimiter : ",", (null != header.getValue()) ? header.getValue().toString() : "null");
			}
		}

		return messagePropertiesStr;
	}

	public static Map<JMSHeadersType, Object> getMessageJMSHeaderPropsAsMap(Message msg) throws JMSException {
		Map<JMSHeadersType, Object> jmsHeaderProps = null;
		if (null != msg) {
			jmsHeaderProps = new HashMap<JMSHeadersType, Object>();

			if (null != msg.getJMSMessageID() && !"".equals(msg.getJMSMessageID()))
				jmsHeaderProps.put(JMSHeadersType.JMS_MESSAGEID, msg.getJMSMessageID());

			if (null != msg.getJMSCorrelationID() && !"".equals(msg.getJMSCorrelationID()))
				jmsHeaderProps.put(JMSHeadersType.JMS_CORRELATIONID, msg.getJMSCorrelationID());

			if (null != msg.getJMSDestination())
				jmsHeaderProps.put(JMSHeadersType.JMS_DESTINATION, msg.getJMSDestination());

			if (null != msg.getJMSReplyTo())
				jmsHeaderProps.put(JMSHeadersType.JMS_REPLYTO, msg.getJMSReplyTo());

			if (null != msg.getJMSType() && !"".equals(msg.getJMSType()))
				jmsHeaderProps.put(JMSHeadersType.JMS_TYPE, msg.getJMSType());

			jmsHeaderProps.put(JMSHeadersType.JMS_REDELIVERED, msg.getJMSRedelivered());
			jmsHeaderProps.put(JMSHeadersType.JMS_TIMESTAMP, msg.getJMSTimestamp());
			jmsHeaderProps.put(JMSHeadersType.JMS_EXPIRATION, msg.getJMSExpiration());
			jmsHeaderProps.put(JMSHeadersType.JMS_DELIVERYMODE, msg.getJMSDeliveryMode());
			jmsHeaderProps.put(JMSHeadersType.JMS_PRIORITY, msg.getJMSPriority());
		}

		return jmsHeaderProps;
	}

	public static Map<JMSHeadersType, Object> getMessageJMSHeaderPropsAsMap(final Destination destination, final Integer deliveryMode, final Integer priority, final String correlationID, final Destination replyTo) throws JMSException {
		Map<JMSHeadersType, Object> jmsHeaderProps = new HashMap<JMSHeadersType, Object>();
		jmsHeaderProps.put(JMSHeadersType.JMS_CORRELATIONID, correlationID);
		jmsHeaderProps.put(JMSHeadersType.JMS_DELIVERYMODE, deliveryMode);
		jmsHeaderProps.put(JMSHeadersType.JMS_PRIORITY, priority);
		jmsHeaderProps.put(JMSHeadersType.JMS_DESTINATION, destination);
		jmsHeaderProps.put(JMSHeadersType.JMS_REPLYTO, replyTo);

		return jmsHeaderProps;
	}

	public static String getMessageJMSHeaderPropsAsString(Message msg, String delimiter) throws JMSException {
		return getMessageJMSHeaderPropsAsString(JMSHelper.getMessageJMSHeaderPropsAsMap(msg), delimiter);
	}

	public static String getMessageJMSHeaderPropsAsString(Map<JMSHeadersType, Object> messageJMSHeaderProps, String delimiter) throws JMSException {
		String messageJMSHeaderPropsStr = "";
		//transform the property map into a string
		if (null != messageJMSHeaderProps) {
			for (Map.Entry<JMSHeadersType, Object> header : messageJMSHeaderProps.entrySet()) {
				messageJMSHeaderPropsStr += String.format("[%s%s%s]", header.getKey(), (null != delimiter) ? delimiter : ",", (null != header.getValue()) ? header.getValue().toString() : "null");
			}
		}

		return messageJMSHeaderPropsStr;
	}
}
