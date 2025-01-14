/*
 * Copyright (C) 2012-2014 Alingsås Kommun
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

import org.alfresco.repo.model.Repository;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.site.SiteService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Policy base class which make sure that some services are available to
 * implementing policies.
 * 
 * @author Marcus Svensson - Redpill Linpro AB
 * @author Carl Nordenfelt - Redpill Linpro AB
 *
 */

public abstract class AbstractPolicy implements InitializingBean {
  
  protected Repository repository;
  
  protected NodeService nodeService;

  protected PermissionService permissionService;

  protected PolicyComponent policyComponent;

  protected BehaviourFilter behaviourFilter;

  protected LockService lockService;
  
  protected FileFolderService fileFolderService;

  protected SiteService siteService;
  
  protected DictionaryService dictionaryService;

  public void setPolicyComponent(final PolicyComponent policyComponent) {
    this.policyComponent = policyComponent;
  }

  public void setNodeService(final NodeService nodeService) {
    this.nodeService = nodeService;
  }

  public void setPermissionService(final PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  public void setBehaviourFilter(final BehaviourFilter behaviourFilter) {
    this.behaviourFilter = behaviourFilter;
  }

  public void setLockService(final LockService lockService) {
    this.lockService = lockService;
  }
  
  public void setSiteService(SiteService siteService) {
    this.siteService = siteService;
  }

  public void setFileFolderService(FileFolderService fileFolderService) {
    this.fileFolderService = fileFolderService;
  }
  
  public void setDictionaryService(DictionaryService dictionaryService) {
    this.dictionaryService = dictionaryService;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(nodeService, "You must provide an instance of NodeService.");
    Assert.notNull(permissionService, "You must provide an instance of PermissionService.");
    Assert.notNull(policyComponent, "You must provide an instance of PolicyComponent.");
    Assert.notNull(behaviourFilter, "You must provide an instance of BehaviourFilter.");
    Assert.notNull(lockService, "You must provide an instance of LockService.");
    Assert.notNull(fileFolderService, "You must provide an instance of FileFolderService");
    Assert.notNull(siteService, "You must provide an instance of SiteService");
    Assert.notNull(dictionaryService, "You must provide an instance of DictionaryService");
    Assert.notNull(repository, "You must provide an instance of Repository");
  }


  public void setRepository(Repository repository) {
    this.repository = repository;
  }



}
