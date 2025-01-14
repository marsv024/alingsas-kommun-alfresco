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

package se.alingsas.alfresco.repo.workflow.model;

public interface CommonWorkflowModel {
	public static final String DOC_STATUS_DONE = "Färdigt dokument";
	public static final String DOC_STATUS_WORKING = "Arbetsdokument";
	public static final String BPM_PACKAGE = "bpm_package";
	public static final String BPM_COMMENT = "bpm_comment";
	public static final String AKWF_REVIEWOUTCOME = "akwf_reviewOutcome";
	public static final String HANDLING = "akwf_handling";
	public static final String INITIATOR = "akwf_initiator";
	public static final String APPROVER = "akwf_approver";
	public static final String TARGET_SITE = "akwf_targetSite";
	public static final String TARGET_FOLDER = "akwf_targetFolder";
	public static final String TARGET_CHOICE = "akwf_reviewDocumentTargetChoice";	
	public static final String TARGET_CHOICE_OPTION_SITE = "site";
	public static final String TARGET_CHOICE_OPTION_MY_HOME_FOLDER = "my_homefolder";
	public static final String TARGET_CHOICE_OPTION_USER_HOME_FOLDER = "user_homefolder";	
	public static final String AKWF_RESULTVARIABLE = "akwf_resultVariable";
	public static final String DOCUMENT_SECRECY = "akwf_documentSecrecy";
	
	public static final String SITE_GROUP = "akwf_siteGroup";
	public static final String SITE = "akwf_site";
	public static final String SITE_MANAGER = "SiteManager";
	public static final String SITE_COLLABORATOR = "SiteCollaborator";
	public static final String REMOVE_PERMISSIONS_DONE = "akwf_premissionsRemoved";
}
