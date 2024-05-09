package com.mysite.core.search;

import com.mysite.core.models.TextSearchResult;

import javax.jcr.Session;
import java.util.List;

public interface TextSearchService {

    TextSearchResult doSearch(String text, List<String> paths, Session session);

    default void setAdditionalDataToResult(TextSearchResult result, String text, List<String> paths) {
        result.setSearchedText(text);
        result.setSearchPaths(paths);
    }
}
