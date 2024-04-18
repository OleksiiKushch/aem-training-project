package com.mysite.core.models;

import com.adobe.cq.wcm.core.components.models.Component;

import java.util.List;

public interface TextSearch extends Component {

    String PATHS_NODE_NAME = "paths";
    String PN_PATH = "path";

    String getSearchedText();
    String getSearchStrategy();
    List<String> getSearchPaths();
}
