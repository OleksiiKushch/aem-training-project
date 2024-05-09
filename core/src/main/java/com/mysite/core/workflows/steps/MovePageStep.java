package com.mysite.core.workflows.steps;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.mysite.core.constants.MySiteConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Optional;

@Component(
        service = WorkflowProcess.class,
        immediate = true,
        property = {
                "process.label=" + "Move Page Step Label"
        }
)
public class MovePageStep implements WorkflowProcess {

    private static final String PATH_TO_MOVE_PROPERTY = "pathToMove";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        try (ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class)) {
            Optional.ofNullable(resourceResolver)
                    .map(resolver -> getPageResource(resolver, workItem))
                    .map(resource -> resource.adaptTo(Node.class))
                    .filter(this::isPresentPathToMoveProperty)
                    .map(this::getPathToMoveProperty)
                    .ifPresent(pathToMove -> movePage(pathToMove, workItem, resourceResolver));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }

    private void movePage(String pathToMove, WorkItem workItem, ResourceResolver resourceResolver) {
        try {
            resourceResolver.move(getPagePath(workItem), pathToMove);
            resourceResolver.commit();
        } catch (PersistenceException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }

    private Resource getPageResource(ResourceResolver resourceResolver, WorkItem workItem) {
        return resourceResolver.getResource(getPagePath(workItem) + MySiteConstants.SLASH + JcrConstants.JCR_CONTENT);
    }

    private String getPagePath(WorkItem workItem) {
        return workItem.getWorkflowData().getPayload().toString();
    }

    private boolean isPresentPathToMoveProperty(Node node) {
        try {
            return node.hasProperty(PATH_TO_MOVE_PROPERTY);
        } catch (RepositoryException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    private String getPathToMoveProperty(Node node) {
        try {
            return node.getProperty(PATH_TO_MOVE_PROPERTY).getString();
        } catch (RepositoryException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
}
