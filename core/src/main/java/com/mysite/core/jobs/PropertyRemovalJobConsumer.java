package com.mysite.core.jobs;

import com.day.cq.commons.jcr.JcrConstants;
import com.mysite.core.constants.MySiteConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component(
        service = JobConsumer.class,
        immediate = true,
        property = {
                JobConsumer.PROPERTY_TOPICS + "=" + MySiteConstants.PROPERTY_REMOVAL_JOB_TOPIC
        }
)
public class PropertyRemovalJobConsumer implements JobConsumer {

    private static final String TARGET_FOLDER = "/var/log/removedProperties";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public JobResult process(Job job) {
        String path = (String) job.getProperty(MySiteConstants.PROPERTY_PATH);
        String name = (String) job.getProperty(MySiteConstants.PROPERTY_NAME);

        ResourceResolver resourceResolver = getResourceResolver();

        Resource resource = resourceResolver.getResource(TARGET_FOLDER);
        if (Objects.nonNull(resource)) {
            Node folderNode = resource.adaptTo(Node.class);
            if (Objects.nonNull(folderNode)) {
                try {
                    Node propertyNode = folderNode.addNode(generateUniqueNodeName(name), JcrConstants.NT_UNSTRUCTURED);
                    propertyNode.setProperty(MySiteConstants.PROPERTY_PATH, path);
                    propertyNode.setProperty(MySiteConstants.PROPERTY_NAME, name);
                } catch (RepositoryException exception) {
                    log.error(exception.getMessage());
                    throw new RuntimeException(exception);
                }
            }

            try {
                resourceResolver.commit();
            } catch (PersistenceException exception) {
                log.error(exception.getMessage());
                throw new RuntimeException(exception);
            }
        }

        return JobResult.OK;
    }

    private String generateUniqueNodeName(String name) {
        return name + MySiteConstants.UNDERSCORE + UUID.randomUUID();
    }

    private Map<String, Object> getServiceUserParam() {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, MySiteConstants.MYSITE_REMOVE_PROPERTY_LISTENER_SERVICE_USER);
        return param;
    }

    private ResourceResolver getResourceResolver() {
        try {
            return getResourceResolverFactory().getServiceResourceResolver(getServiceUserParam());
        } catch (LoginException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    public ResourceResolverFactory getResourceResolverFactory() {
        return resourceResolverFactory;
    }
}
