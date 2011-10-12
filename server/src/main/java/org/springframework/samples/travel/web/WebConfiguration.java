package org.springframework.samples.travel.web;

import java.util.Arrays;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.js.ajax.AjaxUrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.mvc.builder.MvcViewFactoryCreator;
import org.springframework.webflow.mvc.servlet.FlowHandlerAdapter;
import org.springframework.webflow.mvc.servlet.FlowHandlerMapping;
import org.springframework.webflow.mvc.view.FlowAjaxTilesView;
import org.springframework.webflow.security.SecurityFlowExecutionListener;

/**
 * Sets up all artifacts related to the web
 */
@Configuration
public class WebConfiguration {

	@Inject private FlowExecutor flowExecutor;

	@Inject private FlowDefinitionRegistry flowDefinitionRegistry;

	@Bean(name = "hotels/booking")
	public BookingFlowHandler bookingFlowHandler() {
		return new BookingFlowHandler();
	}

	@Bean
	public AjaxUrlBasedViewResolver ajaxUrlBasedViewResolver() {
		AjaxUrlBasedViewResolver aubvr = new AjaxUrlBasedViewResolver();
		aubvr.setViewClass(FlowAjaxTilesView.class);
		return aubvr;
	}

	@Bean
	public TilesConfigurer tilesConfigurer() {
		TilesConfigurer tilesConfigurer = new TilesConfigurer();
		tilesConfigurer.setDefinitions(new String[]{"/WEB-INF*//**//*tiles.xml"});
		return tilesConfigurer;
	}

	@Bean
	public FlowHandlerMapping mapping() {
		FlowHandlerMapping flowHandlerMapping = new FlowHandlerMapping();
		flowHandlerMapping.setOrder(-1);
		flowHandlerMapping.setFlowRegistry(flowDefinitionRegistry);
		return flowHandlerMapping;
	}

	@Bean
	public FlowHandlerAdapter flowHandlerAdapter() {
		FlowHandlerAdapter fha = new FlowHandlerAdapter();
		fha.setFlowExecutor(flowExecutor);
		return fha;
	}

	@Bean
	public MvcViewFactoryCreator viewFactoryCreator() {
		MvcViewFactoryCreator mvcViewFactoryCreator = new MvcViewFactoryCreator();
		mvcViewFactoryCreator.setViewResolvers(Arrays.asList(ajaxUrlBasedViewResolver()));
		return mvcViewFactoryCreator;
	}

	@Bean
	public SecurityFlowExecutionListener securityFlowExecutionListener() {
		return new SecurityFlowExecutionListener();
	}
}
