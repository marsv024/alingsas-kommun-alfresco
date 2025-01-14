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

import org.alfresco.repo.model.Repository;
import org.alfresco.repo.nodelocator.NodeLocatorService;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.redpill.alfresco.numbering.component.NumberingComponent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.util.StringUtils;
import se.alingsas.alfresco.repo.model.AkDmModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandleMissingDocumentNumbers extends DeclarativeWebScript
        implements InitializingBean {

  private static final Log LOG = LogFactory.getLog(HandleMissingDocumentNumbers.class);
  private ServiceRegistry serviceRegistry;
  private BehaviourFilter behaviourFilter;
  private NumberingComponent numberingComponent;
  private Repository repositoryHelper;

  @Override
  protected Map<String, Object> executeImpl(final WebScriptRequest req,
          final Status status, final Cache cache) {
    final Map<String, Object> model = new HashMap<String, Object>();

    SearchService searchService = serviceRegistry.getSearchService();
    NodeService nodeService = serviceRegistry.getNodeService();
    NodeLocatorService nodeLocatorService = serviceRegistry.getNodeLocatorService();
    FileFolderService fileFolderService = serviceRegistry.getFileFolderService();

    int result = 0;

    List<FileInfo> search = fileFolderService.search(repositoryHelper.getCompanyHome(), "*", true, false, true);
    behaviourFilter.disableBehaviour();
    for (FileInfo item : search) {
      NodeRef nodeRef = item.getNodeRef();
      if (nodeService.exists(nodeRef) && nodeService.hasAspect(nodeRef, AkDmModel.ASPECT_AKDM_COMMON)) {
        String property = (String) nodeService.getProperty(nodeRef, AkDmModel.PROP_AKDM_DOC_NUMBER);
        if (property == null || !StringUtils.hasText(property)) {
          try {
            String decoratedNextNumber = numberingComponent.getDecoratedNextNumber(nodeRef);
            nodeService.setProperty(nodeRef, AkDmModel.PROP_AKDM_DOC_NUMBER, decoratedNextNumber);
            if (LOG.isDebugEnabled()) {
              LOG.debug("Setting document number for " + decoratedNextNumber + " for node " + nodeRef.toString());
            }

          } catch (Exception e) {
            throw new RuntimeException(e);
          }
          result++;
        }
      }
    }
    behaviourFilter.enableBehaviour();
    model.put("documents", result);
    return model;
  }

  /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    // TODO Auto-generated method stub

  }

  public void setServiceRegistry(ServiceRegistry serviceRegistry) {
    this.serviceRegistry = serviceRegistry;
  }

  public void setNumberingComponent(NumberingComponent numberingComponent) {
    this.numberingComponent = numberingComponent;
  }

  public void setBehaviourFilter(BehaviourFilter behaviourFilter) {
    this.behaviourFilter = behaviourFilter;
  }

  public Repository getRepositoryHelper() {
    return repositoryHelper;
  }

  public void setRepositoryHelper(Repository repositoryHelper) {
    this.repositoryHelper = repositoryHelper;
  }

}
