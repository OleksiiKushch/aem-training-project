package com.mysite.core.imagetransformation.transformer;

import com.day.image.Layer;

import java.util.ArrayList;
import java.util.List;

public class ImageTransformerChain {

    private final List<ImageTransformer> transformers;

    private ImageTransformerChain(List<ImageTransformer> transformers) {
        this.transformers = transformers;
    }

    public Layer applyTransformation(Layer layer) {
        for (ImageTransformer transformer : transformers) {
            layer = transformer.transform(layer);
        }
        return layer;
    }

    public static class ImageTransformerChainBuilder {

        private final List<ImageTransformer> transformers;

        public ImageTransformerChainBuilder() {
            transformers = new ArrayList<>();
        }

        public ImageTransformerChainBuilder addTransformer(ImageTransformer transformer) {
            transformers.add(transformer);
            return this;
        }

        public ImageTransformerChain build() {
            return new ImageTransformerChain(transformers);
        }
    }
}
