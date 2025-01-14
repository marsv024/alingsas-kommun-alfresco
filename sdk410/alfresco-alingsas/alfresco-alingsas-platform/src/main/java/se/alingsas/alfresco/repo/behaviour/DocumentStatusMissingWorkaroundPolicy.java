/*
 * Copyright (C) 2012-2013 Alingsås Kommun
 *
 * This file is part of Alfresco customizations made for Alingsås Kommun
 *
 * The Alfresco customizations made for Alingsås Kommun is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Alfresco customizations made for Alingsås Kommun is distributed in the 
 * hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the Alfresco customizations made for Alingsås Kommun. 
 * If not, see <http://www.gnu.org/licenses/>.
 */

package se.alingsas.alfresco.repo.behaviour;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import se.alingsas.alfresco.repo.model.AkDmModel;

/**
 * Policy which fills in document status if its missing when creating new
 * documents using upload. Reported Alfresco issue#15024-60871
 * 
 * @author Marcus Svensson - Redpill Linpro AB
 * 
 */
public class DocumentStatusMissingWorkaroundPolicy implements NodeServicePolicies.OnUpdateNodePolicy {

  private static final Log LOG = LogFactory.getLog(DocumentStatusMissingWorkaroundPolicy.class);
  private Behaviour onUpdateNode;

  private PolicyComponent policyComponent;
  private ServiceRegistry serviceRegistry;
  private BehaviourFilter behaviourFilter;
  private final String DOC_STATUS_WORKING = "Arbetsdokument";

  public void init() {

    this.onUpdateNode = new JavaBehaviour(this, "onUpdateNode", NotificationFrequency.TRANSACTION_COMMIT);

    this.policyComponent.bindClassBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateNode"), AkDmModel.TYPE_AKDM_DOCUMENT, this.onUpdateNode);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.alfresco.repo.node.NodeServicePolicies.OnUpdateNodePolicy#onUpdateNode
   * (org.alfresco.service.cmr.repository.NodeRef)
   */
  @Override
  public void onUpdateNode(NodeRef nodeRef) {
    if (LOG.isTraceEnabled()) {
      LOG.trace("onUpdateNode");
    }
    NodeService nodeService = serviceRegistry.getNodeService();
    if (nodeService.exists(nodeRef) && nodeService.hasAspect(nodeRef, AkDmModel.ASPECT_AKDM_COMMON)) {
      String docStatus = (String) nodeService.getProperty(nodeRef, AkDmModel.PROP_AKDM_DOC_STATUS);
      if (docStatus == null || !StringUtils.hasText(docStatus)) {
        LOG.info("Document status is missing adding it for node: " + nodeRef.toString());
        behaviourFilter.disableBehaviour(nodeRef);
        nodeService.setProperty(nodeRef, AkDmModel.PROP_AKDM_DOC_STATUS, DOC_STATUS_WORKING);
        behaviourFilter.enableBehaviour(nodeRef);
      }
    }
  }

  public void setPolicyComponent(PolicyComponent policyComponent) {
    this.policyComponent = policyComponent;
  }

  public void setServiceRegistry(ServiceRegistry serviceRegistry) {
    this.serviceRegistry = serviceRegistry;
  }

  public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
    this.behaviourFilter = behaviourFilter;
  }

}
