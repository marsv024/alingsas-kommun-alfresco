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
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import se.alingsas.alfresco.repo.workflow.model.CommonWorkflowModel;

import java.util.List;

public class UnlockFilesTaskListener implements TaskListener {
	private static final Log LOG = LogFactory.getLog(UnlockFilesTaskListener.class);

	@Override
	public void notify(final DelegateTask task) {
		final String akwfInitiator = AuthenticationUtil
				.getFullyAuthenticatedUser();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Entering UnlockFilesTaskListener");

			LOG.debug("Authenticated user is " + akwfInitiator);
		}
		final ServiceRegistry serviceRegistry = WorkflowUtil
				.getServiceRegistry();
		final LockService lockService = serviceRegistry.getLockService();
		final NodeService nodeService = serviceRegistry.getNodeService();
		final PermissionService permissionService = serviceRegistry
				.getPermissionService();

		// Setting result variable if review outcome is set.
		String reviewOutcome = (String) task.getExecution().getVariable(
				CommonWorkflowModel.AKWF_REVIEWOUTCOME);

		if (reviewOutcome != null && StringUtils.hasText(reviewOutcome)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Setting resultVariable to " + reviewOutcome);
			}
			task.setVariableLocal(CommonWorkflowModel.AKWF_RESULTVARIABLE,
					reviewOutcome);
		}

		final List<ChildAssociationRef> childAssocs = WorkflowUtil
				.getBpmPackageFiles(task.getExecution(), nodeService);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Unlocking files");
		}
		AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
			public Object doWork() throws Exception {
				if (childAssocs != null && childAssocs.size() > 0) {
					for (ChildAssociationRef childAssoc : childAssocs) {
						final NodeRef fileNodeRef = childAssoc.getChildRef();
						lockService.unlock(fileNodeRef);
						String readPermSiteRoleGroup = (String) task
								.getExecution().getVariable(
										CommonWorkflowModel.SITE_GROUP);
						if (readPermSiteRoleGroup != null) {
							String removePermissions = (String) task
									.getExecution()
									.getVariable(
											CommonWorkflowModel.REMOVE_PERMISSIONS_DONE);
							if (removePermissions == null
									|| !"true".equals(removePermissions)) {
								// If temporary permissions are not already
								// cleared, clear them now
								if (LOG.isDebugEnabled()) {
									LOG.debug("Removing temporary read permission on file for "
											+ readPermSiteRoleGroup);
								}
								permissionService.deletePermission(fileNodeRef,
										readPermSiteRoleGroup,
										PermissionService.READ);
							}
						}
					}
				}
				return "";
			}
		}, AuthenticationUtil.getSystemUserName());
	}

}
