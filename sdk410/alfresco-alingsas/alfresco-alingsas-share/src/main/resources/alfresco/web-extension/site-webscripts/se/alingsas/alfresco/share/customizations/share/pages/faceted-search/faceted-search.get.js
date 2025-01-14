<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-footer.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/config.lib.js">

// This will cause issues due to old incompatible data in the search index
// Removing search of calendar related metadata
function disable_highlighting_of_unused_fields() {
  var searchService = widgetUtils.findObject(model.jsonModel, "name", "alfresco/services/SearchService");
  if (searchService) {
    searchService.config.highlightFields = "cm:name,cm:description,cm:title,content";
  }
}
disable_highlighting_of_unused_fields();