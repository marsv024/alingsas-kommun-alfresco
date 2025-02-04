
//@overridden projects/web-framework-commons/source/web/components/documentlibrary/global-folder.js

/**
 #Filter out all site descriptions to reduce size of pickers
 */
(function (_onSiteChanged) {
  $hasEventInterest = Alfresco.util.hasEventInterest;

  Alfresco.module.DoclibGlobalFolder.prototype.onSiteChanged = function AK_DLGF_onSiteChanged(layer, args) {
    _onSiteChanged.call(this, layer, args);
    // Check the event is directed towards this instance
    if ($hasEventInterest(this, args))
    {
      var siteDescriptions = Selector.query("span", this.id + "-sitePicker");
      for (var i = 0; i < siteDescriptions.length; i++) {
        siteDescriptions[i].innerHTML="";
      }
    }
  }
}(Alfresco.module.DoclibGlobalFolder.prototype.onSiteChanged));

 