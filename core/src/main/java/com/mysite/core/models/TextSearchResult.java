package com.mysite.core.models;

import java.util.List;

public interface TextSearchResult extends TextSearch {

    long getTotalMatches();
    List<String> getResult();

    void setTotalMatches(long totalMatches);
    void setResult(List<String> result);
    void setSearchedText(String text);
    void setSearchStrategy(String searchStrategy);
    void setSearchPaths(List<String> searchPaths);
}
