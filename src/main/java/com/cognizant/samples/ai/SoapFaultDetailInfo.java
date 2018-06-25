package com.cognizant.samples.ai;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates the SOAP Fault's detail namespace for an endpoint. This processing
 * is not part of standard spring.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SoapFaultDetailInfo {
    String namespace();
    String localPart();
}
