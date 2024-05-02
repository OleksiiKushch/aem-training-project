package com.mysite.core.permissions;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import java.util.Objects;

@Component(immediate = true)
public class ModifyPermissions {

    private static final String CONTENT_SITE_FR = "/content/we-retail/fr";
    private static final String DENY_ACCESS_GROUP = "deny-access";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private SlingRepository slingRepository;

    @Activate
    protected void activate() {
        log.info("ModifyPermissions activated");
        modifyPermissions();
    }

    private void modifyPermissions() {
        Session adminSession = null;
        try {
            // Don’t use loginAdministrative in real application, it’s deprecated
            // loginAdministrative requires whitelisting starting with AEM 6.3
            // https://sling.apache.org/documentation/the-sling-engine/service-authentication.html#whitelisting-bundles-for-administrative-login
            adminSession = getSlingRepository().loginAdministrative(null);

            JackrabbitAccessControlList acl = AccessControlUtils.getAccessControlList(adminSession, CONTENT_SITE_FR);
            if (Objects.nonNull(acl)) {
                Authorizable denyAccess = ((JackrabbitSession) adminSession).getUserManager().getAuthorizable(DENY_ACCESS_GROUP);
                if (Objects.nonNull(denyAccess)) {
                    AccessControlManager accessControlManager = adminSession.getAccessControlManager();
                    Privilege[] privileges = {accessControlManager.privilegeFromName(Privilege.JCR_READ)};
                    acl.addAccessControlEntry(denyAccess.getPrincipal(), privileges);
                    accessControlManager.setPolicy(acl.getPath(), acl);
                    adminSession.save();
                }
            }
        } catch (RepositoryException exception) {
            log.error(exception.getMessage());
        } finally {
            if (Objects.nonNull(adminSession)) {
                adminSession.logout();
            }
        }
    }

    public SlingRepository getSlingRepository() {
        return slingRepository;
    }
}
