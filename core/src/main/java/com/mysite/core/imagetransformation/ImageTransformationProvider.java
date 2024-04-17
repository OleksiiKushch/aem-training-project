package com.mysite.core.imagetransformation;

import com.mysite.core.imagetransformation.transformer.ImageTransformerChain;

public interface ImageTransformationProvider {

    ImageTransformerChain getTransformer();
}
