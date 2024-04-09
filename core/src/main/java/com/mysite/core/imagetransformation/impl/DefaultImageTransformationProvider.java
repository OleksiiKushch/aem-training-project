package com.mysite.core.imagetransformation.impl;

import com.mysite.core.imagetransformation.ImageTransformationProvider;
import com.mysite.core.imagetransformation.transformer.ImageTransformerChain;
import com.mysite.core.imagetransformation.transformer.impl.GrayscaleImageTransformer;
import com.mysite.core.imagetransformation.transformer.impl.UpsideDownImageTransformer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component(service = ImageTransformationProvider.class)
@Designate(ocd = DefaultImageTransformationProviderConfig.class)
public class DefaultImageTransformationProvider implements ImageTransformationProvider {

    private static final String UNKNOWN_TRANSFORMATION_LOG_MSG = "Unknown transformation: {}";
    private static final String ACTIVATED_TRANSFORMATIONS_LOG_MSG = "Activated with transformations: {}";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Set<String> transformationNames;

    @Activate
    @Modified
    public void activate(DefaultImageTransformationProviderConfig config) {
        this.transformationNames = new HashSet<>(Arrays.asList(config.transformations()));
        log.debug(ACTIVATED_TRANSFORMATIONS_LOG_MSG, transformationNames);
    }

    @Override
    public ImageTransformerChain getTransformer() {
        return formTransformer();
    }

    private ImageTransformerChain formTransformer() {
        ImageTransformerChain.ImageTransformerChainBuilder builder = new ImageTransformerChain.ImageTransformerChainBuilder();
        for(String transformationName : transformationNames) {
            switch(transformationName) {
                case GrayscaleImageTransformer.TRANSFORMATION_NAME:
                    builder.addTransformer(new GrayscaleImageTransformer());
                    break;
                case UpsideDownImageTransformer.TRANSFORMATION_NAME:
                    builder.addTransformer(new UpsideDownImageTransformer());
                    break;
                default:
                    log.error(UNKNOWN_TRANSFORMATION_LOG_MSG, transformationName);
            }
        }
        return builder.build();
    }
}
