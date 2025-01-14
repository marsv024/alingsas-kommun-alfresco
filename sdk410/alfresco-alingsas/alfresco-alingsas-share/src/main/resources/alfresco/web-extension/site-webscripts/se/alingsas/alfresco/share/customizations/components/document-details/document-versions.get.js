// @overridden projects/slingshot/config/alfresco/site-webscripts/org/alfresco/components/document-details/document-versions.get.js

function ak_disable_upload_new_version() {
	model.allowNewVersionUpload = false;
}

ak_disable_upload_new_version();