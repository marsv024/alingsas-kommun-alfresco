

function hideThemesForCommonUsers() {
  result = remote.call("/alingsas/user/editthemeenabled");
  // Check if user is allowed to create a site, if they are they should also be able to change themes.
  var canChangeTheme = (result.status == 200 && result == "true");
  if (!canChangeTheme) {
    for (var i=0; i<model.themes.length; i++) {
      if (model.themes[i].selected!==true) {
        model.themes.splice(i,1);
        i--;
        continue;
      }
    }
  }
}
hideThemesForCommonUsers();