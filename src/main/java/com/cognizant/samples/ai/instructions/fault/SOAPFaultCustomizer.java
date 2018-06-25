package com.cognizant.samples.ai.instructions.fault;


import com.cognizant.samples.account_instructions.ObjectFactory;
import com.cognizant.samples.account_instructions.StandardFaultType;
import com.cognizant.samples.ai.ApplicationException;
import com.cognizant.samples.ai.SoapFaultDetailInfo;
import com.cognizant.samples.ai.instructions.endpoint.AccountInstructionEndpoint;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.server.endpoint.AbstractSoapFaultDefinitionExceptionResolver;
import org.springframework.ws.soap.server.endpoint.SoapFaultAnnotationExceptionResolver;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.lang.reflect.Method;

public class SOAPFaultCustomizer extends AbstractSoapFaultDefinitionExceptionResolver {

    private static final String NAMESPACE = AccountInstructionEndpoint.NAMESPACE;

    private static final QName DETAILS = new QName(NAMESPACE, "details");
    private static final QName CODE = new QName(NAMESPACE, "code");
    private static final QName DESCRIPTION = new QName(NAMESPACE, "message");

    private Environment environment;

    private String genericMessage;

    public SOAPFaultCustomizer(Environment env) {
        this.environment = env;
    }

    @Override
    protected SoapFaultDefinition getFaultDefinition(Object endpoint, Exception ex) {
        // Copied & modified from Spring's SoapFaultAnnotationExceptionResolver.
        org.springframework.ws.soap.server.endpoint.annotation.SoapFault faultAnnotation = ex.getClass().getAnnotation(org.springframework.ws.soap.server.endpoint.annotation.SoapFault.class);
        if (faultAnnotation != null) {
            SoapFaultDefinition definition = new SoapFaultDefinition();
            if (faultAnnotation.faultCode() != FaultCode.CUSTOM) {
                definition.setFaultCode(faultAnnotation.faultCode().value());
            } else if (isEmpty(faultAnnotation.customFaultCode())) {
                definition.setFaultCode(QName.valueOf(faultAnnotation.customFaultCode()));
            }
            // The standard SoapFaultAnnotationExceptionResolver does not allow parametization/substituion of the
            // fault reason. The convertEmptyToGeneric handles the substitution of a generic message as well as
            // resolves property parameters.
            definition.setFaultStringOrReason(convertEmptyToGeneric(faultAnnotation.faultStringOrReason()));
            definition.setLocale(StringUtils.parseLocaleString(faultAnnotation.locale()));
            return definition;
        } else {
            // Default case is to return a Generic SOAP:Server fault.
            SoapFaultDefinition definition = new SoapFaultDefinition();
            definition.setFaultCode(FaultCode.SERVER.value());
            definition.setFaultStringOrReason(getGenericMessage());
            return definition;
        }
    }

    private String convertEmptyToGeneric(String s) {
        if (isEmpty(s)) {
            s = environment.resolvePlaceholders(getGenericMessage());
        } else {
            s = environment.resolvePlaceholders(s);
        }
        return s;
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        logger.warn(String.format("Processing exception %s:%s", ex.getClass().getName(), ex.getMessage()));


        if (ex instanceof ApplicationException) {
            ApplicationException appEx = (ApplicationException) ex;
            SoapFaultDetail detail = fault.addFaultDetail();
            try {
                ObjectFactory factory = new ObjectFactory();
                Marshaller m = JAXBContext.newInstance(StandardFaultType.class).createMarshaller();
                StandardFaultType fb = factory.createStandardFaultType();
                fb.setCode(appEx.code());
                fb.setMessage(appEx.description());
                JAXBElement<StandardFaultType> element = new JAXBElement(getFaultQName(endpoint), StandardFaultType.class, null, fb);
                m.marshal(element, detail.getResult());
            } catch (JAXBException jex) {
                logger.error("Error converting to details", jex);
            }
        }
    }

    /**
     * Gets the fault's QName for the method.
     * This looksup a custom annotation ${@see SoapFaultDetailInfo} to determine the fault detail's QName.
     *
     * @param endpoint
     * @return
     */
    private QName getFaultQName(Object endpoint) {
        if (endpoint instanceof MethodEndpoint) {
            MethodEndpoint me = (MethodEndpoint) endpoint;
            SoapFaultDetailInfo fi = me.getMethod().getAnnotation(SoapFaultDetailInfo.class);
            if (fi == null) {
                return new QName("");
            }
            return new QName(fi.namespace(), fi.localPart());
        }
        return new QName("");
    }


    public void setGenericMessage(String genericMessage) {
        this.genericMessage = genericMessage;
    }

    public String getGenericMessage() {
        return genericMessage;
    }
}