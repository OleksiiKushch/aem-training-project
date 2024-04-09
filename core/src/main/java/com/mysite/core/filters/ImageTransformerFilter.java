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

import javax.imageio.ImageIO;
import javax.jcr.RepositoryException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Component(service = Filter.class)
@SlingServletFilter(scope = {SlingServletFilterScope.REQUEST},
        pattern = "/content/we-retail/.*",
        extensions = {"jpg", "jpeg", "png"},
        methods = HttpConstants.METHOD_GET)
public class ImageTransformerFilter implements Filter {

    private static final String PROCESS_IMAGE_LOG_MSG = "Process the image with the file reference: {}";

    private static final String AFTER_TRANSFORM_FORMAT = "png";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private ImageTransformationProvider imageTransformationProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        SlingHttpServletRequest request = (SlingHttpServletRequest) servletRequest;

        Image image = new Image(request.getResource());
        log.debug(PROCESS_IMAGE_LOG_MSG, image.getFileReference());
        Layer layer;
        try {
            layer = image.getLayer(true, true, true);
            getImageTransformationProvider().getTransformer()
                    .applyTransformation(layer);
        } catch (RepositoryException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        ImageIO.write(layer.getImage(), AFTER_TRANSFORM_FORMAT, servletResponse.getOutputStream());

        filterChain.doFilter(servletRequest, servletResponse);
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
