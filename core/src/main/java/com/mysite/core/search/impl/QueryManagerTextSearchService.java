package com.mysite.core.search.impl;

import com.mysite.core.mapper.Mapper;
import com.mysite.core.models.TextSearchResult;
import com.mysite.core.models.impl.TextSearchResultImpl;
import com.mysite.core.search.TextSearchService;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.List;
import java.util.stream.Collectors;

import static com.mysite.core.search.TextSearchServiceFactory.QUERY_MANAGER_STRATEGY_NAME;

@Component(service = TextSearchService.class)
public class QueryManagerTextSearchService implements TextSearchService {

    private static final String SELECT_CLAUSE = "SELECT * FROM [nt:base] AS node";
    private static final String CONTAINS_CLAUSE_TEMPLATE = "CONTAINS(node.*, '%s')";
    private static final String IS_DESCENDANT_NODE_CLAUSE_TEMPLATE = "ISDESCENDANTNODE(node, '%s')";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private SlingRepository slingRepository;
    @Reference(target = "(component.name=com.mysite.core.mapper.impl.QueryResultToTextSearchResultMapper)")
    private Mapper<QueryResult, TextSearchResult> mapper;

    @Override
    public TextSearchResult doSearch(String text, List<String> paths) {
        if (StringUtils.isEmpty(text)) {
            return new TextSearchResultImpl();
        }

        Query query = createQuery(getQueryManager(), formSql2Query(text, paths));

        TextSearchResult result = getMapper().map(executeQuery(query));

        setAdditionalDataToResult(result, text, paths);
        result.setSearchStrategy(QUERY_MANAGER_STRATEGY_NAME);

        return result;
    }

    private QueryManager getQueryManager() {
        try {
            return getSession(getSlingRepository(), log).getWorkspace().getQueryManager();
        } catch (RepositoryException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    private String formSql2Query(String text, List<String> paths) {
        StringBuilder query = new StringBuilder(SELECT_CLAUSE);
        query.append(" WHERE ")
                .append(String.format(CONTAINS_CLAUSE_TEMPLATE, text));
        if (!paths.isEmpty()) {
            query.append(" AND (")
                    .append(collectPathsForQuery(paths))
                    .append(")");
        }
        return query.toString();
    }

    private String collectPathsForQuery(List<String> paths) {
        return paths.stream()
                .map(path -> String.format(IS_DESCENDANT_NODE_CLAUSE_TEMPLATE, path))
                .collect(Collectors.joining(" OR "));
    }

    private Query createQuery(QueryManager queryManager, String query) {
        Query result;
        try {
            result = queryManager.createQuery(query, Query.JCR_SQL2);
        } catch (RepositoryException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
        return result;
    }

    private QueryResult executeQuery(Query query) {
        QueryResult result;
        try {
            result = query.execute();
        } catch (RepositoryException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
        return result;
    }

    public SlingRepository getSlingRepository() {
        return slingRepository;
    }

    public Mapper<QueryResult, TextSearchResult> getMapper() {
        return mapper;
    }
}
