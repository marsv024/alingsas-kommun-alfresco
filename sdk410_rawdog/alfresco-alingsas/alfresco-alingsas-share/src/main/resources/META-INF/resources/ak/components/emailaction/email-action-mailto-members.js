(function() {
  YAHOO.Bubbling.fire("registerAction", {
      actionName: "onActionMultiEmailMembers",
      fn: function multiEmail(record, owner)
      {
        var payload = [];
        var site = "";
        if(Array.isArray(record)){
          for(var i = 0; i < record.length; i++){
           site = record[i].location.site.name;
           payload[i] = {
             nodeRef: record[i].nodeRef,
             fileName: record[i].fileName
           };
          }
        }
        else{
         site = record.location.site.name;
         payload =[{
                 nodeRef: record.nodeRef,
                 fileName: record.fileName
               }];
        }
        var body = "";
        var subject = "";
        var currentBrowserBaseUrl = window.location.protocol + "//" +window.location.host;
        for (var i = 0; i< payload.length; i++) {
          if (body.length > 0) {
            body += " \n"
            subject += ", "
          }
          body += payload[i].fileName + " - " + Alfresco.util.combinePaths(currentBrowserBaseUrl, Alfresco.util.siteURL("document-details?nodeRef=" + payload[i].nodeRef));
          subject += payload[i].fileName;
        }


       Alfresco.util.Ajax.jsonGet({
          url:Alfresco.constants.PROXY_URI + "alingsas/site/member-emails?siteName=" + site,
          successCallback:{fn:function(res){
            var emails = res.json.emails;
            var currentMailToLink = "mailto:" + encodeURIComponent(emails) + "?subject=" + encodeURIComponent(subject) + "&body=" + encodeURIComponent(body)
            window.open(currentMailToLink);
          }},
          failureCallback: {fn:function(res){
            popup.hide();
            Alfresco.util.PopupManager.displayMessage({
              text:Alfresco.util.message("Failed to send email")
            });
          }}
       });
      }

    });



})();


