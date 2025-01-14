<#-- @overridden projects/slingshot/config/alfresco/site-webscripts/org/alfresco/components/workflow/workflow-list-toolbar.get.html.ftl -->

<@markup id="ak-html" target="html" action="replace">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="workflow-list-toolbar toolbar">
         <div id="${el}-headerBar" class="header-bar flat-button theme-bg-2">
            <div class="left">
               <div class="hideable hidden">
<!-- Alingsås kommun, hide this 
                  <div class="start-workflow"><button id="${el}-startWorkflow-button" name="startWorkflow">${msg("button.startWorkflow")}</button></div>
--> 
              </div>
            </div>
            <div class="right">
            </div>
         </div>
      </div>
   </@>
</@>