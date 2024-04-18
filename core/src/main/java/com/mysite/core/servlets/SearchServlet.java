package com.mysite.core.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.core.models.TextSearch;
import com.mysite.core.models.TextSearchResult;
import com.mysite.core.search.TextSearchServiceFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Optional;

import static org.eclipse.jetty.http.MimeTypes.Type.APPLICATION_JSON;

@Component(service = Servlet.class)
@SlingServletPaths(
        value = "/bin/search"
)
public class SearchServlet extends SlingAllMethodsServlet {

    private static final String RESOURCE_PATH_PARAM = "resourcePath";
    private static final String ERROR_PARAM = "error";

    private static final String ERROR_MSG = "Resource not found or failed to adapt to model at path: ";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private TextSearchServiceFactory textSearchServiceFactory;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String resourcePath = request.getParameter(RESOURCE_PATH_PARAM);

        ResourceResolver resourceResolver = request.getResourceResolver();

        String result = Optional.ofNullable(resourceResolver.getResource(resourcePath))
                .map(resource -> resource.adaptTo(TextSearch.class))
                .map(searchModel -> getTextSearchServiceFactory().getTextSearch(searchModel.getSearchStrategy())
                        .doSearch(searchModel.getSearchedText(), searchModel.getSearchPaths()))
                .map(this::mapToJsonString)
                .orElseGet(() -> createErrorObject(resourcePath));

        response.setContentType(APPLICATION_JSON.toString());
        response.getWriter().write(result);
    }

    private String mapToJsonString(TextSearchResult textSearchResult) {
        String result;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            result = objectMapper.writeValueAsString(textSearchResult);
        } catch (JsonProcessingException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
        return result;
    }

    private String createErrorObject(Object... messageArguments) {
        JSONObject errorObject = new JSONObject();
        try {
            errorObject.put(ERROR_PARAM, String.format(ERROR_MSG, messageArguments));
        } catch (JSONException exception) {
            log.error(exception.getMessage());
        }
        return errorObject.toString();
    }

    public TextSearchServiceFactory getTextSearchServiceFactory() {
        return textSearchServiceFactory;
    }
}
