/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.alingsas.alfresco.repo.webscripts;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static se.alingsas.alfresco.repo.model.AkDmModel.AKDM_URI;

/**
 *
 * @author jimmie
 */
public class SiteHasAspect extends DeclarativeWebScript {

  final Map<String, Object> model = new HashMap<>();
  protected NodeService nodeService;
  protected SiteService siteService;
  private NodeRef siteNodeRef;

  public void setNodeService(NodeService nodeService) {
    this.nodeService = nodeService;
  }

  public void setSiteService(SiteService siteService) {
    this.siteService = siteService;
  }

  @Override
  protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {

    String nodeRefString = (String) req.getParameter("nodeRef");
    String aspectString = (String) req.getParameter("aspect");
    NodeRef nodeRef = new NodeRef(nodeRefString);
    QName createdQName = QName.createQName(AKDM_URI, aspectString);

    SiteInfo site = siteService.getSite(nodeRef);
    if (site != null) {
      siteNodeRef = site.getNodeRef();
    }
    Set<QName> aspects = nodeService.getAspects(siteNodeRef);
    model.put("site", site.getShortName());
    model.put("aspect", aspectString);
    
    if (aspects.contains(createdQName)) {
      model.put("hasaspect", true);
      return model;
    }

    model.put("hasaspect", false);
    return model;
  }

}
