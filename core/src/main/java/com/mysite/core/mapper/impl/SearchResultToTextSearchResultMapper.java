package com.mysite.core.mapper.impl;

import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.mysite.core.mapper.Mapper;
import com.mysite.core.models.TextSearchResult;
import com.mysite.core.models.impl.TextSearchResultImpl;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;

@Component(service = Mapper.class)
public class SearchResultToTextSearchResultMapper implements Mapper<SearchResult, TextSearchResult> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public TextSearchResult map(SearchResult source) {
        TextSearchResult target = new TextSearchResultImpl();
        target.setTotalMatches(source.getTotalMatches());
        List<String> hitsArray = new ArrayList<>();
        for (Hit hit : source.getHits()) {
            try {
                hitsArray.add(hit.getPath());
            } catch (RepositoryException exception) {
                log.error(exception.getMessage());
            }
        }
        target.setResult(hitsArray);
        return target;
    }
}
