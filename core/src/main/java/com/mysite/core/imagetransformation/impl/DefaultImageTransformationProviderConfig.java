package com.mysite.core.imagetransformation.impl;

import com.mysite.core.imagetransformation.transformer.ImageTransformerType;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface DefaultImageTransformationProviderConfig {

    @AttributeDefinition(name = "Transformations")
    ImageTransformerType[] transformations() default {};
}
