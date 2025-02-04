// @overridden projects/slingshot/source/web/components/data-lists/datalists.js
/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
(function (_populateDataLists) {
  var $html = Alfresco.util.encodeHTML, $combine = Alfresco.util.combinePaths;
  /**
   * Retrieves the Data Lists from the Repo
   * 
   * Reverses the data list listing. Newest first.
   *
   * @method populateDataLists
   * @param callback {Object} Optional callback literal {fn, scope, obj} whose function is invoked once list has been retrieved
   */
  Alfresco.component.DataLists.prototype.populateDataLists = function AK_DataLists_populateDataLists(p_callback)
  {
    /**
     * Success handler for Data Lists request
     * @method fnSuccess
     * @param response {Object} Ajax response object literal
     * @param obj {Object} Callback object from original function call
     */
    var fnSuccess = function DataLists_pDL_fnSuccess(response, p_obj)
    {
      var lists = response.json.datalists,
              list;

      this.dataLists = {};
      this.containerNodeRef = new Alfresco.util.NodeRef(response.json.container);
      this.widgets.newList.set("disabled", !response.json.permissions.create);

      function compare(a,b) {
        if (a.title < b.title)
          return 1;
        if (a.title > b.title)
          return -1;
        return 0;
      }

      lists.sort(compare);

      for (var i = lists.length-1; i >= 0; i--)
      {
        list = lists[i];
        this.dataLists[list.name] = list;
      }
      this.dataListsLength = lists.length;

      if (p_callback && (typeof p_callback.fn == "function"))
      {
        p_callback.fn.call(p_callback.scope || this, p_callback.obj);
      }
    };

    /**
     * Failure handler for Data Lists request
     * @method fnFailure
     * @param response {Object} Ajax response object literal
     */
    var fnFailure = function DataLists_pDL_fnFailure(response)
    {
      if (response.status == 401)
      {
        // Our session has likely timed-out, so refresh to offer the login page
        window.location.reload();
      } else
      {
        this.dataLists = null;
        this.containerNodeRef = null;
        this.widgets.newList.set("disabled", true);
        var errorMsg = "";
        try
        {
          errorMsg = $html(YAHOO.lang.JSON.parse(response.responseText).message);
        } catch (e)
        {
          errorMsg = this.msg("message.error-unknown");
        }
      }
    };

    Alfresco.util.Ajax.jsonGet(
            {
              url: $combine(Alfresco.constants.PROXY_URI, "slingshot/datalists/lists/site", this.options.siteId, this.options.containerId),
              successCallback:
                      {
                        fn: fnSuccess,
                        obj: p_callback,
                        scope: this
                      },
              failureCallback:
                      {
                        fn: fnFailure,
                        scope: this
                      }
            });
  };
})(Alfresco.component.DataLists.prototype.populateDataLists);