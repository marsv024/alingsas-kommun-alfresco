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

package se.alingsas.alfresco.repo.model;

import org.alfresco.service.namespace.QName;

public interface AkDmModel {
  /**
   * Namespaces
   */
  public static final String AKDM_URI = "http://www.alingsas.se/alfresco/model/dm/1.0";
  public static final String STCP_URI = "http://www.alfresco.org/model/sitecustomproperty/1.0";

  /**
   * Types
   */
  public static final QName TYPE_AKDM_DOCUMENT = QName.createQName(AKDM_URI, "document");
  public static final QName TYPE_AKDM_INSTRUCTION = QName.createQName(AKDM_URI, "instruction");
  public static final QName TYPE_AKDM_ORDINANCE = QName.createQName(AKDM_URI, "ordinance");
  public static final QName TYPE_AKDM_MANUAL = QName.createQName(AKDM_URI, "manual");
  public static final QName TYPE_AKDM_PROTOCOL = QName.createQName(AKDM_URI, "protocol");
  public static final QName TYPE_AKDM_GENERAL_DOC = QName.createQName(AKDM_URI, "generaldocument");
  public static final QName TYPE_AKDM_ECONOMY_DOC = QName.createQName(AKDM_URI, "economydocument");
  public static final QName TYPE_AKDM_ISSUE_LIST = QName.createQName(AKDM_URI, "issuelist");
  public static final QName TYPE_AKDM_INTERNAL_VALIDATION = QName.createQName(AKDM_URI, "internalvalidation");
  public static final QName TYPE_AKDM_PUL_LIST = QName.createQName(AKDM_URI, "listpul");
  public static final QName TYPE_AKDM_BYGGREDA_DOC = QName.createQName(AKDM_URI, "byggredadocument");
  public static final QName TYPE_AKDM_FOLDER = QName.createQName(AKDM_URI, "folder");

  /**
   * Aspects
   */
  public static final QName ASPECT_AKDM = QName.createQName(AKDM_URI, "aspect");

  public static final QName ASPECT_AKDM_COMMON = QName.createQName(AKDM_URI, "commonAspect");
  public static final QName PROP_AKDM_DOC_NUMBER = QName.createQName(AKDM_URI, "documentNumber");
  public static final QName PROP_AKDM_DOC_STATUS = QName.createQName(AKDM_URI, "documentStatus");
  public static final QName PROP_AKDM_DOC_SECRECY = QName.createQName(AKDM_URI, "documentSecrecy");

  public static final QName ASPECT_AKDM_INSTRUCTION = QName.createQName(AKDM_URI, "instructionAspect");
  public static final QName PROP_AKDM_INSTRUCTION_DECISION_DATE = QName.createQName(AKDM_URI, "instructionDecisionDate");
  public static final QName PROP_AKDM_INSTRUCTION_GROUP = QName.createQName(AKDM_URI, "instructionGroup");

  public static final QName ASPECT_AKDM_MANUAL = QName.createQName(AKDM_URI, "manualAspect");
  public static final QName PROP_AKDM_MANUAL_FUNCTION = QName.createQName(AKDM_URI, "manualFunction");
  public static final QName PROP_AKDM_MANUAL_MANUAL = QName.createQName(AKDM_URI, "manualManual");

  public static final QName ASPECT_AKDM_PROTOCOL = QName.createQName(AKDM_URI, "protocolAspect");
  public static final QName PROP_AKDM_PROTOCOL_ID = QName.createQName(AKDM_URI, "protocolId");

  public static final QName ASPECT_AKDM_GENERAL_DOC = QName.createQName(AKDM_URI, "generalDocumentAspect");
  public static final QName PROP_AKDM_GENERAL_DOCUMENT_DESCRIPTION = QName.createQName(AKDM_URI, "generalDocumentDescription");
  public static final QName PROP_AKDM_GENERAL_DOCUMENT_DATE = QName.createQName(AKDM_URI, "generalDocumentDate");

  public static final QName ASPECT_AKDM_ISSUE_LIST = QName.createQName(AKDM_URI, "issueListAspect");
  public static final QName PROP_AKDM_ISSUE_LIST_MEETINGDATE = QName.createQName(AKDM_URI, "issuelistMeetingDate");

