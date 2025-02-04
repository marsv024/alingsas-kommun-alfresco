//@overridden projects/web-framework-commons/source/web/components/object-finder/object-finder.js

/**
 Add support for association-based-on-field.ftl
 */
(function(_onReady, _onAddButtonClick) {
    /**     
      * Specifies a field id which root and startlocation should be based on, 
      * this field id should contain a nodeRef should be read when button for 
      * opening the picker is clicked and value should be  written into 
      * rootNode and startLocation
      */
	Alfresco.ObjectFinder.prototype.options.basedOnField = null;
	/**
      * If selection should be based on a specific field value
      */
	Alfresco.ObjectFinder.prototype.options.basedOnFieldValue = null;
	/**
      * Use basedOnFieldValue as root & start
      */
	Alfresco.ObjectFinder.prototype.options.useBasedOnFieldValueAsRoot = true;
	/**
	  * Label field for failed select action based on field
	  */
	Alfresco.ObjectFinder.prototype.options.selectActionFailedBasedOnFieldLabelId = "form.control.object-picker.button.select-target-folder-fail";

	Alfresco.ObjectFinder.prototype.onReady = function() {
		this.handleBasedOnValue();
		_onReady.call(this)
	}

	Alfresco.ObjectFinder.prototype.handleBasedOnValue = function ObjectFinder_handleBasedOnValue() {
		var currentBasedOnFieldValue = null;
	 	if (this.options.basedOnField!=null) {
	  		currentBasedOnFieldValue = Dom.get(this.options.basedOnField).value;
		  	if (currentBasedOnFieldValue!==null && currentBasedOnFieldValue!=="" && this.options.useBasedOnFieldValueAsRoot===true) {
		  		this.options.rootNode = currentBasedOnFieldValue;
		  		this.options.startLocation = currentBasedOnFieldValue;
		  	}
		 	this.setOptions(this.options);
	  	}	    	  
	  	return (currentBasedOnFieldValue!==null && currentBasedOnFieldValue!=="") && (this.options.basedOnFieldValue==null || (this.options.basedOnFieldValue!=null && this.options.basedOnFieldValue==currentBasedOnFieldValue));
	}

	Alfresco.ObjectFinder.prototype.onAddButtonClick = function(e, p_obj) {
		this.handleBasedOnValue();
		this.options.objectRenderer.startLocationResolved = false;
		_onAddButtonClick.call(this, e, p_obj);
	}

}(Alfresco.ObjectFinder.prototype.onReady, Alfresco.ObjectFinder.prototype.onAddButtonClick));

