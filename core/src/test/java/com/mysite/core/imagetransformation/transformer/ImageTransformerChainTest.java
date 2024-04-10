package com.mysite.core.imagetransformation.transformer;

import com.day.image.Layer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageTransformerChainTest {

    @InjectMocks
    ImageTransformerChain testInstance;

    @Mock
    Layer layer;

    @Spy
    ImageTransformer imageTransformer1;
    @Spy
    ImageTransformer imageTransformer2;

    @BeforeEach
    void setUp() {
        testInstance = ImageTransformerChain.builder()
                .addTransformer(imageTransformer1)
                .addTransformer(imageTransformer2)
                .build();
    }

    @Test
    void applyTransformation() {
        when(imageTransformer1.transform(layer)).thenReturn(layer);
        when(imageTransformer2.transform(layer)).thenReturn(layer);

        Layer actualResult = testInstance.applyTransformation(layer);

        assertEquals(layer, actualResult);
    }
}