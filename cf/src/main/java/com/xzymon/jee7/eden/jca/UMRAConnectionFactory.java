package com.xzymon.jee7.eden.jca;

import org.apache.log4j.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;

public class UMRAConnectionFactory extends com.sun.genericra.outbound.ManagedJMSConnectionFactory {
	//private static final Logger LOGGER = Logger.getLogger(UMRAConnectionFactory.class);

	private final ConnectionFactory internalConnectionFactory;
	private Connection internalCachedConnection;

	private volatile Object padlock = new Object();

	public UMRAConnectionFactory(ConnectionFactory internalConnectionFactory) {
		if (null == internalConnectionFactory) {
			System.out.println("Passed ConnectionFactory is null!");
			//LOGGER.error("Passed ConnectionFactory is null!");
			throw new IllegalArgumentException("ConnectionFactory cannot be null");
		}

		this.internalConnectionFactory = internalConnectionFactory;
	}

	private void checkAndCreateConnection() throws JMSException {
		checkAndCreateConnection(null, null);
	}

	private void checkAndCreateConnection(String userName, String password) throws JMSException {
		if (null == internalCachedConnection) {
			synchronized (padlock) {
				if (null == internalCachedConnection) {
					if (null != userName || null != password) {
						System.out.println("Creating connection with credentials");
						//LOGGER.info("Creating connection with credentials");
						internalCachedConnection = internalConnectionFactory.createConnection(userName, password);
					} else {
						System.out.println("At least one of [userName, password] is null. Will continue without using credentials");
						//LOGGER.error("At least one of [userName, password] is null. Will continue without using credentials");
						internalCachedConnection = internalConnectionFactory.createConnection();
					}
				}
			}
		}
	}

	@Override
	public Connection createConnection() throws JMSException {
		checkAndCreateConnection();
		return new UMRAConnection(internalCachedConnection);
	}

	@Override
	public Connection createConnection(String userName, String password) throws JMSException {
		checkAndCreateConnection(userName, password);
		return new UMRAConnection(internalCachedConnection);
	}

	@Override
	public JMSContext createContext() {
		return null;
	}

	@Override
	public JMSContext createContext(String userName, String password) {
		return null;
	}

	@Override
	public JMSContext createContext(String userName, String password, int sessionMode) {
		return null;
	}

	@Override
	public JMSContext createContext(int sessionMode) {
		return null;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		if (null != internalCachedConnection)
			internalCachedConnection.close();
		internalCachedConnection = null;
	}
}