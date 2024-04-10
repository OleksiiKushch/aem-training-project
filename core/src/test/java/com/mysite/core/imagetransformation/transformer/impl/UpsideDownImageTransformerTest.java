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
class UpsideDownImageTransformerTest {

    private static final double UPSIDE_DOWN_ROTATION_DEGREES = 180d;

    @InjectMocks
    UpsideDownImageTransformer testInstance;

    @Mock
    Layer layer;

    @Test
    void transform() {
        doNothing().when(layer).rotate(UPSIDE_DOWN_ROTATION_DEGREES);

        Layer actualResult = testInstance.transform(layer);

        assertEquals(layer, actualResult);
    }
}