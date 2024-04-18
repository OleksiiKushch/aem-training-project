package com.mysite.core.mapper.impl;

import com.mysite.core.mapper.Mapper;
import com.mysite.core.models.TextSearchResult;
import com.mysite.core.models.impl.TextSearchResultImpl;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import java.util.ArrayList;
import java.util.List;

@Component(service = Mapper.class)
public class QueryResultToTextSearchResultMapper implements Mapper<QueryResult, TextSearchResult> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public TextSearchResult map(QueryResult source) {
        TextSearchResult target = new TextSearchResultImpl();
        List<String> rowsArray = new ArrayList<>();

        RowIterator it;
        try {
            it = source.getRows();
        } catch (RepositoryException exception) {
            log.error(exception.getMessage());
            return target;
        }

        while (it.hasNext()) {
            Row row = (Row) it.next();
            try {
                rowsArray.add(row.getPath());
            } catch (RepositoryException exception) {
                log.error(exception.getMessage());
            }
        }
        target.setTotalMatches(rowsArray.size());
        target.setResult(rowsArray);
        return target;
    }
}
