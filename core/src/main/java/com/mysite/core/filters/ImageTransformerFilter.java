package com.mysite.core.filters;

import com.day.cq.wcm.foundation.Image;
import com.day.image.Layer;
import com.mysite.core.imagetransformation.ImageTransformationProvider;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.servlets.annotations.SlingServletFilter;
import org.apache.sling.servlets.annotations.SlingServletFilterScope;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component(service = Filter.class)
@SlingServletFilter(scope = {SlingServletFilterScope.REQUEST},
        pattern = "/content/we-retail/.*",
        extensions = {"jpg", "jpeg", "png"},
        methods = HttpConstants.METHOD_GET)
public class ImageTransformerFilter implements Filter {

    private static final String PROCESS_IMAGE_LOG_MSG = "Process the image with the file reference: {}";

    private static final String MIME_TYPE_PREFIX = "image/";
    private static final double MAX_QUALITY = 1.0d;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private ImageTransformationProvider imageTransformationProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        SlingHttpServletRequest request = (SlingHttpServletRequest) servletRequest;

        Image image = getImage(request);
        Layer layer = getLayer(image);

        if (Objects.nonNull(layer)) {
            log.debug(PROCESS_IMAGE_LOG_MSG, image.getFileReference());
            getImageTransformationProvider().getTransformer()
                    .applyTransformation(layer);

            layer.write(getMimeType(request), MAX_QUALITY, servletResponse.getOutputStream());
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    protected Image getImage(SlingHttpServletRequest request) {
        return new Image(request.getResource());
    }

    private Layer getLayer(Image image) {
        try {
            return image.getLayer(true, true, true);
        } catch (RepositoryException | IOException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    private String getMimeType(SlingHttpServletRequest request) {
        return MIME_TYPE_PREFIX + request.getRequestPathInfo().getExtension();
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    public ImageTransformationProvider getImageTransformationProvider() {
        return imageTransformationProvider;
    }
}
