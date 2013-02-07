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
import org.apache.log4j.Logger;

import se.alingsas.alfresco.repo.workflow.model.CommonWorkflowModel;

/**
 * Revert document workflow task listener. Will verify that the approver have
 * actually permission to write files to the target folder.
 * 
 * @author Marcus Svensson - Redpill Linpro AB
 * 
 */
public class CompleteDocumentReviewTaskListener implements TaskListener {
	private static final Logger LOG = Logger
			.getLogger(CompleteDocumentReviewTaskListener.class);

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
				targetFolderNodeRef,
				PermissionService.CREATE_CHILDREN))) {
			LOG.debug("Access allowed, approver set to " + akwfApprover
					+ ", review outcome is "
					+ task.getVariable(CommonWorkflowModel.AKWF_REVIEWOUTCOME));
			task.getExecution().setVariable(CommonWorkflowModel.BPM_COMMENT,
					task.getVariable(CommonWorkflowModel.BPM_COMMENT));
			task.getExecution().setVariable(
					CommonWorkflowModel.AKWF_REVIEWOUTCOME,
					task.getVariable(CommonWorkflowModel.AKWF_REVIEWOUTCOME));
		} else {
			LOG.error("User " + akwfApprover
					+ " does not have write permission on folder "
					+ akwfTargetFolder.getDisplayPath());
			throw new AccessDeniedException(
					"Du har inte rättighet att skriva i målmappen");
		}
	}

}
