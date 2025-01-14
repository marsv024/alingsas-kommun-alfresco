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

package se.alingsas.alfresco.repo.model;

import org.alfresco.service.namespace.QName;

public interface AkWfModel {
  /**
   * Namespaces
   */
  public static final String AKWF_URI = "http://www.alingsas.se/alfresco/model/wf/1.0";
  public static final String AKWF_SHORT = "akwf";
  /**
   * Types
   */
  public static final QName TYPE_AKWF_COMPLETE_DOCUMENT = QName.createQName(AKWF_URI, "completeDocument");
  public static final QName TYPE_AKWF_COMPLETE_DOCUMENT_REVIEW_TASK = QName.createQName(AKWF_URI, "completeDocumentReviewDocumentTask");
  public static final QName TYPE_AKWF_COMPLETE_DOCUMENT_REVIEW_RESULT_RECEIPT_TASK = QName.createQName(AKWF_URI, "completeDocumentUserReviewResultReceiptTask");

  public static final QName TYPE_AKWF_REVERT_DOCUMENT_REVIEW_RESULT_RECEIPT_TASK = QName.createQName(AKWF_URI, "revertDocumentReviewReceiptTask");
  /**
   * Aspects
   */

  public static final QName ASPECT_AKWF_WORK_INFO = QName.createQName(AKWF_URI, "workInfo");

  /**
   * Properties
   */
  public static final QName PROP_AKWF_COMPLETE_DOCUMENT_REVIEW_OUTCOME = QName.createQName(AKWF_URI, "reviewOutcome");
  public static final QName PROP_AKWF_RESULT_VARIABLE = QName.createQName(AKWF_URI, "resultVariable");
  public static final QName PROP_AKWF_TARGET_SITE = QName.createQName(AKWF_URI, "targetSite");
  public static final QName PROP_AKWF_TARGET_FOLDER = QName.createQName(AKWF_URI, "targetFolder");
  public static final QName PROP_AKWF_DOCUMENT_SECRECY = QName.createQName(AKWF_URI, "documentSecrecy");

}
