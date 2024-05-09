package com.mysite.core.models.impl;

import com.mysite.core.models.TextSearchResult;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import java.util.List;

@Model(
        adaptables = Resource.class,
        adapters = TextSearchResult.class
)
public class TextSearchResultImpl implements TextSearchResult {

    private String searchedText;
    private String searchStrategy;
    private List<String> searchPaths;
    private long totalMatches;
    private List<String> result;

    @Override
    public long getTotalMatches() {
        return totalMatches;
    }

    @Override
    public List<String> getResult() {
        return result;
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

    @Override
    public void setTotalMatches(long totalMatches) {
        this.totalMatches = totalMatches;
    }

    @Override
    public void setResult(List<String> result) {
        this.result = result;
    }

    @Override
    public void setSearchedText(String text) {
        this.searchedText = text;
    }

    @Override
    public void setSearchStrategy(String searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

    @Override
    public void setSearchPaths(List<String> searchPaths) {
        this.searchPaths = searchPaths;
    }
}
