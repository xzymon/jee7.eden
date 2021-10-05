package com.xzymon.jee7.eden.jca;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;

public class UMRAConnectionFactory implements ConnectionFactory {
	private final ConnectionFactory internalConnectionFactory;
	private Connection internalCachedConnection;

	private volatile Object padlock = new Object();

	public UMRAConnectionFactory(ConnectionFactory internalConnectionFactory) {
		if (null == internalConnectionFactory)
			throw new IllegalArgumentException("ConnectionFactory cannot be null");

		this.internalConnectionFactory = internalConnectionFactory;
	}

	private void checkAndCreateConnection() throws JMSException {
		checkAndCreateConnection(null, null);
	}

	private void checkAndCreateConnection(String userName, String password) throws JMSException {
		if (null == internalCachedConnection) {
			synchronized (padlock) {
				if (null == internalCachedConnection) {
					if (null != userName || null != password)
						internalCachedConnection = internalConnectionFactory.createConnection(userName, password);
					else
						internalCachedConnection = internalConnectionFactory.createConnection();
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