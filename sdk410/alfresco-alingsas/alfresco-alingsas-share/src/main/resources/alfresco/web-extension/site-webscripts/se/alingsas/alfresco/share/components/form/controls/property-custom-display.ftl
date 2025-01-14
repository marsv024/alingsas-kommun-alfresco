<#-- @overridden projects/web-framework-commons/config/alfresco/site-webscripts/org/alfresco/components/form/controls/selectone.ftl -->
<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#if field.control.params.optionSeparator??>
   <#assign optionSeparator=field.control.params.optionSeparator>
<#else>
   <#assign optionSeparator=",">
</#if>
<#if field.control.params.labelSeparator??>
   <#assign labelSeparator=field.control.params.labelSeparator>
<#else>
   <#assign labelSeparator="|">
</#if>
	<#assign fieldValue=field.value>
  <div class="viewmode-field">
     <#if field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
        <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
     </#if>
     <span class="viewmode-label">${field.label?html}:</span>
     <#if fieldValue?string == "">
     	<#if field.control.params.blankValueMapping?? && field.control.params.blankValueMapping != "">
     		<#assign nameValue=field.control.params.blankValueMapping>
       		<#assign choice=nameValue?split(labelSeparator)>
       		<#assign valueToShow=msgValue(choice[1])>
        <#else>
      		<#assign valueToShow=msg("form.control.novalue")>
        </#if>
     <#else>
        <#assign valueToShow=fieldValue>
        <#if field.control.params.nameValueMappings?? && field.control.params.nameValueMappings != "">
           <#list field.control.params.nameValueMappings?split(optionSeparator) as nameValue>
              <#if nameValue?index_of(labelSeparator) == -1>
                 <#if nameValue == fieldValue?string || (fieldValue?is_number && fieldValue?c == nameValue)>
                    <#assign valueToShow=nameValue>
                    <#break>
                 </#if>
              <#else>
                 <#assign choice=nameValue?split(labelSeparator)>
                 <#if choice[0] == fieldValue?string || (fieldValue?is_number && fieldValue?c == choice[0])>
                    <#assign valueToShow=msgValue(choice[1])>
                    <#break>
                 </#if>
              </#if>
           </#list>
        </#if>
     </#if>
     <span class="viewmode-value">${valueToShow?html}</span>
  </div>
