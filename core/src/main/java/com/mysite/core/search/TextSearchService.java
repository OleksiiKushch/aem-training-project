package com.mysite.core.search;

import com.mysite.core.models.TextSearchResult;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.List;

import static com.mysite.core.constants.MySiteConstants.MYSITE_SERVICE_USER;

public interface TextSearchService {

    TextSearchResult doSearch(String text, List<String> paths);

    default Session getSession(SlingRepository slingRepository, Logger log) {
        try {
            return slingRepository.loginService(MYSITE_SERVICE_USER, null);
        } catch (RepositoryException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    default void setAdditionalDataToResult(TextSearchResult result, String text, List<String> paths) {
        result.setSearchedText(text);
        result.setSearchPaths(paths);
    }
}
