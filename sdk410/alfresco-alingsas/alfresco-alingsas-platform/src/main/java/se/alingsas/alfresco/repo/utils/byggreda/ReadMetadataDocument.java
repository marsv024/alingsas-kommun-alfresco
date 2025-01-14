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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import se.alingsas.alfresco.repo.utils.CommonFileUtil;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReadMetadataDocument {
	private static final Log LOG = LogFactory.getLog(ReadMetadataDocument.class);
	private static final int EXPECTED_NUM_PARTS = 14;
	private static final String RECORD_DISPLAY_SEPARATOR = "-";
	/**
	 * Takes an input stream which should point to a metadata document for
	 * byggreda. Validates and parses the data and returns a set of
	 * ByggRedaDocument
	 */
	public static Set<ByggRedaDocument> read(final InputStream inputStream, List<String> globalMessages) {
		if (inputStream == null) {
			return null;
		}
		Set<ByggRedaDocument> result = new HashSet<ByggRedaDocument>();
		LineIterator lineIterator = null;
		String line = "";
		int lineNumber = 1;
		try {
			lineIterator = IOUtils.lineIterator(inputStream, "ISO-8859-1");
			// Skip first line which is a header line
			if (lineIterator.hasNext()) {

				line = lineIterator.nextLine();
				if (!line.startsWith("\"Film\";\"") && !line.startsWith("Film;")) {
					globalMessages.add("#1: Sidhuvud ej funnet på första raden i styrfilen. Första raden var: "+line);
					LOG.error("No header found on the first line in the document. First line was: "
							+ line +". Aborting...");
					return result;
				} else {
					LOG.debug("Line #"+lineNumber+": Skipping header");
				}
			}
			while (lineIterator.hasNext()) {
				lineNumber++;
				line = lineIterator.nextLine();
				// if it's an empty line or a comment, skip
				if (!StringUtils.hasText(line) || line.startsWith("#")) {
					globalMessages.add("#"+lineNumber+": Tom rad, eller bortkommenterad rad funnel, skippas");
					LOG.info("Line #"+lineNumber+": Skipping comment or empty line");
					continue;
				}
				// Validation and error handling
				ByggRedaDocument document = parseAndValidate(line);
				document.setLineNumber(lineNumber);
				if (!document.isReadSuccessfully()) {
					// An error occured, we need to log this
					LOG.error("Line #"+document.getLineNumber()+": "+document.getStatusMsg());
				} else {
					// Document successfully read
					LOG.debug("Line #"+document.getLineNumber()+": "+"Successfully read record. , Record number: "
							+ document.getRecordDisplay());
				}
				result.add(document);
			}
		} catch (final Exception ex) {
			globalMessages.add("#"+lineNumber+ ": Fel vid inläsning av rad "+lineNumber+" från styrfil. Radens innehåll: "+line+" Systemmeddelande: "+ex.getMessage());
			LOG.error("Error on line '" + lineNumber + "'. Line contents: "+line, ex);
		}  finally {
			IOUtils.closeQuietly(inputStream);
			LineIterator.closeQuietly(lineIterator);
		}
		return result;
	}

	/**
	 * Parse and validate a single line in a metadata document for ByggReda
	 * 
	 * @param line
	 * @return
	 */
	private static ByggRedaDocument parseAndValidate(String line) {
		ByggRedaDocument document = new ByggRedaDocument();
		String[] parts = StringUtils.delimitedListToStringArray(line,
				"\";\"");
		if (parts.length <= 1) {
			//If parts were not found, try without quotes
			parts = StringUtils.delimitedListToStringArray(line, ";");
		}
		document.setReadSuccessfully(false);
		
		for (String part : parts) {
			if (part.indexOf(";")!=-1) {
				document.setStatusMsg("Semikolon påträffades i något av fälten på raden");
				return document;
			}
		}
		
		// Verify that all parts were extracted
		if (parts.length != 14) {
			document.setStatusMsg("Fel antal delar i rad. Antal delar funna:"
              + parts.length + " Förväntat antal delar: " + EXPECTED_NUM_PARTS
              + " Rad: " + line);
		} else {
			// All parts were extracted successfully
			int i = 0;
			document.setReadSuccessfully(true);
			document.setFilm(parts[i++]); 
			if (document.getFilm().indexOf("\"")==0) {
				// Remove prefix "
				document.setFilm(document.getFilm().substring(1));
			}				
			document.setSerialNumber(parts[i++]);
			try {
				document.setRecordYear((Integer) Integer.parseInt(parts[i++]));
			} catch (NumberFormatException e) {
				document.setReadSuccessfully(false);
				document.setStatusMsg("Ogiltigt diarieår");
				return document;
			}
			document.setRecordNumber(parts[i++]);
			if (!StringUtils.hasText(document.getRecordNumber())) {
				document.setReadSuccessfully(false);
				document.setStatusMsg("Diarienummer saknas");
				return document;
			}
			document.setRecordDisplay(document.getRecordYear() + RECORD_DISPLAY_SEPARATOR + document.getRecordNumber()); 
			document.setBuildingDescription(parts[i++]);
			if (!StringUtils.hasText(document.getBuildingDescription())) {
				document.setReadSuccessfully(false);
				document.setStatusMsg("Fastighetsbeteckning saknas");
				return document;
			}
			document.setLastBuildingDescription(parts[i++]);
			document.setAddress(parts[i++]);
			document.setLastAddress(parts[i++]);
			document.setDecision(parts[i++]);
			document.setForA(parts[i++]);
			document.setIssuePurpose(parts[i++]);
			document.setNote(parts[i++]);
			document.setRecords(parts[i++]);
			document.setFileName(parts[i]);
			if (!StringUtils.hasText(document.getFileName())) {
				document.setReadSuccessfully(false);
				document.setStatusMsg("Filnamn saknas");
				return document;
			}
			if (document.getFileName().indexOf("\"") ==(document.getFileName().length()-1)) {
				// Remove postfix " from last part
				document.setFileName(document.getFileName().substring(0, document.getFileName().length() - 1));
			}
			document.setFileName(CommonFileUtil.parseValidFileName(document.getFileName()));
			document.setMimetype(CommonFileUtil.getMimetypeByExtension(FilenameUtils.getExtension(document.getFileName())));
			String originalPart1 = document.getBuildingDescription().substring(0, 1).toUpperCase();
			String part1 = CommonFileUtil.parseValidFileName(originalPart1);
			String originalPart2 = document.getBuildingDescription().toUpperCase();
			String part2 = CommonFileUtil.parseValidFileName(originalPart2);
			String originalPart3 = document.getRecordDisplay() + " "+ document.getIssuePurpose().toUpperCase();
			String part3 = CommonFileUtil.parseValidFileName(originalPart3);
			document.setPath(part1 + "/" + part2 + "/"+ part3);
			document.setOriginalPath(originalPart1 +"#/#"+originalPart2+"#/#"+originalPart3);
			document.setTitle(document.getRecordDisplay() + " " + document.getIssuePurpose());
		}
		return document;
	}
}
