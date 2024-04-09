package com.mysite.core.imagetransformation.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface DefaultImageTransformationProviderConfig {

    @AttributeDefinition(name = "Transformations")
    String[] transformations() default {};
}
