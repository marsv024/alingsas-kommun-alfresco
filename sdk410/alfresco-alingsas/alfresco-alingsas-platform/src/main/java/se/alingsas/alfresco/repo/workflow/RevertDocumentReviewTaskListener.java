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

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.alingsas.alfresco.repo.workflow.model.CommonWorkflowModel;

/**
 * Revert document workflow task listener. Will verify that the approver have
 * actually permission to write files to the target folder.
 * 
 * @author Marcus Svensson - Redpill Linpro AB
 * 
 */
public class RevertDocumentReviewTaskListener implements TaskListener {
	private static final Log LOG = LogFactory.getLog(RevertDocumentReviewTaskListener.class);

	@Override
	public void notify(DelegateTask task) {
		final String akwfApprover = AuthenticationUtil
				.getFullyAuthenticatedUser();
		LOG.debug("Entering CompleteDocumentReviewTaskListener");

		LOG.debug("Authenticated user is " + akwfApprover);

		task.getExecution().setVariable(CommonWorkflowModel.APPROVER,
				akwfApprover);

		ActivitiScriptNode akwfTargetFolder = (ActivitiScriptNode) task
				.getVariable(CommonWorkflowModel.TARGET_FOLDER);
		NodeRef targetFolderNodeRef = akwfTargetFolder.getNodeRef();
		final ServiceRegistry serviceRegistry = WorkflowUtil
				.getServiceRegistry();
		PermissionService permissionService = serviceRegistry
				.getPermissionService();

		if (AccessStatus.ALLOWED.equals(permissionService.hasPermission(
				akwfTargetFolder.getNodeRef(), PermissionService.CREATE_CHILDREN))) {
			LOG.debug("Access allowed, approver set to " + akwfApprover);
			task.getExecution().setVariable(CommonWorkflowModel.BPM_COMMENT,
					task.getVariable(CommonWorkflowModel.BPM_COMMENT));
		} else {
			LOG.error("User " + akwfApprover
					+ " does not have write permission on folder "
					+ akwfTargetFolder.getDisplayPath());
			throw new AccessDeniedException(
					"Du har inte rättighet att skriva i målmappen");
		}
	}

}
