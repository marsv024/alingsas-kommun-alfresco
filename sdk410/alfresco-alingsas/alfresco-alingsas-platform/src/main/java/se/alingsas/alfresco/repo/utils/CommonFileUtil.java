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

package se.alingsas.alfresco.repo.utils;

import org.springframework.util.StringUtils;

public class CommonFileUtil {

	/**
	 * Get mimetype by a files extension
	 * 
	 * @param extension The extension
	 * @return The minetype
	 */
	public static String getMimetypeByExtension(final String extension) {
		String mimetype;

		if (!StringUtils.hasText(extension)) {
			mimetype = "application/octet-stream";
		} else if (extension.equalsIgnoreCase("pdf")) {
			mimetype = "application/pdf";
		} else if (extension.equalsIgnoreCase("ppt")) {
			mimetype = "application/vnd.ms-powerpoint";
		} else if (extension.equalsIgnoreCase("doc")) {
			mimetype = "application/msword";
		} else if (extension.equalsIgnoreCase("vsd")) {
			mimetype = "application/visio";
		} else if (extension.equalsIgnoreCase("bmp")) {
			mimetype = "image/bmp";
		} else if (extension.equalsIgnoreCase("htm")) {
			mimetype = "text/html";
		} else if (extension.equalsIgnoreCase("html")) {
			mimetype = "text/html";
		} else if (extension.equalsIgnoreCase("dot")) {
			mimetype = "application/msword";
		} else if (extension.equalsIgnoreCase("xls")) {
			mimetype = "application/vnd.ms-excel";
		} else if (extension.equalsIgnoreCase("jpg")) {
			mimetype = "image/jpeg";
		} else if (extension.equalsIgnoreCase("pot")) {
			mimetype = "application/vnd.ms-powerpoint";
		} else if (extension.equalsIgnoreCase("rtf")) {
			mimetype = "application/rtf";
		} else if (extension.equalsIgnoreCase("eps")) {
			mimetype = "application/eps";
		} else if (extension.equalsIgnoreCase("zip")) {
			mimetype = "application/zip";
		} else if (extension.equalsIgnoreCase("txt")) {
			mimetype = "text/plain";
		} else if (extension.equalsIgnoreCase("text")) {
			mimetype = "text/plain";
		} else if (extension.equalsIgnoreCase("log")) {
			mimetype = "text/plain";
		} else if (extension.equalsIgnoreCase("odt")) {
			mimetype = "application/vnd.oasis.opendocument.text";
		} else if (extension.equalsIgnoreCase("ods")) {
			mimetype = "application/vnd.oasis.opendocument.spreadsheet";
		} else if (extension.equalsIgnoreCase("odp")) {
			mimetype = "application/vnd.oasis.opendocument.presentation";
		} else if (extension.equalsIgnoreCase("ott")) {
			mimetype = "application/vnd.oasis.opendocument.formula-template";
		} else if (extension.equalsIgnoreCase("tif")) {
			mimetype = "image/tiff";
		} else if (extension.equalsIgnoreCase("tiff")) {
			mimetype = "image/tiff";
		} else if (extension.equalsIgnoreCase("pcx")) {
			mimetype = "image/pcx";
		} else if (extension.equalsIgnoreCase("docx")) {
			mimetype = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		} else if (extension.equalsIgnoreCase("xlsx")) {
			mimetype = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		} else if (extension.equalsIgnoreCase("pptx")) {
			mimetype = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
		} else if (extension.equalsIgnoreCase("nal")) {
			mimetype = "application/octet-stream";
		} else if (extension.equalsIgnoreCase("indd")) {
			mimetype = "application/ocet-stream";
		} else if (extension.equalsIgnoreCase("pmd")) {
			mimetype = "application/ocet-stream";
		} else if (extension.equalsIgnoreCase("mht")) {
			mimetype = "application/ocet-stream";
		} else if (extension.equalsIgnoreCase("lnk")) {
			mimetype = "application/ocet-stream";
		}
		
				
				
		
		

		else {
			throw new RuntimeException("The extension '" + extension
					+ "' has no configured mimetype.");
		}

		return mimetype;
	}

	public static String parseValidFileName(String fileName) {
		while (fileName.endsWith(".")) {
			fileName = fileName.substring(0, fileName.length()-1);
			fileName = fileName.trim();
		}
		fileName = fileName.replace("/", " ");
		fileName = fileName.replace("\\", " ");
		fileName = fileName.replace(":", " ");
		fileName = fileName.replace("\"", "\'");
		fileName = fileName.replace("?", "");
		fileName = fileName.replace("*", "");
		fileName = fileName.replace("|", " ");
		fileName = fileName.replace("<", " ");
		fileName = fileName.replace(">", " ");
		while (fileName.indexOf("  ")!=-1) {
			fileName = fileName.replace("  ", " ");
		}
		fileName = fileName.trim();
		while (fileName.endsWith(".")) {
			fileName = fileName.substring(0, fileName.length()-1);
			fileName = fileName.trim();
		}
		return fileName;
	}
}
