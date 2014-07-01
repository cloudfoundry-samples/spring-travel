package org.springframework.samples.travel.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.cloudfoundry.runtime.env.RabbitServiceInfo;
import org.springframework.amqp.rabbit.log4j.AmqpAppender;
import org.springframework.web.util.Log4jWebConfigurer;

/**
 * Dynamic configuration of Log4j with an AMQP appender.
 */
public class Log4jConfigListener implements ServletContextListener {
	public void contextInitialized(ServletContextEvent event) {
		Log4jWebConfigurer.initLogging(event.getServletContext());

		CloudEnvironment cloudEnvironment = new CloudEnvironment();
		if (cloudEnvironment.isCloudFoundry()) {
			initCloudFoundryAppender(cloudEnvironment);
		}
	}
 
	public void contextDestroyed(ServletContextEvent event) {
		Log4jWebConfigurer.shutdownLogging(event.getServletContext());
	}
	
	void initCloudFoundryAppender(CloudEnvironment env) {
		try {
			RabbitServiceInfo rabbitServiceInfo = env.getServiceInfo("rabbitmq-logs", RabbitServiceInfo.class);
			AmqpAppender amqpAppender = getAmqpAppender();
			amqpAppender.close();                                 // Required for changes to take effect
			amqpAppender.setHost(rabbitServiceInfo.getHost());
			amqpAppender.setPort(rabbitServiceInfo.getPort());
			amqpAppender.setVirtualHost(rabbitServiceInfo.getVirtualHost());
			amqpAppender.setUsername(rabbitServiceInfo.getUserName());
			amqpAppender.setPassword(rabbitServiceInfo.getPassword());
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	AmqpAppender getAmqpAppender() {
		return (AmqpAppender) Logger.getRootLogger().getAppender("amqp");
	}
}
