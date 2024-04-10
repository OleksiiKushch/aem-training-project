package com.mysite.core.imagetransformation.impl;

import com.mysite.core.imagetransformation.transformer.ImageTransformerChain;
import com.mysite.core.imagetransformation.transformer.ImageTransformerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultImageTransformationProviderTest {

    @Spy
    @InjectMocks
    DefaultImageTransformationProvider testInstance;

    @Mock
    DefaultImageTransformationProviderConfig config;

    ImageTransformerType[] transformations;

    @BeforeEach
    void setUp() {
        transformations = new ImageTransformerType[]{
                ImageTransformerType.GREYSCALE,
                ImageTransformerType.UPSIDE_DOWN};
    }

    @Test
    void shouldActivateConfigs() {
        when(config.transformations()).thenReturn(transformations);

        testInstance.activate(config);

        assertEquals(transformations, testInstance.getTransformationNames());
    }

    @Test
    void getTransformer() {
        when(testInstance.getTransformationNames()).thenReturn(transformations);

        ImageTransformerChain actualResult = testInstance.getTransformer();

        assertTrue(actualResult.getTransformers().contains(ImageTransformerType.GREYSCALE.getTransformer()));
        assertTrue(actualResult.getTransformers().contains(ImageTransformerType.UPSIDE_DOWN.getTransformer()));
    }
}