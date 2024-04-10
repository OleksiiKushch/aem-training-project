package com.mysite.core.imagetransformation.transformer;

import com.mysite.core.imagetransformation.transformer.impl.GrayscaleImageTransformer;
import com.mysite.core.imagetransformation.transformer.impl.UpsideDownImageTransformer;

public enum ImageTransformerType {

    GREYSCALE(new GrayscaleImageTransformer()),
    UPSIDE_DOWN(new UpsideDownImageTransformer());

    private final ImageTransformer transformer;

    ImageTransformerType(ImageTransformer transformer) {
        this.transformer = transformer;
    }

    public ImageTransformer getTransformer() {
        return transformer;
    }
}
