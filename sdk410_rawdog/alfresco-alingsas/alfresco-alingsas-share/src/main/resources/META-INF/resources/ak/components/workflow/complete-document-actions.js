/*
 * Copyright (C) 2012-2013 Alingsås Kommun
 *
 * This file is part of Alfresco customizations made for Alingsås Kommun
 *
 * The Alfresco customizations made for Alingsås Kommun is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Alfresco customizations made for Alingsås Kommun is distributed in the 
 * hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the Alfresco customizations made for Alingsås Kommun. 
 * If not, see <http://www.gnu.org/licenses/>.
 */

(function() {

  YAHOO.Bubbling
      .fire(
          "registerAction",
          {
            actionName : "onActionCompleteDocumentAction",
            fn : function onActionCompleteDocumentAction(record, owner) {

              var me = this, displayName = record.displayName, nodeRef = record.nodeRef, parentNodeRef = record.parent.nodeRef, action = Alfresco.doclib.Actions.prototype
                  .getAction(record, owner), params = action.params;

              var documentSecrecy;
              if (record.jsNode.properties["akdm:documentSecrecy"] != undefined) {
                documentSecrecy = record.jsNode.properties["akdm:documentSecrecy"];
              } else {
                documentSecrecy = null;
              }

              var siteName = null;
              if (siteName = record.location.site != undefined) {
                siteName = record.location.site.name;
              } else {
                if (documentSecrecy == null) {
                  Alfresco.util.PopupManager.displayMessage({
                    text : me.msg("message.completedocument.nosecrecyset")
                  });
                } else {
                  location.href = "start-workflow?selectedItems=" + nodeRef
                      + "&startWorkflow=" + params.workflow + "";
                }
                return;
              }

              Alfresco.util.PopupManager
                  .displayPrompt({
                    title : this.msg("actions.complete-document.title"),
                    noEscape : true,
                    text : this.msg("message.complete-document.message",
                        displayName),
                    buttons : [
                        {
                          text : this
                              .msg("button.complete-document.completehere"),
                          handler : function dlA_onActionCompleteDocument_completehere() {

                            if (documentSecrecy == null) {
                              var _ak_parent = this;
                              Alfresco.util.PopupManager
                                  .getUserInput({
                                    title : me
                                        .msg("actions.complete-document.missing-secrecy.title"),
                                    noEscape : true,
                                    html : me
                                        .msg(
                                            "message.complete-document.missing-secrecy.message",
                                            displayName)
                                        + '<select id="complete_document_secrecy_id" name="secrecy"><option value="Offentligt">Offentligt</option><option value="Sekretessbelagt">Sekretessbelagt</option></select>',
                                    value : "Dummy",
                                    buttons : [
                                        {
                                          text : me
                                              .msg("button.complete-document.missing-secrecy.ok"),
                                          handler : function dlA_onActionCompleteDocument_missingSecrecy_ok() {
                                            documentSecrecy = document
                                                .getElementById("complete_document_secrecy_id").value;
                                            this.destroy();
                                            // Click the ok button of the parent
                                            // form.
                                            _ak_parent.getButtons()[0]._button
                                                .click();
                                          }
                                        },
                                        {
                                          text : me.msg("button.cancel"),
                                          handler : function dlA_onActionCompleteDocument_missingSecrecy_cancel() {
                                            this.destroy();
                                          },
                                          isDefault : true
                                        } ]

                                  });
                            } else {

                              if (documentSecrecy != null) {
                                this.destroy();
                                Alfresco.util.Ajax
                                    .request({
                                      url : Alfresco.constants.PROXY_URI
                                          + "alingsas/workflow/completedocument/"
                                          + params.workflow + "/"
                                          + record.jsNode.nodeRef.uri + "/"
                                          + documentSecrecy + "/" + siteName
                                          + "/"
                                          + parentNodeRef.replace("://", "/"),
                                      method : Alfresco.util.Ajax.POST,
                                      successCallback : {
                                        fn : function completeDocument_onCompleteDocument_success() {
                                          YAHOO.Bubbling
                                              .fire("metadataRefresh");
                                          Alfresco.util.PopupManager
                                              .displayMessage({
                                                text : me
                                                    .msg("message.completedocument.success")
                                              })
                                        },
                                        scope : me
                                      },
                                      failureMessage : me
                                          .msg("message.completedocument.failure")
                                    });
                              }
                            }
                          }
                        },
                        {
                          text : this
                              .msg("button.complete-document.completeother"),
                          handler : function dlA_onActionCompleteDocument_completeother() {
                            this.destroy();
                            if (documentSecrecy == null) {
                              Alfresco.util.PopupManager
                                  .displayMessage({
                                    text : me
                                        .msg("message.completedocument.nosecrecyset")
                                  });
                            } else {
                              location.href = "start-workflow?selectedItems="
                                  + nodeRef + "&startWorkflow="
                                  + params.workflow + "";
                            }
                            return;
                          }
                        },
                        {
                          text : this.msg("button.cancel"),
                          handler : function dlA_onActionCompleteDocument_cancel() {
                            this.destroy();
                          },
                          isDefault : true
                        } ]
                  });

            }
          });
})();
