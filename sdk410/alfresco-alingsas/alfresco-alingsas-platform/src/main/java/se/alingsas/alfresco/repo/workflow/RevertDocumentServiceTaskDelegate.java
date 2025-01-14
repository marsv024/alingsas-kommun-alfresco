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
import org.activiti.engine.delegate.JavaDelegate;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.OwnableService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.version.VersionType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import se.alingsas.alfresco.repo.model.AkDmModel;
import se.alingsas.alfresco.repo.workflow.model.CommonWorkflowModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Revert document workflow service task. Contains the actual logic to change
 * document status and move it.
 * 
 * @author Marcus Svensson - Redpill Linpro AB
 * 
 */
public class RevertDocumentServiceTaskDelegate implements JavaDelegate {
	private static final Log LOG = LogFactory.getLog(RevertDocumentServiceTaskDelegate.class);

	@Override
	public void execute(final DelegateExecution execution) throws Exception {
		LOG.debug("Entering RevertDocumentServiceTaskDelegate");

		final ServiceRegistry serviceRegistry = WorkflowUtil
				.getServiceRegistry();
		final LockService lockService = serviceRegistry.getLockService();

		final NodeService nodeService = serviceRegistry.getNodeService();
		final FileFolderService fileFolderService = serviceRegistry
				.getFileFolderService();
		final PermissionService permissionService = serviceRegistry.getPermissionService();
		final CheckOutCheckInService checkOutCheckInService = serviceRegistry
				.getCheckOutCheckInService();

		ActivitiScriptNode akwfTargetFolder = (ActivitiScriptNode) execution
				.getVariable(CommonWorkflowModel.TARGET_FOLDER);
		final NodeRef targetFolderNodeRef = akwfTargetFolder.getNodeRef();
		final OwnableService ownableService = serviceRegistry
				.getOwnableService();
		AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
			public Object doWork() throws Exception {

				List<ChildAssociationRef> childAssocs = WorkflowUtil
						.getBpmPackageFiles(execution, nodeService);
				if (childAssocs != null && childAssocs.size() > 0) {
					for (ChildAssociationRef childAssoc : childAssocs) {
						NodeRef fileNodeRef = childAssoc.getChildRef();
						String akwfApprover = (String) execution
								.getVariable("akwf_approver");

						LOG.debug("Unlocking document.");
						lockService.unlock(fileNodeRef);
						
						String readPermSiteRoleGroup = (String) execution.getVariable(CommonWorkflowModel.SITE_GROUP);
						if (readPermSiteRoleGroup != null) { 
							LOG.debug("Removing temporary read permission on file for "+readPermSiteRoleGroup);
							permissionService.deletePermission(fileNodeRef, readPermSiteRoleGroup, PermissionService.READ);
							execution.setVariable(CommonWorkflowModel.REMOVE_PERMISSIONS_DONE, "true");
						}
						ChildAssociationRef primaryParent = nodeService
								.getPrimaryParent(fileNodeRef);

						if (!targetFolderNodeRef.equals(primaryParent
								.getParentRef())) {
							LOG.debug("Moving document.");
							try {
								fileFolderService.move(fileNodeRef,
										targetFolderNodeRef, null);
							} catch (FileExistsException e) {
								LOG.info("File exists, renaming...");
								String name = (String) nodeService.getProperty(
										fileNodeRef, ContentModel.PROP_NAME);
								String strippedFileName = StringUtils
										.stripFilenameExtension(name);
								String filenameExtension = StringUtils
										.getFilenameExtension(name);
								name = strippedFileName + "_"
										+ System.currentTimeMillis() + "."
										+ filenameExtension;
								fileFolderService.move(fileNodeRef,
										targetFolderNodeRef, name);
							}
						} else {
							LOG.debug("Target folder is the same as source folder. Will not move the file.");
						}
						ownableService.setOwner(fileNodeRef, akwfApprover);
						LOG.debug("Checking in document");
						NodeRef workingCopy = checkOutCheckInService
								.checkout(fileNodeRef);
						nodeService.setProperty(workingCopy,
								AkDmModel.PROP_AKDM_DOC_STATUS,
								CommonWorkflowModel.DOC_STATUS_WORKING);
						Map<String, Serializable> versionProperties = new HashMap<String, Serializable>();
						versionProperties.put(VersionModel.PROP_VERSION_TYPE,
								VersionType.MINOR);

						versionProperties.put(VersionModel.PROP_DESCRIPTION,
								"Dokumentets status ändrades till arbetsdokument av "
										+ akwfApprover);
						checkOutCheckInService.checkin(workingCopy,
								versionProperties);

					}
				}
				return "";
			}
		}, AuthenticationUtil.getSystemUserName());
	}

}
