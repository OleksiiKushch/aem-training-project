package com.mysite.core.imagetransformation.transformer.impl;

import com.day.image.Layer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class GrayscaleImageTransformerTest {

    @InjectMocks
    GrayscaleImageTransformer testInstance;

    @Mock
    Layer layer;

    @Test
    void transform() {
        doNothing().when(layer).grayscale();

        Layer actualResult = testInstance.transform(layer);

        assertEquals(layer, actualResult);
    }
}