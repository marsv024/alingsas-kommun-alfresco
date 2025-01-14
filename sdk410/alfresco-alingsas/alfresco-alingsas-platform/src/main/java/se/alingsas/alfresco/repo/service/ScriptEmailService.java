package se.alingsas.alfresco.repo.service;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.util.GUID;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Used as bridge between Javascript and Java.
 * Can be called in JS by: archiveService.METHOD
 *
 * @author Anton Häägg - Redpill Linpro AB
 */
public class ScriptEmailService extends BaseProcessorExtension implements InitializingBean {
    private static final Log LOG = LogFactory.getLog(ScriptEmailService.class);

    protected NodeService nodeService;
    protected PersonService personService;

    public boolean validateEmailExistance(String userName) {
        return AuthenticationUtil.runAsSystem(() -> {
            final NodeRef requestedUser = personService.getPersonOrNull(userName);

            if (requestedUser == null) {
                return false;
            }

            if (!nodeService.exists(requestedUser)) {
                return false;
            }
            String email = (String) nodeService.getProperty(requestedUser, ContentModel.PROP_EMAIL);

            if (StringUtils.isEmpty(email)) {
                String newEmail = "default" + "@" + GUID.generate() + ".com";
                LOG.info("No email found for user " + userName + " setting a generated email: " + newEmail);
                nodeService.setProperty(requestedUser, ContentModel.PROP_EMAIL, newEmail);
            }

            return true;
        });
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(nodeService, "nodeService is null");
        Assert.notNull(personService, "personService is null");
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }
}
