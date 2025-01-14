<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
//@overridden projects/slingshot/config/alfresco/site-webscripts/org/alfresco/components/invite/members-bar.get.js

function akMain()
{
	model.membershipRequestsExists=false;
	model.hiddenTaskTypes = getHiddenTaskTypes();
	
	var obj = null;
	var url = "/api/task-instances?authority=" + "GROUP_site_"+page.url.templateArgs.site+"_SiteManager" +
    "&properties=" + ["bpm_priority", "bpm_status", "bpm_dueDate", "bpm_description"].join(",") +
    "&exclude=" + model.hiddenTaskTypes.join(",") + "&skipCount=0&maxItems=50";
	var totalItems = 0;
	var json = remote.call(url);
	   if (json.status == 200)
	   {
	      obj = JSON.parse(json);
	   }
	   if (obj && obj.paging && obj.paging.totalItems > 0)
	   {
	      model.membershipRequestsExists = true;
	      totalItems = obj.paging.totalItems;
	   }

   // Add Membership Requests link (if user is manager)
   if (model.isManager)
   {
	   // If membership requests exist, write out a * after title
	   if(model.membershipRequestsExists){
		      model.links.push(
		    	      {
		    	         id: "membership-requests-link",
		    	         href: "membership-requests",
		    	         cssClass: (model.activePage == "membership-requests") ? "theme-color-4" : null,
		    	         label: msg.get("link.membership-requests") + " (" +totalItems+ ")"
		    	      });
	   }
	   else{
		      model.links.push(
		    	      {
		    	         id: "membership-requests-link",
		    	         href: "membership-requests",
		    	         cssClass: (model.activePage == "membership-requests") ? "theme-color-4" : null,
		    	         label: msg.get("link.membership-requests")
		    	      });
	   }

   }
}
akMain();