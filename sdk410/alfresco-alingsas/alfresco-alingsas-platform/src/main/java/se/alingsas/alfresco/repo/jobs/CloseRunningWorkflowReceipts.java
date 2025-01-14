package se.alingsas.alfresco.repo.jobs;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.cmr.workflow.WorkflowTaskQuery;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.redpill.alfresco.module.jobs.ClusteredExecuter;
import org.springframework.util.Assert;
import se.alingsas.alfresco.repo.model.AkWfModel;

import java.util.List;

/**
 * Scheduled job which closes running workflow receipt tasks
 *
 * @author Marcus Svartmark - Redpill Linpro AB
 */
public class CloseRunningWorkflowReceipts extends ClusteredExecuter {

    private WorkflowService workflowService;
    private static final Log LOG = LogFactory.getLog(CloseRunningWorkflowReceipts.class);

    @Override
    protected void executeInternal(String jobName) {
        AuthenticationUtil.runAs((RunAsWork<Void>) () -> {
            RetryingTransactionHelper transactionHelper = transactionService.getRetryingTransactionHelper();
            transactionHelper.doInTransaction((RetryingTransactionCallback<Void>) () -> {
                LOG.debug("End workflow task: " + AkWfModel.TYPE_AKWF_COMPLETE_DOCUMENT_REVIEW_RESULT_RECEIPT_TASK);
                int count = cancelAllReceiptTasks(AkWfModel.TYPE_AKWF_COMPLETE_DOCUMENT_REVIEW_RESULT_RECEIPT_TASK);

                LOG.debug("End workflow task: " + AkWfModel.TYPE_AKWF_REVERT_DOCUMENT_REVIEW_RESULT_RECEIPT_TASK);
                count += cancelAllReceiptTasks(AkWfModel.TYPE_AKWF_REVERT_DOCUMENT_REVIEW_RESULT_RECEIPT_TASK);
                // build the result message
                LOG.info("Closed " + count + " tasks");
                return null;
            }, false, true);
            return null;
        }, AuthenticationUtil.SYSTEM_USER_NAME);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(workflowService, "WorkflowService is null");
        LOG.info("Initialized " + CloseRunningWorkflowReceipts.class.getName());
    }

    public void setWorkflowService(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    public WorkflowService getWorkflowService() {
        return workflowService;
    }

    /**
     * Cancels ongoing workflows
     *
     * @param taskQName
     */
    @SuppressWarnings("deprecation")
    private int cancelAllReceiptTasks(QName taskQName) {
        int counter = 0;
        WorkflowTaskQuery query = new WorkflowTaskQuery();
        query.setTaskName(taskQName);
        query.setActive(true);
        // query for invite workflow task associate
        List<WorkflowTask> tasks = workflowService.queryTasks(query);

        for (WorkflowTask task : tasks) {
            LOG.debug("End task with id: " + task.getId());
            workflowService.endTask(task.getId(), null);
            counter++;
        }

        return counter;
    }
}
