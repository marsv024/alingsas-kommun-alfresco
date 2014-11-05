<#-- @overridden projects/slingshot/config/alfresco/site-webscripts/org/alfresco/components/document-details/document-workflows.get.html.ftl -->
<#if workflows??>
   <#include "../../include/alfresco-macros.lib.ftl" />
   <#assign el=args.htmlid?js_string>
   <script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentWorkflows("${el}").setOptions(
   {
      nodeRef: "${nodeRef?js_string}",
      siteId: <#if site??>"${site?js_string}"<#else>null</#if>,
      destination: "${destination}"
   }).setMessages(
      ${messages}
   );
   //]]></script>

   <div id="${el}-body" class="document-workflows document-details-panel">

      <h2 id="${el}-heading" class="thin dark">
         ${msg("header.workflows")}
         <span class="alfresco-twister-actions">
         <!-- Disable assign workflow button
            <a href="#" name=".onAssignWorkflowClick" class="${el} edit" title="${msg("label.assignWorkflow")}">&nbsp;</a>
             -->
         </span>
      </h2>

      <div class="panel-body">
         <div class="info">
         <#if workflows?size == 0>
            ${msg("label.partOfNoWorkflows")}
         <#else>
            ${msg("label.partOfWorkflows")}
         </#if>
         </div>

         <#if workflows?size &gt; 0>
         <hr/>
         <div class="workflows">
            <#list workflows as workflow>
               <div class="workflow <#if !workflow_has_next>workflow-last</#if>">
               <!-- Bugfix for missing initiators -->
                  <#if workflow.initiator?? && workflow.initiator.avatarUrl??>
                  <img src="${url.context}/proxy/alfresco/${workflow.initiator.avatarUrl}" alt="${msg("label.avatar")}"/>
                  <#else>
                  <img src="${url.context}/components/images/no-user-photo-64.png" alt="${msg("label.avatar")}"/>
                  </#if>
                  <a href="${siteURL("workflow-details?workflowId=" + workflow.id?js_string + "&nodeRef=" + (args.nodeRef!"")?js_string)}"><#if workflow.message?? && workflow.message?length &gt; 0>${workflow.message?html}<#else>${msg("workflow.no_message")?html}</#if></a>
                  <div class="title">${workflow.title?html}</div>
                  <div class="clear"></div>
               </div>
            </#list>
         </div>
         </#if>

      </div>

      <script type="text/javascript">//<![CDATA[
         Alfresco.util.createTwister("${el}-heading", "DocumentWorkflows");
      //]]></script>

   </div>
</#if>