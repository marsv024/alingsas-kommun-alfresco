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
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.alfresco.repo.workflow.activiti.ActivitiConstants;
import org.alfresco.repo.workflow.activiti.ActivitiScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import se.alingsas.alfresco.repo.workflow.model.CommonWorkflowModel;

import java.util.List;

public class WorkflowUtil {

	/**
	 * Get the service registry bean from the activiti context

	 */
	public static ServiceRegistry getServiceRegistry() {
		ProcessEngineConfigurationImpl config = Context
				.getProcessEngineConfiguration();
		if (config != null) {
			// Fetch the registry that is injected in the activiti
			// spring-configuration
			ServiceRegistry registry = (ServiceRegistry) config.getBeans().get(
					ActivitiConstants.SERVICE_REGISTRY_BEAN_KEY);
			if (registry == null) {
				throw new RuntimeException(
						"Service-registry not present in ProcessEngineConfiguration beans, expected ServiceRegistry with key"
								+ ActivitiConstants.SERVICE_REGISTRY_BEAN_KEY);
			}
			return registry;
		}
		throw new IllegalStateException(
				"No ProcessEngineCOnfiguration found in active context");
	}

	/**
	 * Get the workflow files from the bpm:package model variable as a list
	 */
	public static List<ChildAssociationRef> getBpmPackageFiles(
			DelegateExecution execution, NodeService nodeService) {
		ActivitiScriptNode bpm_package = (ActivitiScriptNode) execution
				.getVariable(CommonWorkflowModel.BPM_PACKAGE);
		if (bpm_package == null) {
			return null;
		} else {
			NodeRef nodeRef = bpm_package.getNodeRef();
			return nodeService.getChildAssocs(nodeRef);
		}
	}
}
