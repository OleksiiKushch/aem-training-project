package com.mysite.core.listeners;

import com.mysite.core.constants.MySiteConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import java.util.HashMap;
import java.util.Map;

@Component(
        service = EventListener.class,
        immediate = true
)
public class PropertyRemovalListener implements EventListener {

    private static final String CREATE_JOB_ERROR_LOG_MSG = "Error During Creation of Job: {}";
    private static final String TARGET_PATH = "/content/mysite";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private SlingRepository slingRepository;
    @Reference
    private JobManager jobManager;

    private ObservationManager observationManager;

    @Activate
    protected void activate(ComponentContext context) throws RepositoryException {
        Session session = getSlingRepository().loginService(MySiteConstants.MYSITE_REMOVE_PROPERTY_LISTENER_SERVICE_USER, null);
        observationManager = session.getWorkspace().getObservationManager();
        getObservationManager().addEventListener(
                this,                       // handler
                Event.PROPERTY_REMOVED,     // code for event type
                TARGET_PATH,                // path
                true,                       // is deep?
                null,                       // UUIDs filter
                null,                       // node types filter (for example: {"cq:PageContent"})
                false                       // are ignored events triggered by this session (service user, that added this listener)?
        );
        log.info(MySiteConstants.ACTIVATING_THE_AUTO_VERSIONING_EVENT_LISTENER_LOG_MSG);
    }

    @Deactivate
    private void deactivate() throws RepositoryException {
        getObservationManager().removeEventListener(this);
        log.info(MySiteConstants.DEACTIVATING_THE_AUTO_VERSIONING_EVENT_LISTENER_LOG_MSG);
    }

    @Override
    public void onEvent(EventIterator eventIterator) {
        while (eventIterator.hasNext()) {
            Event event = eventIterator.nextEvent();
            try {
                Map<String, Object> jobProperties = new HashMap<>();
                jobProperties.put(MySiteConstants.PROPERTY_PATH, event.getPath());
                jobProperties.put(MySiteConstants.PROPERTY_NAME, getPropertyName(event.getPath()));
                getJobManager().addJob(MySiteConstants.PROPERTY_REMOVAL_JOB_TOPIC, jobProperties);
            } catch (RepositoryException exception) {
                log.error(CREATE_JOB_ERROR_LOG_MSG, exception.getMessage());
            }
        }
    }

    private String getPropertyName(String path) {
        return StringUtils.substringAfterLast(path, MySiteConstants.SLASH);
    }

    public SlingRepository getSlingRepository() {
        return slingRepository;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public ObservationManager getObservationManager() {
        return observationManager;
    }
}
