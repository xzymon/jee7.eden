package com.xzymon.jee7.eden.utils;

import org.apache.log4j.Logger;

import javax.naming.OperationNotSupportedException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class PropertyUtils {

	private static final Logger LOGGER = Logger.getLogger(PropertyUtils.class);

	private Properties properties;

	public PropertyUtils(String propLocation) throws Exception {
		this.properties = loadProperties(propLocation);
	}

	public Properties getProperties() {
		return properties;
	}

	public String getProperty(String key) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Getting key:" + key);

		String val = System.getProperty(key);
		if (val == null)
			val = properties.getProperty(key);

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("value:" + val);

		return (val == null) ? null : val.trim();
	}

	public Boolean getPropertyAsBoolean(String key) {
		String val = getProperty(key);
		if (val == null)
			return null;

		return Boolean.parseBoolean(val);
	}

	public Integer getPropertyAsInt(String key) {
		String val = getProperty(key);
		if (val == null)
			return null;

		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	public Long getPropertyAsLong(String key) {
		String val = getProperty(key);
		if (val == null)
			return null;

		try {
			return Long.parseLong(val);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	public String getProperty(String key, String defaultVal) {
		String val = getProperty(key);
		if (val == null)
			val = defaultVal;
		return val;
	}

	public Long getPropertyAsLong(String key, long defaultVal) {
		Long val = getPropertyAsLong(key);
		if (val == null)
			return defaultVal;
		return val;
	}

	public Integer getPropertyAsInt(String key, int defaultVal) {
		Integer val = getPropertyAsInt(key);
		if (val == null)
			return defaultVal;
		return val;
	}

	public Boolean getPropertyAsBoolean(String key, boolean defaultVal) {
		Boolean val = getPropertyAsBoolean(key);
		if (val == null)
			return defaultVal;
		return val;
	}

	public String[] getPropertiesAsStringValueArray(String key) {
		int index = 1;
		ArrayList<String> list = new ArrayList<String>();
		String tmpProperty;

		while ((tmpProperty = getProperty(key + '.' + index++)) != null) {
			list.add(tmpProperty);
		}

		return list.toArray(new String[list.size()]);
	}

	private Properties loadProperties(final String location) throws Exception {
		if (null == location)
			throw new OperationNotSupportedException("Location cannot be null");

		Properties props = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = getFile(location);
			props.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			inputStream.close();
		}
		return props;
	}

	private InputStream getFile(final String fileLocation) throws FileNotFoundException, OperationNotSupportedException {
		if (null == fileLocation)
			throw new OperationNotSupportedException("Location cannot be null");

		InputStream inputStream = null;
		if (fileLocation.indexOf("file:") > -1) {
			String trueLocation = fileLocation.substring("file:".length());
			try {
				inputStream = new FileInputStream(trueLocation);
			} catch (FileNotFoundException fne) {
				LOGGER.warn("could not find the file from path...trying in the classpath.");
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(trueLocation);
				LOGGER.info("Found the file in the classpath.");
			}
		} else {
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileLocation);
		}

		if (inputStream == null) {
			throw new FileNotFoundException("Property file '" + fileLocation + "' not found in the classpath");
		}

		return inputStream;
	}
}
