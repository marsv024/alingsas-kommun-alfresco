
function main()
{
   // Make the shareid available in token resolution when the components are bound into the page
   // I.e. <param>{shareid}</param>
   var nodeRef = page.url.args.nodeRef;
   context.attributes.nodeRef = nodeRef;
   if (nodeRef==null) {
     nodeRef = "";
   }
   // Check of the shareId exists
   var result = remote.connect("alfresco").get("/api/node/" + nodeRef.replace(/:\//g, "") + "/metadata");
   model.outcome = result.status == 200 ? "" : "error";
}

main();
