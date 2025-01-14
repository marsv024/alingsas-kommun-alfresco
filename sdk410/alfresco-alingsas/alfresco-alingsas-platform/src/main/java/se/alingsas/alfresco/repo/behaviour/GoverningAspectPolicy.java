package se.alingsas.alfresco.repo.behaviour;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.alingsas.alfresco.repo.model.AkDmModel;

import java.io.Serializable;

/**
 * 
 * 
 * @author Jimmie Aleksic - Redpill Linpro AB
 * 
 */

public class GoverningAspectPolicy extends AbstractPolicy implements OnAddAspectPolicy {

  private static final Log LOG = LogFactory.getLog(GoverningAspectPolicy.class);
  private static boolean isInitialized = false;

  @Override
  public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName) {
    if (nodeRef != null && AkDmModel.ASPECT_AKDM_GOVERNING.equals(aspectTypeQName)) {
      // Get creator of document
      Serializable creator = nodeService.getProperty(nodeRef, ContentModel.PROP_CREATOR);

      // Set aspect property "akdm:governingDocResponsible"
      nodeService.setProperty(nodeRef, AkDmModel.PROP_AKDM_GOVERNING_DOC_RESPONSIBLE, creator);
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    if (!isInitialized()) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Initializing " + this.getClass().getName());
      }
      // Create behaviours
      policyComponent.bindClassBehaviour(OnAddAspectPolicy.QNAME, AkDmModel.ASPECT_AKDM_GOVERNING, new JavaBehaviour(this, "onAddAspect", NotificationFrequency.FIRST_EVENT));
    }

  }

  private Boolean isInitialized() {
    if (!isInitialized) {
      isInitialized = true;
    } else {
      return true;
    }
    return false;
  }

}
