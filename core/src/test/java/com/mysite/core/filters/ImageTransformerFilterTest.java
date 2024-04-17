package com.mysite.core.filters;

import com.day.cq.wcm.foundation.Image;
import com.day.image.Layer;
import com.mysite.core.imagetransformation.ImageTransformationProvider;
import com.mysite.core.imagetransformation.transformer.ImageTransformerChain;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.RepositoryException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageTransformerFilterTest {

    private static final String MIME_TYPE_PREFIX = "image/";
    private static final double MAX_QUALITY = 1.0d;

    private static final String TEST_RESOURCE_EXTENSION = "jpg";

    private static final String EXPECTED_MIME_TYPE = MIME_TYPE_PREFIX + TEST_RESOURCE_EXTENSION;

    @Spy
    @InjectMocks
    ImageTransformerFilter testInstance;

    @Mock
    ImageTransformationProvider imageTransformationProvider;

    @Mock
    SlingHttpServletRequest request;
    @Mock
    SlingHttpServletResponse response;
    @Mock
    FilterChain filterChain;

    @Mock
    Image image;
    @Mock
    Layer layer;
    @Mock
    ImageTransformerChain imageTransformerChain;
    @Mock
    RequestPathInfo requestPathInfo;
    @Mock
    ServletOutputStream outputStream;

    @BeforeEach
    void setUp() {
        doReturn(image).when(testInstance).getImage(request);
    }

    @Test
    void doFilter() throws ServletException, IOException, RepositoryException {
        when(image.getLayer(true, true, true)).thenReturn(layer);
        when(imageTransformationProvider.getTransformer()).thenReturn(imageTransformerChain);
        when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
        when(requestPathInfo.getExtension()).thenReturn(TEST_RESOURCE_EXTENSION);
        when(response.getOutputStream()).thenReturn(outputStream);
        doReturn(true).when(layer).write(EXPECTED_MIME_TYPE, MAX_QUALITY, outputStream);

        testInstance.doFilter(request, response, filterChain);

        verify(imageTransformerChain).applyTransformation(layer);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotTransformImage_when() throws ServletException, IOException, RepositoryException {
        when(image.getLayer(true, true, true)).thenReturn(null);

        testInstance.doFilter(request, response, filterChain);

        verify(imageTransformationProvider, never()).getTransformer();
        verify(request, never()).getRequestPathInfo();
        verify(requestPathInfo, never()).getExtension();
        verify(response, never()).getOutputStream();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldThrowException_whenCannotCreateLayer() throws IOException, RepositoryException {
        when(image.getLayer(true, true, true)).thenThrow(RepositoryException.class);

        assertThrows(RuntimeException.class, () ->
                testInstance.doFilter(request, response, filterChain)
        );
    }
}