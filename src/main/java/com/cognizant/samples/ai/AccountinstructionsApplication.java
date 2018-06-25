package com.cognizant.samples.ai;

import com.cognizant.samples.ai.instructions.AccountAlreadyExistsException;
import com.cognizant.samples.ai.instructions.ObjectNotFoundException;
import com.cognizant.samples.ai.instructions.fault.SOAPFaultCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.Properties;

@SpringBootApplication
public class AccountinstructionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountinstructionsApplication.class, args);
	}

	@EnableWs
	@Configuration
	public static class SoapWebServiceConfig extends WsConfigurerAdapter {


		@Bean("soapFaultAnnotationExceptionResolver")
        // Override the default annotation exception resolver
		public SOAPFaultCustomizer soapFaultAnnotationExceptionResolver(Environment env){
			SOAPFaultCustomizer exceptionResolver = new SOAPFaultCustomizer(env);
			exceptionResolver.setGenericMessage("${faultString.generic:An error occurred}");
			Properties p = new Properties();
			p.setProperty(ObjectNotFoundException.class.getName(), "CLIENT,A required entity was not found");
            p.setProperty(AccountAlreadyExistsException.class.getName(), "CLIENT,The account is already present");
			exceptionResolver.setOrder(1);
			return exceptionResolver;
		}

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
