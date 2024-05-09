package com.mysite.core.versioning;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;
import com.day.cq.wcm.api.WCMException;
import com.drew.lang.annotations.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.mysite.core.constants.MySiteConstants.MYSITE_AUTO_VERSIONING_SERVICE_USER;

@Component(
        service = ResourceChangeListener.class,
        immediate = true,
        property= {
                ResourceChangeListener.PATHS + "=/content/mysite",
                ResourceChangeListener.CHANGES + "=ADDED",
                ResourceChangeListener.CHANGES + "=CHANGED",
                ResourceChangeListener.CHANGES + "=REMOVED"
        }
)
public class AutoVersioningEventListener implements ResourceChangeListener {

    private static final String VERSION_LABLE = "AutoCreated";
    private static final String VERSION_COMMENT = null;
    private static final String MYSITE_PATH = "/content/mysite/";
    private static final int FIRST = 0;
    private static final String SLASH = "/";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    @Reference
    private PageManagerFactory pageManagerFactory;

    @Override
    public void onChange(@NotNull List<ResourceChange> list) {
        String path = ifChangeTypeIsRemovedReturnParrentPath(list.get(FIRST));
        if (isNotJcrContent(path)) {
            try (ResourceResolver resourceResolver = getResourceResolverFactory().getServiceResourceResolver(getServiceUserParam())) {
                findPageResource(path, resourceResolver)
                        .filter(this::isPageDirectUnderTargetPath)
                        .filter(this::isPageHasDescription)
                        .ifPresent(resource -> createRevision(resourceResolver, resource));
            } catch (LoginException exception) {
                log.error(exception.getMessage());
                throw new RuntimeException(exception);
            }
        }
    }

    private Optional<Resource> findPageResource(String path, ResourceResolver resourceResolver) {
        Resource resource = resourceResolver.getResource(path);
        while (Objects.nonNull(resource) && isNotPage(resource)) {
            resource = resource.getParent();
        }
        return Optional.ofNullable(resource);
    }

    private void createRevision(ResourceResolver resourceResolver, Resource resource) {
        Session session = resourceResolver.adaptTo(Session.class);
        if (Objects.nonNull(session)) {
            PageManager pageManager = getPageManager(resourceResolver);
            try {
                pageManager.createRevision(pageManager.getPage(resource.getPath()), VERSION_LABLE, VERSION_COMMENT);
            } catch (WCMException exception) {
                log.error(exception.getMessage());
                throw new RuntimeException(exception);
            }
        }
    }

    private PageManager getPageManager(ResourceResolver resourceResolver) {
        return getPageManagerFactory().getPageManager(resourceResolver);
    }

    private String ifChangeTypeIsRemovedReturnParrentPath(ResourceChange resourceChange) {
        String result = resourceChange.getPath();
        if (ResourceChange.ChangeType.REMOVED.equals(resourceChange.getType())) {
            result = StringUtils.substringBeforeLast(result, SLASH);
        }
        return result;
    }

    private boolean isNotJcrContent(String path) {
        return !path.endsWith(NameConstants.NN_CONTENT);
    }

    private boolean isNotPage(Resource resource) {
        return !resource.isResourceType(NameConstants.NT_PAGE);
    }

    private boolean isPageDirectUnderTargetPath(Resource resource) {
        return resource.getPath().startsWith(MYSITE_PATH + resource.getName());
    }

    private boolean isPageHasDescription(Resource resource) {
        return Optional.ofNullable(resource.getChild(NameConstants.NN_CONTENT))
                .filter(jcrContent -> StringUtils.isNotEmpty(getDescription(jcrContent)))
                .isPresent();
    }

    private String getDescription(Resource jcrContent) {
        return jcrContent.getValueMap().get(NameConstants.PN_DESCRIPTION, String.class);
    }

    private Map<String, Object> getServiceUserParam() {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, MYSITE_AUTO_VERSIONING_SERVICE_USER);
        return param;
    }

    public ResourceResolverFactory getResourceResolverFactory() {
        return resourceResolverFactory;
    }

    public PageManagerFactory getPageManagerFactory() {
        return pageManagerFactory;
    }
}
