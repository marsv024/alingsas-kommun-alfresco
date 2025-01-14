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

package se.alingsas.alfresco.repo.scripts;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.extensions.webscripts.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import se.alingsas.alfresco.repo.model.AkDmModel;
import se.alingsas.alfresco.repo.model.AkWfModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Web script for creating a complete document workflow
 * 
 * @author Marcus Svensson - marcus.svensson (at) redpill-linpro.com
 * 
 */
public class CompleteDocumentStartWf extends DeclarativeWebScript implements InitializingBean {
  private static final Log LOG = LogFactory.getLog(CompleteDocumentStartWf.class);

  private NodeService nodeService;
  private SiteService siteService;
  private WorkflowService workflowService;

  @Override
  protected Map<String, Object> executeImpl(final WebScriptRequest req, final Status status, final Cache cache) {
    

    if (LOG.isTraceEnabled()) {
      LOG.trace("CompleteDocumentStartWf.executeImpl");
    }

    final Map<String, Object> model = new HashMap<String, Object>();

    // NodeRef nodeRef = new NodeRef()
    Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();

    // Parse and validate node
    NodeRef nodeRef = new NodeRef(templateVars.get("node_protocol_id"), templateVars.get("node_store_id"), templateVars.get("node_id"));

    if (!nodeService.exists(nodeRef)) {
      throw new WebScriptException("Node does not exist " + nodeRef.toString());
    }
    
    String setDocumentSecrecy = templateVars.get("secrecy");
    
    if (setDocumentSecrecy==null || !StringUtils.hasText(setDocumentSecrecy)) {
      throw new WebScriptException("Document secrecy parameter is missing");
    }
    
    // Validate secrecy status on node
    String documentSecrecy = (String) nodeService.getProperty(nodeRef, AkDmModel.PROP_AKDM_DOC_SECRECY);
    if (documentSecrecy != null && StringUtils.hasText(documentSecrecy)) {
      setDocumentSecrecy = documentSecrecy;
    }

    // Parse and validate site
    String siteId = templateVars.get("target_site");
    SiteInfo site = siteService.getSite(siteId);
    if (site == null) {
      throw new WebScriptException("Site " + siteId + " does not exist");
    }

    // Parse and validate parent node
    NodeRef parentNodeRef = new NodeRef(templateVars.get("parent_protocol_id"), templateVars.get("parent_store_id"), templateVars.get("parent_node_id"));
    if (!nodeService.exists(parentNodeRef)) {
      throw new WebScriptException("Parent node does not exist " + parentNodeRef.toString());
    }
    SiteInfo siteContained = siteService.getSite(parentNodeRef);
    if (!site.equals(siteContained)) {
      throw new WebScriptException("Parent node " + parentNodeRef.toString() + " does not exist in the site " + siteId);
    }

    // Parse and validate workflow
    String workflowId = templateVars.get("workflow");
    WorkflowDefinition workflowDefinition = workflowService.getDefinitionByName(workflowId);

    if (workflowDefinition == null) {
      throw new WebScriptException("Workflow with id " + workflowId + " does not exist");
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug("Validated all parameters, starting workflow");
    }

    //Create a new blank package for documents on the workflow
    NodeRef wfPackage = workflowService.createPackage(null);
    nodeService.addChild(wfPackage, nodeRef, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_PREFIX, "Complete document workflow files"));

    Map<QName, Serializable> workflowParameters = new HashMap<QName, Serializable>();
    workflowParameters.put(WorkflowModel.ASSOC_PACKAGE, wfPackage);
    workflowParameters.put(AkWfModel.PROP_AKWF_TARGET_SITE, site.getNodeRef());
    workflowParameters.put(AkWfModel.PROP_AKWF_TARGET_FOLDER, parentNodeRef);
    workflowParameters.put(AkWfModel.PROP_AKWF_DOCUMENT_SECRECY, setDocumentSecrecy);

    if (LOG.isTraceEnabled()) {
      LOG.trace(workflowParameters);
    }
    WorkflowPath wfPath = workflowService.startWorkflow(workflowDefinition.getId(), workflowParameters);
    String wfPathId = wfPath.getId();
    List<WorkflowTask> wfTasks = workflowService.getTasksForWorkflowPath(wfPathId);

    // throw an exception if no tasks where found on the workflow path
    if (wfTasks.size() == 0) {
      throw new WebScriptException("No tasks were found on workflow");
    }

    try {
      WorkflowTask wfStartTask = wfTasks.get(0);
      this.workflowService.endTask(wfStartTask.getId(), null);
    } catch (RuntimeException e) {
      throw new WebScriptException("Failed to start workflow", e);
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Workflow finished");
    }

    model.put("result", "ok");

    return model;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(nodeService, "nodeService must not be null");
    Assert.notNull(siteService,"siteService must not be null");
    Assert.notNull(workflowService,"workflowService must not be null");;
  }

  public void setNodeService(NodeService nodeService) {
    this.nodeService = nodeService;
  }

  public void setSiteService(SiteService siteService) {
    this.siteService = siteService;
  }

  public void setWorkflowService(WorkflowService workflowService) {
    this.workflowService = workflowService;
  }

}
