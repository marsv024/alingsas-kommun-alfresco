// @override projects/slingshot/config/alfresco/site-webscripts/org/alfresco/share/header/share-header.get.js 

function hideSharedFiles() {
  widgetUtils.deleteObjectFromArray(model.jsonModel, "id", "HEADER_SHARED_FILES");
}

hideSharedFiles();