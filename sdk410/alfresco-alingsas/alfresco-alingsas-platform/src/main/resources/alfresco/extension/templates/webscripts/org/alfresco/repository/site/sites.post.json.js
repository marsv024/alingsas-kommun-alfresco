<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/repository/site/sites.post.json.js">
//@overridden projects/remote-api/config/alfresco/templates/webscripts/org/alfresco/repository/site/sites.post.json.js
function alingsas_extension() {
  var site = model.site;
  var props = new Array();
  var node = site.node;
  var folderTemplate = null;
  if(json.has("siteFolderTemplate") && site){
    folderTemplate = json.get("siteFolderTemplate");
    if(folderTemplate != ""){
      props["alingsas:siteFolderTemplate"] = folderTemplate;
      site.node.addAspect("alingsas:siteFolderTemplateAspect",props);
      site.node.save();
      site.save();
      model.site = site;
    }
  }

    if(site && node.properties["st:sitePreset"] === "ak-dm-secrecy"){
        props["stcp:publishDocument"] = false;
        props["stcp:presetValue"] = "akdm_documentmanagement.property.stcp_presetValue.secrecy.description";
        node.addAspect("akdm:siteSecrecyAspect",props);
        node.save();
    }

    if(site && node.properties["st:sitePreset"] === "ak-dm-public"){
        props["stcp:publishDocument"] = true;
        props["stcp:presetValue"] = "akdm_documentmanagement.property.stcp_presetValue.public.description";
        node.addAspect("akdm:siteSecrecyAspect",props);
        node.save();
    }
}

// First main() will run in sites.post.json.js. This creates the site. 

alingsas_extension();
