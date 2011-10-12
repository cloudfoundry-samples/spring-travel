package org.springframework.samples.travel.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.samples.travel.domain.Amenity;
import org.springframework.samples.travel.domain.Booking;
import org.springframework.samples.travel.domain.Bookings;
import org.springframework.samples.travel.domain.Hotel;
import org.springframework.samples.travel.domain.Hotels;
import org.springframework.samples.travel.domain.User;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.view.xml.MarshallingView;

/**
 * @author Josh Long
 */
@Configuration
public class RestConfiguration {

	private Class<?>[] jaxbClasses = { Hotels.class,Bookings.class, Amenity.class ,Booking.class, User.class, Hotel.class};

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(this.jaxbClasses);
		return marshaller;
	}

	@Bean
	public MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter() {
		MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
		mappingJacksonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
		return mappingJacksonHttpMessageConverter;
	}

	@Bean
	public MarshallingHttpMessageConverter marshallingHttpMessageConverter() {
		MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter(this.marshaller());
		converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_XML));
		return converter;
	}

	public List<HttpMessageConverter<?>> httpMessageConverters() {
		List<HttpMessageConverter<?>> mcList = new ArrayList<HttpMessageConverter<?>>();
		mcList.add(this.marshallingHttpMessageConverter());
		mcList.add(this.mappingJacksonHttpMessageConverter());
		return mcList;
	}
	@Bean
	public MarshallingView view() {
		return new MarshallingView( marshaller());
	}

	/*@Bean
	public DefaultAnnotationHandlerMapping defaultAnnotationHandlerMapping() {
		return new DefaultAnnotationHandlerMapping();
	}*/

	@Bean
	public AnnotationMethodHandlerAdapter handlerAdapter() {

		List<HttpMessageConverter<?>> converters =  httpMessageConverters();

		AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
		annotationMethodHandlerAdapter.setMessageConverters(converters.toArray(new HttpMessageConverter[converters.size()]));
		return annotationMethodHandlerAdapter;
	}

}
