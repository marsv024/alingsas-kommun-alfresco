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

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.site.SiteService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import se.alingsas.alfresco.repo.utils.byggreda.ByggRedaUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ByggRedaImport extends DeclarativeWebScript implements InitializingBean {

  private static final Log LOG = LogFactory.getLog(ByggRedaImport.class);
  private static ServiceRegistry serviceRegistry;
  private static String siteName;
  private static String basePath;

  @Override
  protected Map<String, Object> executeImpl(final WebScriptRequest req, final Status status, final Cache cache) {
    final Map<String, Object> model = new HashMap<String, Object>();
    // Check permissions since only site collaborators and managers should
    // be able to run this action

    SiteService siteService = serviceRegistry.getSiteService();
    MutableAuthenticationService authenticationService = serviceRegistry.getAuthenticationService();
    String currentUserName = authenticationService.getCurrentUserName();
    String membersRole = siteService.getMembersRole(siteName, currentUserName);
    if ("SiteManager".equals(membersRole) || "SiteCollaborator".equals(membersRole)) {
      // get the site parameter
      final String sourcePath = basePath + "/" + req.getParameter("sourcePath");
      final String metaFileName = req.getParameter("metaFileName");
      final boolean updateExisting = "yes".equals(req.getParameter("updateIfExists"));

      File f = new File(sourcePath);
      if (!f.exists()) {
        // status.setCode(400);
        // status.setMessage("Kan inte läsa källmappen " + sourcePath
        // + ". Kontrollera att du skrivit in korrekt sökväg.");
        // status.setRedirect(true);
        status.setCode(200);
        model.put("result", "Kan inte läsa källmappen " + sourcePath + ". Kontrollera att du skrivit in korrekt sökväg.");
        return model;
      }
      f = new File(sourcePath + "/" + metaFileName);
      if (!f.exists()) {
        // status.setCode(400);
        // status.setMessage("Kan inte läsa styrfilen " + sourcePath + "/"
        // + metaFileName
        // + ". Kontrollera att du skrivit in korrekt sökväg.");
        // status.setRedirect(true);
        status.setCode(200);
        model.put("result", "Kan inte läsa styrfilen " + sourcePath + "/" + metaFileName + ". Kontrollera att du skrivit in korrekt sökväg.");
        return model;
      }
      ByggRedaUtil bru = new ByggRedaUtil();
      bru.setUpdateExisting(updateExisting);
      bru.setSourcePath(sourcePath);
      bru.setMetaFileName(metaFileName);
      bru.setRunAs(AuthenticationUtil.getFullyAuthenticatedUser());
      if (!bru.validateParams()) {
        status.setCode(200);
        // status.setMessage("Kunde inte validera inparametrar. Kontrollera systemloggen för mer detaljer.");
        model.put("result", "Kunde inte validera inparametrar. Kontrollera systemloggen för mer detaljer.");
        // status.setRedirect(true);
      } else {
        Thread t = new Thread(bru);
        t.start();
        status.setCode(200);
        model.put("result", "Byggredaimport startad. Du kan nu stänga denna sidan.");
      }
    } else {
      // status.setCode(400);
      status.setCode(200);
      model.put("result", "Åtkomst nekad, du måste vara ansvarig eller dokumentansvarig för samarbetsytan för att använda detta verktyg.");
      // status.setMessage("Access denied, you must be site manager or site collaborator to run this tool");
      // status.setRedirect(true);
      return model;
    }

    return model;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    // TODO Auto-generated method stub

  }

  public ServiceRegistry getServiceRegistry() {
    return serviceRegistry;
  }

  public void setServiceRegistry(ServiceRegistry serviceRegistry) {
    ByggRedaImport.serviceRegistry = serviceRegistry;
  }

  public String getSiteName() {
    return siteName;
  }

  public void setSiteName(String siteName) {
    ByggRedaImport.siteName = siteName;
  }

  public String getBasePath() {
    return basePath;
  }

  public void setBasePath(String basePath) {
    ByggRedaImport.basePath = basePath;
  }

}
