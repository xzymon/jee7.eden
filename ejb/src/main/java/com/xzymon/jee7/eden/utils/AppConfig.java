package com.xzymon.jee7.eden.utils;

import org.apache.log4j.Logger;

public class AppConfig {
	public static final String APP_CONFIGPATH_DEFAULT = "application.properties";
	public static final String APP_CONFIGPATH_ENVPROP = "app.config.path";
	private static final Logger LOGGER = Logger.getLogger(AppConfig.class);
	//singleton instance
	private static AppConfig instance;

	//wrapper property
	private final PropertyUtils propertyUtils;

	private AppConfig(String propertyFile) throws Exception {
		propertyUtils = new PropertyUtils(propertyFile);
	}

	public static AppConfig getInstance() {
		if (instance == null) {
			synchronized (AppConfig.class) {  //1
				if (instance == null) {
					try {
						String propLocation = "";
						if (null != System.getProperty(APP_CONFIGPATH_ENVPROP)) {
							propLocation = System.getProperty(APP_CONFIGPATH_ENVPROP);
							LOGGER.info(APP_CONFIGPATH_ENVPROP + " environment property specified: Loading application configuration from " + propLocation);
						} else {
							LOGGER.info("Loading application configuration from classpath " + APP_CONFIGPATH_DEFAULT);
							propLocation = APP_CONFIGPATH_DEFAULT;
						}

						instance = new AppConfig(propLocation);
					} catch (Exception e) {
						LOGGER.error("Could not load the property file", e);
					}
				}
			}
		}
		return instance;
	}

	public PropertyUtils getPropertyHelper() {
		return propertyUtils;
	}

}
