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

function getUserGroups(person) {
	// person is a Person Node
   	var containerGroups = people.getContainerGroups(person);
   	var groupNames = new Array();
  	for (var i=0;i<containerGroups.length;i++) {
    	var group = containerGroups[i].getQnamePath();
      	var p = group.split(":");
      	var groupName = p[p.length-1];
      	groupNames.push(groupName);
    }
   	return groupNames;
}

function checkAllowCreateSite(person) {
  var groups = getUserGroups(person);
  for (var i=0;i<groups.length;i++) {
      if (groups[i] == "GROUP_ALFRESCO_ADMINISTRATORS" || 
          groups[i] == "GROUP_AK-MAN-Siteinitiators" ||
          groups[i] == "GROUP_SITE_ADMINISTRATORS") {
          return true
      }
  }
  return false;
}

model.createSiteEnabled = checkAllowCreateSite(person);