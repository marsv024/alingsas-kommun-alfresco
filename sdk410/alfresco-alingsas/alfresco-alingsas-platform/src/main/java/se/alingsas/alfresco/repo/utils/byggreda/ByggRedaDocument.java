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

package se.alingsas.alfresco.repo.utils.byggreda;

import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.lang3.StringUtils;

public class ByggRedaDocument {
	private boolean readSuccessfully = false;
	private String statusMsg = "";
	private NodeRef nodeRef = null;
	private int lineNumber;
	private String mimetype = "";
	
	private String film = "";
	private String serialNumber = "";
	private Integer recordYear = 0;
	private String recordNumber = "";
	private String recordDisplay = "";	
	private String buildingDescription = "";
	private String lastBuildingDescription = "";
	private String address = "";
	private String lastAddress = "";
	private String decision = "";
	private String forA= "";
	private String issuePurpose = "";
	private String note = "";
	private String records = "";
	private String fileName = "";
	
	private String path = "";
	private String title = "";
	private String originalPath = "";

  /**
   * @return the readSuccessfully
   */
  public boolean isReadSuccessfully() {
    return readSuccessfully;
  }

  /**
   * @param readSuccessfully the readSuccessfully to set
   */
  public void setReadSuccessfully(boolean readSuccessfully) {
    this.readSuccessfully = readSuccessfully;
  }

  /**
   * @return the statusMsg
   */
  public String getStatusMsg() {
    return statusMsg;
  }

  /**
   * @param statusMsg the statusMsg to set
   */
  public void setStatusMsg(String statusMsg) {
    this.statusMsg = statusMsg;
  }

  /**
   * @return the nodeRef
   */
  public NodeRef getNodeRef() {
    return nodeRef;
  }

  /**
   * @param nodeRef the nodeRef to set
   */
  public void setNodeRef(NodeRef nodeRef) {
    this.nodeRef = nodeRef;
  }

  /**
   * @return the lineNumber
   */
  public int getLineNumber() {
    return lineNumber;
  }

  /**
   * @param lineNumber the lineNumber to set
   */
  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  /**
   * @return the mimetype
   */
  public String getMimetype() {
    return mimetype;
  }

  /**
   * @param mimetype the mimetype to set
   */
  public void setMimetype(String mimetype) {
    this.mimetype = mimetype;
  }

  /**
   * @return the film
   */
  public String getFilm() {
    return film;
  }

  /**
   * @param film the film to set
   */
  public void setFilm(String film) {
    this.film = film;
  }

  /**
   * @return the serialNumber
   */
  public String getSerialNumber() {
    return serialNumber;
  }

  /**
   * @param serialNumber the serialNumber to set
   */
  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  /**
   * @return the recordYear
   */
  public Integer getRecordYear() {
    return recordYear;
  }

  /**
   * @param recordYear the recordYear to set
   */
  public void setRecordYear(Integer recordYear) {
    this.recordYear = recordYear;
  }

  /**
   * @return the recordNumber
   */
  public String getRecordNumber() {
    String newRecordNumber = StringUtils.leftPad(recordNumber, 4, "0");
    return newRecordNumber;
  }

  /**
   * @param recordNumber the recordNumber to set
   */
  public void setRecordNumber(String recordNumber) {
    this.recordNumber = recordNumber;
  }

  /**
   * @return the recordDisplay
   */
  public String getRecordDisplay() {
    return recordDisplay;
  }

  /**
   * @param recordDisplay the recordDisplay to set
   */
  public void setRecordDisplay(String recordDisplay) {
    this.recordDisplay = recordDisplay;
  }

  /**
   * @return the buildingDescription
   */
  public String getBuildingDescription() {
    return buildingDescription;
  }

  /**
   * @param buildingDescription the buildingDescription to set
   */
  public void setBuildingDescription(String buildingDescription) {
    this.buildingDescription = buildingDescription;
  }

  /**
   * @return the lastBuildingDescription
   */
  public String getLastBuildingDescription() {
    return lastBuildingDescription;
  }

  /**
   * @param lastBuildingDescription the lastBuildingDescription to set
   */
  public void setLastBuildingDescription(String lastBuildingDescription) {
    this.lastBuildingDescription = lastBuildingDescription;
  }

  /**
   * @return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * @param address the address to set
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * @return the lastAddress
   */
  public String getLastAddress() {
    return lastAddress;
  }

  /**
   * @param lastAddress the lastAddress to set
   */
  public void setLastAddress(String lastAddress) {
    this.lastAddress = lastAddress;
  }

  /**
   * @return the decision
   */
  public String getDecision() {
    return decision;
  }

  /**
   * @param decision the decision to set
   */
  public void setDecision(String decision) {
    this.decision = decision;
  }

  /**
   * @return the forA
   */
  public String getForA() {
    return forA;
  }

  /**
   * @param forA the forA to set
   */
  public void setForA(String forA) {
    this.forA = forA;
  }

  /**
   * @return the issuePurpose
   */
  public String getIssuePurpose() {
    return issuePurpose;
  }

  /**
   * @param issuePurpose the issuePurpose to set
   */
  public void setIssuePurpose(String issuePurpose) {
    this.issuePurpose = issuePurpose;
  }

  /**
   * @return the note
   */
  public String getNote() {
    return note;
  }

  /**
   * @param note the note to set
   */
  public void setNote(String note) {
    this.note = note;
  }

  /**
   * @return the records
   */
  public String getRecords() {
    return records;
  }

  /**
   * @param records the records to set
   */
  public void setRecords(String records) {
    this.records = records;
  }

  /**
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * @param fileName the fileName to set
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * @param path the path to set
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return the originalPath
   */
  public String getOriginalPath() {
    return originalPath;
  }

  /**
   * @param originalPath the originalPath to set
   */
  public void setOriginalPath(String originalPath) {
    this.originalPath = originalPath;
  }
  
  
	
}
