package com.mysite.core.search.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.mysite.core.mapper.Mapper;
import com.mysite.core.models.TextSearchResult;
import com.mysite.core.models.impl.TextSearchResultImpl;
import com.mysite.core.search.TextSearchService;
import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.mysite.core.search.TextSearchServiceFactory.QUERY_BUILDER_STRATEGY_NAME;

@Component(service = TextSearchService.class)
public class QueryBuilderTextSearchService implements TextSearchService {

    private static final String GROUP_OR_PROPERTY = "group.p.or";
    private static final String FULLTEXT_PROPERTY = "fulltext";
    private static final String PATH_PROPERTY = "path";
    private static final String GROUP_PATH_PROPERTY_PATTERN = "group.%d_%s";

    @Reference
    private QueryBuilder builder;
    @Reference(target = "(component.name=com.mysite.core.mapper.impl.SearchResultToTextSearchResultMapper)")
    private Mapper<SearchResult, TextSearchResult> mapper;

    @Override
    public TextSearchResult doSearch(String text, List<String> paths, Session session) {
        if (StringUtils.isEmpty(text)) {
            return new TextSearchResultImpl();
        }

        Query query = getBuilder().createQuery(getPredicateGroup(text, paths), session);

        TextSearchResult result = getMapper().map(query.getResult());

        setAdditionalDataToResult(result, text, paths);
        result.setSearchStrategy(QUERY_BUILDER_STRATEGY_NAME);

        return result;
    }

    private PredicateGroup getPredicateGroup(String fulltextSearchTerm, List<String> paths) {
        Map<String, Object> map = new HashMap<>();

        map.put(FULLTEXT_PROPERTY, fulltextSearchTerm);

        map.put(GROUP_OR_PROPERTY, Boolean.TRUE.toString());

        IntStream.range(0, paths.size()).forEach(i ->
                map.put(String.format(GROUP_PATH_PROPERTY_PATTERN, i + 1, PATH_PROPERTY), paths.get(i))
        );

        return PredicateGroup.create(map);
    }

    public QueryBuilder getBuilder() {
        return builder;
    }

    public Mapper<SearchResult, TextSearchResult> getMapper() {
        return mapper;
    }
}