  public static final QName ASPECT_AKDM_BYGGREDA = QName.createQName(AKDM_URI, "byggRedaAspect");
  public static final QName PROP_AKDM_BYGGREDA_FILM = QName.createQName(AKDM_URI, "byggRedaFilm");
  public static final QName PROP_AKDM_BYGGREDA_SERIAL_NUMBER = QName.createQName(AKDM_URI, "byggRedaSerialNumber");
  public static final QName PROP_AKDM_BYGGREDA_RECORD_NUMBER = QName.createQName(AKDM_URI, "byggRedaRecordNumber");
  public static final QName PROP_AKDM_BYGGREDA_RECORD_YEAR = QName.createQName(AKDM_URI, "byggRedaRecordYear");
  public static final QName PROP_AKDM_BYGGREDA_RECORD_DISPLAY = QName.createQName(AKDM_URI, "byggRedaRecordDisplay");
  public static final QName PROP_AKDM_BYGGREDA_BUILDING_DESCR = QName.createQName(AKDM_URI, "byggRedaBuildingDescription");
  public static final QName PROP_AKDM_BYGGREDA_LAST_BUILDING_DESCR = QName.createQName(AKDM_URI, "byggRedaLastBuildingDescription");
  public static final QName PROP_AKDM_BYGGREDA_ADDRESS = QName.createQName(AKDM_URI, "byggRedaAddress");
  public static final QName PROP_AKDM_BYGGREDA_LAST_ADDRESS = QName.createQName(AKDM_URI, "byggRedaLastAddress");
  public static final QName PROP_AKDM_BYGGREDA_DECISION = QName.createQName(AKDM_URI, "byggRedaDecision");
  public static final QName PROP_AKDM_BYGGREDA_FOR = QName.createQName(AKDM_URI, "byggRedaFor");
  public static final QName PROP_AKDM_BYGGREDA_ISSUE_PURPOSE = QName.createQName(AKDM_URI, "byggRedaIssuePurpose");
  public static final QName PROP_AKDM_BYGGREDA_NOTE = QName.createQName(AKDM_URI, "byggRedaNote");
  public static final QName PROP_AKDM_BYGGREDA_RECORDS = QName.createQName(AKDM_URI, "byggRedaRecords");

  public static final QName ASPECT_AKDM_DOCUMENT_NUMBER_SETTINGS = QName.createQName(AKDM_URI, "documentNumberSettingsAspect");
  public static final QName PROP_AKDM_DOCUMENT_NUMBER_SETTINGS_DATE_PATTERN = QName.createQName(AKDM_URI, "docNumSettingDatePattern");
  public static final QName PROP_AKDM_DOCUMENT_NUMBER_SETTINGS_1 = QName.createQName(AKDM_URI, "docNumSettingPart1");
  public static final QName PROP_AKDM_DOCUMENT_NUMBER_SETTINGS_2 = QName.createQName(AKDM_URI, "docNumSettingPart2");
  public static final QName PROP_AKDM_DOCUMENT_NUMBER_SETTINGS_3 = QName.createQName(AKDM_URI, "docNumSettingPart3");

  public static final QName ASPECT_AKDM_TEMPLATEDASPECT = QName.createQName(AKDM_URI, "templatedAspect");
  
  public static final QName ASPECT_AKDM_GOVERNING = QName.createQName(AKDM_URI, "governingAspect");
  public static final QName PROP_AKDM_GOVERNING_DOC_RESPONSIBLE = QName.createQName(AKDM_URI, "governingDocResponsible");
  public static final QName PROP_AKDM_GOVERNING_DECISION_DATE = QName.createQName(AKDM_URI, "governingDecisionDate");
  public static final QName PROP_AKDM_GOVERNING_INSTANCE = QName.createQName(AKDM_URI, "governingInstance");
  public static final QName PROP_AKDM_GOVERNING_GROUP = QName.createQName(AKDM_URI, "governingGroup");
  public static final QName PROP_AKDM_GOVERNING_TARGET_GROUP = QName.createQName(AKDM_URI, "governingTargetGroup");

  public static final QName ASPECT_SITE_SECRECY = QName.createQName(AKDM_URI, "siteSecrecyAspect");
  public static final QName PROP_SITE_SECRECY = QName.createQName(STCP_URI, "siteSecrecy");
  public static final QName PROP_SITE_PRESET_VALUE = QName.createQName(STCP_URI, "presetValue");


  /**
   * Associations
   */
  public static final QName ASSOC_AKDM_BASEDONTEMPLATE = QName.createQName(AKDM_URI, "basedOnTemplate");

  /**
   * Constraint values
   */

  public static final String CONST_SECRECY_STATUS_PUBLIC = "Offentligt";
  public static final String CONST_SECRECY_STATUS_SECRET = "Sekretessbelagt";
  
  public static final String CONST_STATUS_WORKING_DOCUMENT = "Arbetsdokument";
  public static final String CONST_STATUS_COMPLETED_DOCUMENT = "Färdigt dokument";

}
