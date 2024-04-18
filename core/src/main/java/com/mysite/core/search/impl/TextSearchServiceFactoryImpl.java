package com.mysite.core.search.impl;

import com.mysite.core.search.TextSearchService;
import com.mysite.core.search.TextSearchServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component(service = TextSearchServiceFactory.class)
public class TextSearchServiceFactoryImpl implements TextSearchServiceFactory {

    private static final String ERROR_MSG = "Invalid search strategy: '%s'";

    private final Map<String, TextSearchService> textSearchStrategies;

    @Reference(target = "(component.name=com.mysite.core.search.impl.QueryManagerTextSearchService)")
    private TextSearchService queryManagerTextSearchService;
    @Reference(target = "(component.name=com.mysite.core.search.impl.QueryBuilderTextSearchService)")
    private TextSearchService queryBuilderTextSearchService;

    public TextSearchServiceFactoryImpl() {
        textSearchStrategies = new HashMap<>();
    }

    @Activate
    public void activate() {
        textSearchStrategies.put(QUERY_MANAGER_STRATEGY_NAME, queryManagerTextSearchService);
        textSearchStrategies.put(QUERY_BUILDER_STRATEGY_NAME, queryBuilderTextSearchService);
    }

    @Override
    public TextSearchService getTextSearch(String searchStrategy) {
        return Optional.ofNullable(getTextSearchStrategies().get(searchStrategy))
                .orElseThrow(() -> new IllegalArgumentException(formErrorMessage(searchStrategy)));
    }

    private String formErrorMessage(Object... messageArguments) {
        return String.format(ERROR_MSG, messageArguments);
    }

    public Map<String, TextSearchService> getTextSearchStrategies() {
        return textSearchStrategies;
    }
}
