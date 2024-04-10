package com.mysite.core.imagetransformation.impl;

import com.mysite.core.imagetransformation.ImageTransformationProvider;
import com.mysite.core.imagetransformation.transformer.ImageTransformerChain;
import com.mysite.core.imagetransformation.transformer.ImageTransformerType;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = ImageTransformationProvider.class)
@Designate(ocd = DefaultImageTransformationProviderConfig.class)
public class DefaultImageTransformationProvider implements ImageTransformationProvider {

    private ImageTransformerType[] transformationNames;

    @Activate
    @Modified
    public void activate(DefaultImageTransformationProviderConfig config) {
        this.transformationNames = config.transformations();
    }

    @Override
    public ImageTransformerChain getTransformer() {
        return formTransformer();
    }

    private ImageTransformerChain formTransformer() {
        var builder = ImageTransformerChain.builder();
        for(ImageTransformerType transformation : getTransformationNames()) {
            switch(transformation) {
                case GREYSCALE:
                    builder.addTransformer(ImageTransformerType.GREYSCALE.getTransformer());
                    break;
                case UPSIDE_DOWN:
                    builder.addTransformer(ImageTransformerType.UPSIDE_DOWN.getTransformer());
                    break;
            }
        }
        return builder.build();
    }

    public ImageTransformerType[] getTransformationNames() {
        return transformationNames;
    }
}
