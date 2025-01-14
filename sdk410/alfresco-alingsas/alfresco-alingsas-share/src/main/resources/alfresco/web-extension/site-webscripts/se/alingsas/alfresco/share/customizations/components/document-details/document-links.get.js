<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function custom_documentlinks() {
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (documentDetails)
   {
      model.document = documentDetails.item;
      model.repositoryUrl = AlfrescoUtil.getRepositoryUrl();
   }

   // Widget instantiation metadata...
   var documentActions = {
      id: "CustomDocumentLinks",
      name: "Alfresco.CustomDocumentLinks",
      options: {
         nodeRef: model.nodeRef,
         siteId: model.site,
         fileName: (model.document!==null)?model.document.fileName:"",
      }
   };
   model.widgets = [documentActions];
}
custom_documentlinks();
