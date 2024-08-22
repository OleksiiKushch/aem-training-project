function check() {
    let pageContentNodePath = workflowData.getPayload() + "/jcr:content";
    if (!jcrSession.nodeExists(pageContentNodePath)) {
        log.info("jcr:content node does not exists");
        return false;
    }

    let pageContent = jcrSession.getNode(pageContentNodePath);
    if (!pageContent.hasProperty("pathToMove")) {
        log.info("pathToMove is NOT present");
        return true;
    }
    return false;
}

function check() {
    let pageContentNodePath = workflowData.getPayload() + "/jcr:content";
    if (!jcrSession.nodeExists(pageContentNodePath)) {
        log.info("jcr:content node does not exists");
        return false;
    }

    let pageContent = jcrSession.getNode(pageContentNodePath);
    return pageContent.hasProperty("pathToMove");
}

function check() {
    let pagePath = workflowData.getPayload();
    let pageContentNodePath = pagePath + "/jcr:content";
    if (!jcrSession.nodeExists(pageContentNodePath)) {
        log.info("jcr:content node does not exists");
        return false;
    }

    let pageContent = jcrSession.getNode(pageContentNodePath);
    if (pageContent.hasProperty("pathToMove")) {
        let pathToMove = pageContent.getProperty("pathToMove").toString();
        if(pathToMove == "") {
            log.info("pathToMove is empty");
            return false;
        }

        let pagePathWithoutPageName = pagePath.substring(0, pagePath.lastIndexOf("/"));
        if(pathToMove == pagePathWithoutPageName) {
            log.info("pathToMove is the same as the current page path");
            return false;
        }

        if(!jcrSession.nodeExists(pathToMove)) {
            log.info("Path '" + pathToMove + "' does not exist in the repository");
            return false;
        }

        return true;
    }

    log.info("pathToMove is not present");
    return false;
}

function check() {
    let pagePath = workflowData.getPayload();
    let pageContentNodePath = pagePath + "/jcr:content";
    if (!jcrSession.nodeExists(pageContentNodePath)) {
        log.info("jcr:content node does not exists");
        return false;
    }

    let pageContent = jcrSession.getNode(pageContentNodePath);
    if (pageContent.hasProperty("pathToMove")) {
        let pathToMove = pageContent.getProperty("pathToMove").toString();

        if(pathToMove == "") {
            return true;
        }

        let pagePathWithoutPageName = pagePath.substring(0, pagePath.lastIndexOf("/"));
        if(pathToMove == pagePathWithoutPageName) {
            return true;
        }

        return !jcrSession.nodeExists(pathToMove);
    }

    return true;
}