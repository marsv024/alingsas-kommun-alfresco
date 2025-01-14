<#-- @overridden projects/slingshot/config/alfresco/site-webscripts/org/alfresco/components/workflow/start-workflow.get.html.ftl -->
<#-- Adds functionality to show a "start workflow form" immediatly -->
<@markup id="ak-js" target="js" action="after">
  <@script src="${url.context}/res/ak/components/workflow/start-workflow.js" group="workflow" />
  <@script src="${url.context}/res/ak/components/object-finder/object-finder.js" group="workflow" />
</@>

