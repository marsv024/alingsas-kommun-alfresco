function main()
{
   var nodeRef = args.nodeRef;

   model.linkButtons = [];

   // Login link
   if (user && user.isGuest)
   {
      model.linkButtons.push({
         id: "login",
         href: url.context,
         label: msg.get("button.login"),
         cssClass: "brand-bgcolor-2"
      });
   }
   if (args.loginLink == "document-details")
   {
      var result = remote.connect("alfresco").get("/api/sites/shareUrl?nodeRef=" + nodeRef);
      if (result.status == 200)
      {
         var nodeMetadata = JSON.parse(result);
         if (nodeMetadata.site != null)
         {
            model.linkButtons.push({
               id: "document-details",
               href: url.context + "/page/site/"+ nodeMetadata.site +"/document-details?nodeRef=" + args.nodeRef,
               label: (user && user.isGuest) ? msg.get("button.login") : msg.get("button.document-details"),
               cssClass: "brand-bgcolor-2"
            });
         }
      }
   }
}

main();
