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

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Validate that a document is of the correct type, all Alingsås types have the akdm:commonAspect applied to them
 * @author Marcus Svensson - Redpill Linpro AB
 * 
 */
public class IsAlingsasDocument extends BaseEvaluator {

	private static final String ASPECT_AK_COMMON = "akdm:commonAspect";

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
			JSONArray nodeAspects = getNodeAspects(jsonObject);

			if (nodeAspects == null) {
				return false;
			} else {
				if (nodeAspects.contains(ASPECT_AK_COMMON)) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception err) {
			throw new AlfrescoRuntimeException(
					"JSONException whilst running action evaluator: "
							+ err.getMessage());
		}
	}

}