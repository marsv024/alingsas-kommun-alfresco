<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="Generator" content="Alfresco Repository">
		<title>${msg("byggreda.import.header.title")}</title>
		<style type="text/css">
body {
	margin: 3em;
	font-family: arial, helvetica, clean, sans-serif;
}

div.header {
	background: #56A3D9;
}

h1 {
	color: white;
	font-size: 1.3em;
	padding: 5px 6px 3px;
}
span.help {
    display: block;
    font-size: small;
}

div {
    margin-bottom: 10px;
}
</style>
</head>
<body>
	<img src="/alfresco/images/logo/alfresco3d.jpg"
		alt="Alfresco" /> <#if allowAccess>
	<div id="formContainer">
		<form method="post" onreset="document.getElementById('submitButton').disabled = 0;" onsubmit="document.getElementById('submitButton').disabled = 1;">
			<div>${msg("byggreda.import.form.title")}</div>
			<div>				
				<label for="sourcePath">${msg("byggreda.import.form.sourcePath.title")}</label>
				<input type="text" name="sourcePath" />
				<span class="help">${msg("byggreda.import.form.sourcePath.help")}</span>
			</div>
			<div>				
				<label for="metaFileName">${msg("byggreda.import.form.metaFileName.title")}</label>
				<input type="text" name="metaFileName" />
				<span class="help">${msg("byggreda.import.form.metaFileName.help")}</span>
			</div>
			<div>				
				<label for="metaFileName">${msg("byggreda.import.form.updateIfExists.title")}</label>
				<input type="checkbox" name="updateIfExists" value="yes" />
				<span class="help">${msg("byggreda.import.form.updateIfExists.help")}</span>
			</div>			
			<input type="submit" id="submitButton" value="${msg("byggreda.import.form.submit.title")}" />
			<input type="reset" value="${msg("byggreda.import.form.reset.title")}" />
			<span class="help">${msg("byggreda.import.form.submit.help")}</span>
		</form>
	</div>
	<#else> ${msg("byggreda.import.accessDenied")} </#if>

</body>
</html>