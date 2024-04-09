package com.mysite.core.imagetransformation.transformer.impl;

import com.day.image.Layer;
import com.mysite.core.imagetransformation.transformer.ImageTransformer;

/**
 * Transformer that turn the image upside-down
 */
public class UpsideDownImageTransformer implements ImageTransformer {

    private static final double ROTATION_DEGREES = 180d;

    public static final String TRANSFORMATION_NAME = "upside-down";

    @Override
    public Layer transform(Layer layer) {
        layer.rotate(ROTATION_DEGREES);
        return layer;
    }
}
