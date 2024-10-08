package com.mysite.core.imagetransformation.transformer.impl;

import com.day.image.Layer;
import com.mysite.core.imagetransformation.transformer.ImageTransformer;

/**
 * Transformer that grayscale the image
 */
public class GrayscaleImageTransformer implements ImageTransformer {

    @Override
    public Layer transform(Layer layer) {
        layer.grayscale();
        return layer;
    }
}
