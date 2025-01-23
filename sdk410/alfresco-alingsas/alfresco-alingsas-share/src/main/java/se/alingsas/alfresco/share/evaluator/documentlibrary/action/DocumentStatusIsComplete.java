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

package se.alingsas.alfresco.share.evaluator.documentlibrary.action;

/**
 * @author Marcus Svensson - Redpill Linpro AB
 *
 */

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;

/**
 * Evaluator to determine if a document is completed or not
 * 
 * @author Marcus Svensson - Redpill Linpro AB
 * 
 */
public class DocumentStatusIsComplete extends BaseEvaluator {

	private static final String DOC_STATUS_PROPERTY = "akdm:documentStatus";
	private static final String DOC_STATUS_COMPLETE_VALUE = "Färdigt dokument";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.alfresco.web.evaluator.BaseEvaluator#evaluate(org.json.simple.JSONObject
	 * )
	 */
	@Override
	public boolean evaluate(JSONObject jsonObject) {
		try {
			String property = (String) getProperty(jsonObject,
					DOC_STATUS_PROPERTY);

			if (DOC_STATUS_COMPLETE_VALUE.equalsIgnoreCase(property)) {
				return true;
			}
			return false;
		} catch (Exception err) {
			throw new AlfrescoRuntimeException(
					"JSONException whilst running action evaluator: "
							+ err.getMessage());
		}
	}

}
