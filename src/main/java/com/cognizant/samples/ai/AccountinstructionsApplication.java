package com.cognizant.samples.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@SpringBootApplication
public class AccountinstructionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountinstructionsApplication.class, args);
	}

	@EnableWs
	@Configuration
	public static class SoapWebServiceConfig extends WsConfigurerAdapter {

		@Bean
		public ServletRegistrationBean messageDispatcherServlet(ApplicationContext context) {
			MessageDispatcherServlet servlet = new MessageDispatcherServlet();
			servlet.setApplicationContext(context);
			servlet.setTransformWsdlLocations(true);

			return new ServletRegistrationBean(servlet,true, "/services/*");
		}


		@Bean("AccountInstructionsSchema")
		public XsdSchema accountInstructionsSchema() {
			return new SimpleXsdSchema(new ClassPathResource("wsdl/AccountInstructionsSchema.xsd"));
		}

		@Bean("AccountInstructions")
		public Wsdl11Definition defaultWsdl11Definition(XsdSchema accountInstructionsSchema) {
			SimpleWsdl11Definition definition = new SimpleWsdl11Definition(new ClassPathResource("wsdl/AccountInstructions.wsdl"));
			return definition;
		}

	}
}
