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

package se.alingsas.alfresco.repo.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import se.alingsas.alfresco.repo.model.AkDmModel;
import se.alingsas.alfresco.repo.workflow.model.CommonWorkflowModel;
import se.alingsas.alfresco.repo.workflow.model.CompleteDocumentWorkflowModel;

import java.util.List;
import java.util.Set;

public class CompleteDocumentExecutionStartListener implements ExecutionListener {
  private static final Log LOG = LogFactory.getLog(CompleteDocumentExecutionStartListener.class);

  /**
   * CompleteDocumentStartTaskListener which will prepare the workflow with data
   */
  @Override
  public void notify(DelegateExecution execution) {
    final String akwfInitiator = AuthenticationUtil.getFullyAuthenticatedUser();
    LOG.debug("Entering CompleteDocumentStartTaskListener");

    LOG.debug("Authenticated user is " + akwfInitiator);

    final ServiceRegistry serviceRegistry = WorkflowUtil.getServiceRegistry();
    final LockService lockService = serviceRegistry.getLockService();
    final PermissionService permissionService = serviceRegistry.getPermissionService();
    final NodeService nodeService = serviceRegistry.getNodeService();
    final SiteService siteService = serviceRegistry.getSiteService();
    final AuthorityService authorityService = serviceRegistry.getAuthorityService();
    execution.setVariable(CommonWorkflowModel.INITIATOR, akwfInitiator);
    LOG.info("Starting complete document workflow for user " + AuthenticationUtil.getFullyAuthenticatedUser());
    ActivitiScriptNode akwfTargetFolder = (ActivitiScriptNode) execution.getVariable(CommonWorkflowModel.TARGET_FOLDER);
    ActivitiScriptNode akwfTargetSite = (ActivitiScriptNode) execution.getVariable(CommonWorkflowModel.TARGET_SITE);
    NodeRef targetFolderNodeRef = akwfTargetFolder.getNodeRef();
    NodeRef akwfTargetSiteNodeRef = akwfTargetSite.getNodeRef();
    if (AccessStatus.ALLOWED.equals(permissionService.hasPermission(akwfTargetFolder.getNodeRef(), PermissionService.CREATE_CHILDREN))) {
      execution.setVariable(CommonWorkflowModel.HANDLING, CompleteDocumentWorkflowModel.AUTOMATIC_HANDLING);
      LOG.debug("User " + akwfInitiator + " have write permission on target folder, setting WF handling to Automatic");
      execution.setVariable(CommonWorkflowModel.APPROVER, akwfInitiator);
    } else {
      execution.setVariable(CommonWorkflowModel.HANDLING, CompleteDocumentWorkflowModel.MANUAL_HANDLING);
      LOG.debug("User " + akwfInitiator + " have read permission on target folder, setting WF handling to Manual");
    }

    String documentSecrecy = (String) execution.getVariable(CommonWorkflowModel.DOCUMENT_SECRECY);
    /*
     * Verify package contents
     */
    List<ChildAssociationRef> childAssocs = WorkflowUtil.getBpmPackageFiles(execution, nodeService);
    if (childAssocs != null && childAssocs.size() > 0) {
      for (ChildAssociationRef childAssoc : childAssocs) {
        NodeRef fileNodeRef = childAssoc.getChildRef();
        if (AccessStatus.ALLOWED.equals(permissionService.hasPermission(fileNodeRef, PermissionService.WRITE))) {
          LOG.debug("User " + akwfInitiator + " has permission to complete file ");
        } else {
          throw new RuntimeException("Fel: Du har inte skrivrättigheter på filen som du försöker färdigställa.");
        }

        if (!CommonWorkflowModel.DOC_STATUS_DONE.equals(nodeService.getProperty(fileNodeRef, AkDmModel.PROP_AKDM_DOC_STATUS))) {
          LOG.debug("Document is in status " + CommonWorkflowModel.DOC_STATUS_WORKING + ".");
        } else {
          throw new RuntimeException("Fel: Dokumentet har redan status " + CommonWorkflowModel.DOC_STATUS_DONE);
        }

        if (StringUtils.hasText((String) nodeService.getProperty(fileNodeRef, AkDmModel.PROP_AKDM_DOC_SECRECY))) {
          LOG.debug("Document has secrecy set.");
        } else {
          if (StringUtils.hasText(documentSecrecy)) {
            LOG.info("Setting document secrecy to " + documentSecrecy + " for document " + fileNodeRef.toString());
            nodeService.setProperty(fileNodeRef, AkDmModel.PROP_AKDM_DOC_SECRECY, documentSecrecy);
          } else {
            throw new RuntimeException("Fel: Dokumentet har inte någon sekretess vald.");
          }
        }
      }
    } else {
      throw new RuntimeException("Fel: Inga filer inkluderades i arbetsflödet");
    }

    /*
     * Validate sites assignment
     */
    SiteInfo folderSite = siteService.getSite(targetFolderNodeRef);

    SiteInfo site = siteService.getSite(akwfTargetSiteNodeRef);
    if (!site.equals(folderSite)) {
      throw new RuntimeException("Fel: Vald mapp ligger inte under den valda siten.");
    }

    String siteRoleGroup = siteService.getSiteRoleGroup(site.getShortName(), CommonWorkflowModel.SITE_COLLABORATOR);
    Set<String> containedAuthorities = authorityService.getContainedAuthorities(AuthorityType.USER, siteRoleGroup, false);
    if (containedAuthorities.size() == 0) {
      siteRoleGroup = siteService.getSiteRoleGroup(site.getShortName(), CommonWorkflowModel.SITE_MANAGER);
      containedAuthorities = authorityService.getContainedAuthorities(AuthorityType.USER, siteRoleGroup, false);
      if (containedAuthorities.size() == 0) {
        throw new RuntimeException("Fel: Det finns ingen som kan godkänna på denna sammarbetsyta.");
      }
    }
    LOG.info(siteRoleGroup + " assigned for review task.");
    execution.setVariable(CommonWorkflowModel.SITE, site.getShortName());
    execution.setVariable(CommonWorkflowModel.SITE_GROUP, siteRoleGroup);
    final String readPermSiteRoleGroup = siteRoleGroup;
    /*
     * Lock files and set permissions
     */
    String akwfHandling = (String) execution.getVariable(CommonWorkflowModel.HANDLING);
    if (akwfHandling != "Error") {
      if (childAssocs != null && childAssocs.size() > 0) {
        for (ChildAssociationRef childAssoc : childAssocs) {
          final NodeRef fileNodeRef = childAssoc.getChildRef();
          LOG.debug("Locked file " + fileNodeRef.toString());
          AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
            public Object doWork() throws Exception {
              AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
              lockService.lock(fileNodeRef, LockType.READ_ONLY_LOCK);
              LOG.debug("Adding temporary read permission on file for " + readPermSiteRoleGroup);
              permissionService.setPermission(fileNodeRef, readPermSiteRoleGroup, PermissionService.READ, true);
              AuthenticationUtil.setFullyAuthenticatedUser(akwfInitiator);
              return "";
            }
          }, AuthenticationUtil.getSystemUserName());
        }
      }
    }
  }

}
