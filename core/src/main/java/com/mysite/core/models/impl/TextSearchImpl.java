package com.mysite.core.models.impl;

import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.mysite.core.models.TextSearch;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Model(
        adaptables = {SlingHttpServletRequest.class, Resource.class},
        adapters = TextSearch.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class TextSearchImpl extends AbstractComponentImpl implements TextSearch {

    @ValueMapValue
    private String searchedText;

    @ValueMapValue
    private String searchStrategy;

    private List<String> searchPaths;

    @PostConstruct
    protected void init() {
        searchPaths = Optional.ofNullable(resource.getChild(PATHS_NODE_NAME))
                .map(Resource::getChildren)
                .map(children -> StreamSupport.stream(children.spliterator(), false))
                .orElseGet(Stream::empty)
                .map(r -> r.getValueMap().get(PN_PATH, String.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public String getSearchedText() {
        return searchedText;
    }

    @Override
    public String getSearchStrategy() {
        return searchStrategy;
    }

    @Override
    public List<String> getSearchPaths() {
        return searchPaths;
    }
}
