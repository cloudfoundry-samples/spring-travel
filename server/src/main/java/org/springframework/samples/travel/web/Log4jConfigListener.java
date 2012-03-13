package org.springframework.samples.travel.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Level;
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

		AmqpAppender amqpAppender;
		CloudEnvironment cloudEnvironment = new CloudEnvironment();
		if (cloudEnvironment.isCloudFoundry()) {
			amqpAppender = createCloudFoundryAppender(cloudEnvironment);
		}
		else {
			amqpAppender = createLocalAppender();
		}
		
		Logger.getRootLogger().addAppender(amqpAppender);
	}
 
	public void contextDestroyed(ServletContextEvent event) {
		Log4jWebConfigurer.shutdownLogging(event.getServletContext());
	}
	
	AmqpAppender createCloudFoundryAppender(CloudEnvironment env) {
		RabbitServiceInfo rabbitServiceInfo = env.getServiceInfo("rabbitmq-logs", RabbitServiceInfo.class);
		AmqpAppender amqpAppender = createAmqpAppender();
		amqpAppender.setHost(rabbitServiceInfo.getHost());
		amqpAppender.setPort(rabbitServiceInfo.getPort());
		amqpAppender.setVirtualHost(rabbitServiceInfo.getVirtualHost());
		amqpAppender.setUsername(rabbitServiceInfo.getUserName());
		amqpAppender.setPassword(rabbitServiceInfo.getPassword());
		return amqpAppender;
	}
	
	AmqpAppender createLocalAppender() {
		AmqpAppender amqpAppender = createAmqpAppender();
		amqpAppender.setHost("localhost");
		amqpAppender.setVirtualHost("/");
		amqpAppender.setUsername("guest");
		amqpAppender.setPassword("guest");
		return amqpAppender;
	}
	
	AmqpAppender createAmqpAppender() {
		AmqpAppender amqpAppender = new AmqpAppender();
		amqpAppender.setName("rabbit");
		amqpAppender.setExchangeName("amq.topic");
		amqpAppender.setExchangeType("topic");
		amqpAppender.setRoutingKeyPattern("logs.spring-travel");
		amqpAppender.setApplicationId("spring-travel");
		amqpAppender.setThreshold(Level.INFO);
		return amqpAppender;
	}
}
