// @overridden projects/slingshot/config/alfresco/site-webscripts/org/alfresco/components/workflow/start-workflow.get.js

function ak_addstartarguments_main() {
   var startWorkflow = widgetUtils.findObject(model.widgets, "id", "StartWorkflow");
   startWorkflow.options.startWorkflow = (page.url.args.startWorkflow != null) ? page.url.args.startWorkflow : "";

   if (page.url.args.startWorkflow != null) {
      for (var i=0;i<model.workflowDefinitions.length;i++) {
         var workflowDefinition = model.workflowDefinitions[i];
         if (workflowDefinition.name === page.url.args.startWorkflow) {
            //model.workflowdefinitions = new Array();
            for (var j=0;j<model.workflowDefinitions.length;i++) {
               model.workflowDefinitions.pop();
            }
            model.workflowDefinitions.push(workflowDefinition);
            break;
         }
      }
   }
}

ak_addstartarguments_main();