package com.mysite.core.search;

public interface TextSearchServiceFactory {

    String QUERY_BUILDER_STRATEGY_NAME = "queryBuilder";
    String QUERY_MANAGER_STRATEGY_NAME = "queryManager";

    TextSearchService getTextSearch(String searchStrategy);
}
