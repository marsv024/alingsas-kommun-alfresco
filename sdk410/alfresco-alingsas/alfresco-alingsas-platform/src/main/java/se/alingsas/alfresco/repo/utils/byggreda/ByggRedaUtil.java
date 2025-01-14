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

package se.alingsas.alfresco.repo.utils.byggreda;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import se.alingsas.alfresco.repo.model.AkDmModel;

import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ByggRedaUtil implements Runnable {
    private static final Log LOG = LogFactory.getLog(ByggRedaUtil.class);
    public static final String SOURCE_TYPE_REPO = "repository";
    public static final String SOURCE_TYPE_FS = "filesystem";
    public static final String LINE_BREAK = "\r\n";
    private static final String EXTENSION_CHARACTER = ".";
    private static final String MSG_WORKING_COPY_LABEL = "(Working Copy)";

    private static String sourceType;
    private static String siteName;
    private static String destinationPath;
    private static String logPath;
    private static String outputPath;

    private static SiteService siteService;
    private static NodeService nodeService;
    private static VersionService versionService;
    private static FileFolderService fileFolderService;
    private static ContentService contentService;
    private static CheckOutCheckInService checkOutCheckInService;
    private final String DOC_STATUS_COMPLETE = "Färdigt dokument";
    private final String DOC_SECRECY_PUBLIC = "Offentligt";

    private Set<ByggRedaDocument> documents = null;
    private String logMessage;
    private static TransactionService transactionService;

    private static Properties globalProperties;

    private List<String> globalMessages = new ArrayList<String>();
    private boolean updateExisting = false;
    private String sourcePath;
    private String metaFileName;
    private SiteInfo site;
    private InputStream metadataFile;
    private boolean validatedParams = false;
    private String runAs = null;

    /**
     * Main method which runs an import of material.
     */
    public void run() {
        final RetryingTransactionHelper retryingTransactionHelper = transactionService.getRetryingTransactionHelper();
        if (runAs == null) {
            runAs = AuthenticationUtil.getSystemUserName();
        }
        AuthenticationUtil.runAs((AuthenticationUtil.RunAsWork<Void>) () -> {
            retryingTransactionHelper
                    .doInTransaction(() -> {
                        LOG.info("Running Byggreda import as user " + runAs);
                        LOG.info("sourcePath: " + getSourcePath() + ", metaFileName: " + getMetaFileName());
                        if (validatedParams) {
                            try {
                                documents = ReadMetadataDocument.read(metadataFile, globalMessages);
                            } finally {
                                IOUtils.closeQuietly(metadataFile);
                            }
                            documents = importDocuments(site, getSourcePath(), documents);
                            logDocuments(site, globalMessages);
                            createOutputDocument(site);
                        }
                        return null;
                    }, false, true);
            return null;
        }, runAs);
    }

    public boolean validateParams() {
        validatedParams = false;
        site = findSite(siteName);
        if (site == null) {
            LOG.error("Could not find site " + siteName);
            return false;
        }

        if (!StringUtils.hasText(getSourcePath())) {
            LOG.error("Source path does not exist, was " + getSourcePath());
            return false;
        }

        if (SOURCE_TYPE_REPO.equals(sourceType)) {
            if (getSourcePath().startsWith("/")) {
                setSourcePath(getSourcePath().substring(1));
                LOG.debug("Stripping prefix / from sourcePath. Path is: "
                        + getSourcePath());
            }
            if (getSourcePath().endsWith("/")) {
                setSourcePath(getSourcePath().substring(0,
                        getSourcePath().length() - 1));
                LOG.debug("Stripping postfix / from sourcePath. Path is: "
                        + getSourcePath());
            }
        }

        if (!StringUtils.hasText(getSourcePath())
                || !sourceFolderExists(site, getSourcePath())) {
            LOG.error("Source path does not exist, was " + getSourcePath());
            return false;
        }

        if (!StringUtils.hasText(destinationPath)) {
            LOG.error("Destination path does not exist, was " + destinationPath);
            return false;
        }

        if (destinationPath.startsWith("/")) {
            destinationPath = destinationPath.substring(1);
            LOG.debug("Stripping prefix / from destinationPath. Path is: "
                    + destinationPath);
        }
        if (destinationPath.endsWith("/")) {
            destinationPath = destinationPath.substring(0,
                    destinationPath.length() - 1);
            LOG.debug("Stripping postfix / from destinationPath. Path is: "
                    + destinationPath);
        }

        if (!StringUtils.hasText(destinationPath)
                || !repoFolderExists(site, destinationPath)) {
            LOG.error("Destination path does not exist, was " + destinationPath);
            return false;
        }

        if (!StringUtils.hasText(logPath)) {
            LOG.error("Log path does not exist, was " + logPath);
            return false;
        }

        if (logPath.startsWith("/")) {
            logPath = logPath.substring(1);
            LOG.debug("Stripping prefix / from logPath. Path is: " + logPath);
        }
        if (logPath.endsWith("/")) {
            logPath = logPath.substring(0, logPath.length() - 1);
            LOG.debug("Stripping postfix / from logPath. Path is: " + logPath);
        }

        if (!StringUtils.hasText(logPath) || !repoFolderExists(site, logPath)) {
            LOG.error("Log path does not exist, was " + logPath);
            return false;
        }

        if (!StringUtils.hasText(getMetaFileName())) {
            LOG.error("No filename supplied");
            return false;
        }

        metadataFile = readFile(site, getSourcePath(), getMetaFileName());

        if (metadataFile == null) {
            LOG.error("Could not find metadata file at " + getSourcePath()
                    + "/" + getMetaFileName());
            return false;
        } else {
            validatedParams = true;
            return true;
        }
    }


    /**
     * Used for ordering ByggReda results by their linenumbers
     *
     * @author mars
     */
    private class ByggRedaOrderComparator implements Comparator<ByggRedaDocument> {

        @Override
        public int compare(ByggRedaDocument o1, ByggRedaDocument o2) {
            if (o1.getLineNumber() < o2.getLineNumber()) {
                return -1;
            } else if (o1.getLineNumber() > o2.getLineNumber()) {
                return 1;
            } else {
                return 0;
            }
        }

    }

    /**
     * Store a log in Alfresco of the result of the import
     *
     * @param site
     */

    private void logDocuments(SiteInfo site, List<String> globalMessages) {
        NodeRef folderNodeRef = createFolder(logPath, null, null, site);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = formatter.format(new Date());
        formatter = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        String fileName = formatter.format(new Date()) + " Import.log";

        StringBuilder common = new StringBuilder();
        StringBuilder logged = new StringBuilder();

        Iterator<String> globalIt = globalMessages.iterator();
        while (globalIt.hasNext()) {
            String next = globalIt.next();
            logged.append(next + LINE_BREAK);
        }
        int failedCount = 0;
        SortedSet<ByggRedaDocument> sortedDocuments = new TreeSet<ByggRedaDocument>(new ByggRedaOrderComparator());
        sortedDocuments.addAll(documents);
        Iterator<ByggRedaDocument> it = sortedDocuments.iterator();
        while (it.hasNext()) {
            ByggRedaDocument next = it.next();
            if (!next.isReadSuccessfully()) {
                failedCount++;
            }
            if (!next.isReadSuccessfully() || StringUtils.hasText(next.getStatusMsg())) {
                logged.append("#" + next.getLineNumber() + " - "
                        + next.getRecordDisplay() + " - " + next.getBuildingDescription()
                        + ": " + next.getStatusMsg() + LINE_BREAK);
            }
        }
        common.append("Sammaställning av importkörning " + LINE_BREAK);
        common.append("--------------------------" + LINE_BREAK);
        common.append("Datum/tidpunkt: " + currentDate + LINE_BREAK);
        common.append("Antal inlästa dokument från styrfil: "
                + documents.size() + LINE_BREAK);
        common.append("Lyckade inläsningar: "
                + (documents.size() - failedCount) + LINE_BREAK);
        common.append("Misslyckade inläsningar: " + failedCount + LINE_BREAK);
        common.append("--------------------------" + LINE_BREAK + LINE_BREAK);
        if (logged.length() > 0) {
            common.append("Loggmeddelanden:" + LINE_BREAK);
            common.append("--------------------------" + LINE_BREAK);
            common.append(logged);
            common.append("--------------------------" + LINE_BREAK);
        }
        FileInfo fileInfo = fileFolderService.create(folderNodeRef, fileName,
                AkDmModel.TYPE_AKDM_DOCUMENT);

        logMessage = common.toString();
        try {
            InputStream is = new ByteArrayInputStream(common.toString()
                    .getBytes("UTF-8"));
            try {
                final ContentWriter writer = contentService.getWriter(
                        fileInfo.getNodeRef(), ContentModel.PROP_CONTENT, true);

                writer.setMimetype("text/plain");

                writer.putContent(is);
            } finally {
                IOUtils.closeQuietly(is);
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            LOG.error("Error while creating log file, Unsupported Encoding", e);
        }
    }

    /**
     * Store a log in Alfresco of the result of the import
     *
     * @param site
     */
    private void createOutputDocument(SiteInfo site) {
        NodeRef folderNodeRef = createFolder(outputPath, null, null, site);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        String fileName = formatter.format(new Date()) + " Output.log";

        StringBuilder common = new StringBuilder();
        Iterator<ByggRedaDocument> it = documents.iterator();
        while (it.hasNext()) {
            ByggRedaDocument next = it.next();
            if (next.isReadSuccessfully()) {
                common.append(next.getRecordYear() + ";");
                common.append(next.getRecordNumber() + ";");
                common.append(next.getBuildingDescription() + ";");
                // TODO perhaps modify to point to SSO URL
                String shareURL = "";
                String shareProtocol = (String) globalProperties
                        .get("share.protocol");
                String shareHostname = (String) globalProperties
                        .get("share.host");
                String sharePort = (String) globalProperties.get("share.port");
                String shareContext = (String) globalProperties
                        .get("share.context");

                shareURL = shareProtocol + "://" + shareHostname;
                // If we are not using standard ports, then also append port
                // numbers
                if (("http".equals(shareProtocol) && !"80".equals(sharePort))
                        || ("https".equals(shareProtocol) && !"443"
                        .equals(sharePort))) {
                    shareURL = shareURL + ":" + sharePort;
                }
                shareURL = shareURL + "/" + shareContext;

                String url = shareURL + "/"
                        + "proxy/alfresco/api/node/content/"
                        + next.getNodeRef().getStoreRef().getProtocol() + "/"
                        + next.getNodeRef().getStoreRef().getIdentifier() + "/"
                        + next.getNodeRef().getId() + "/" + next.getFileName()
                        + "?a=true";

                // http://localhost:8081/share/proxy/alfresco/api/node/content/workspace/SpacesStore/abb7a6c6-1329-473e-b1b0-621e7aff4d2c/2012-12-03%20095912%20Output.log?a=true
                common.append(url + LINE_BREAK);// TODO add correct URL
            }
        }

        FileInfo fileInfo = fileFolderService.create(folderNodeRef, fileName,
                AkDmModel.TYPE_AKDM_DOCUMENT);

        try {
            InputStream is = new ByteArrayInputStream(common.toString()
                    .getBytes("UTF-8"));
            try {
                final ContentWriter writer = contentService.getWriter(
                        fileInfo.getNodeRef(), ContentModel.PROP_CONTENT, true);

                writer.setMimetype("text/plain");

                writer.putContent(is);
            } finally {
                IOUtils.closeQuietly(is);
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            LOG.error("Error while creating log file, Unsupported Encoding", e);
        }
    }

    /**
     * Look up a site by its id (shortname)
     *
     * @param siteId
     * @return
     */
    private SiteInfo findSite(final String siteId) {
        if (!StringUtils.hasText(siteId)) {
            return null;
        }

        return siteService.getSite(siteId);
    }

    /**
     * Validate that the sourceFolder exists
     *
     * @param site
     * @param path
     * @return
     */
    private boolean sourceFolderExists(SiteInfo site, String path) {
        if (sourceType.equals(SOURCE_TYPE_FS)) {
            // Check filesystem for source folder
            File f = new File(path);
            return f.exists();
        } else {
            return repoFolderExists(site, path);
        }
    }

    /**
     * Get a file or folder from the Repository
     *
     * @param site
     * @param path
     * @return
     */
    private FileInfo getRepoFileFolder(SiteInfo site, String path) {
        // Check Alfresco for folder
        final String[] parts = StringUtils
                .delimitedListToStringArray(path, "/");
        List<String> list = new ArrayList<String>();
        for (String part : parts) {
            list.add(part);
        }

        NodeRef rootNodeRef = fileFolderService.searchSimple(site.getNodeRef(),
                SiteService.DOCUMENT_LIBRARY);
        try {
            return fileFolderService.resolveNamePath(rootNodeRef, list, false);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * Return whether or not a certain file or folder exists.
     *
     * @param site
     * @param path
     * @return
     */
    private boolean repoFolderExists(SiteInfo site, String path) {
        return getRepoFileFolder(site, path) != null;
    }

    /**
     * Checks for file existance trying different cases on extension.
     *
     * @param path
     * @return the correct path or null if file does not exist
     */
    private String checkFileExistsIgnoreExtensionCase(String path) {
        File f = new File(path);
        if (!f.exists()) {
            String ext = StringUtils.getFilenameExtension(path);
            String fileWithNoExt = path.substring(0, path.indexOf(ext));
            LOG.debug("Extracted filename without extension: " + fileWithNoExt + " Extension: " + ext);
            //Try all lowercase
            path = fileWithNoExt + ext.toLowerCase();
            LOG.debug("Trying " + path);
            f = new File(path);
            if (!f.exists()) {
                //Try with all uppercase
                path = fileWithNoExt + ext.toUpperCase();
                LOG.debug("Trying " + path);
                f = new File(path);
                if (!f.exists()) {
                    //Try with capitalized extension
                    String tmp = ext.substring(0, 1).toUpperCase() + ext.substring(1).toLowerCase();
                    path = fileWithNoExt + tmp;
                    LOG.debug("Trying " + path);
                    f = new File(path);
                    if (!f.exists()) {
                        path = null;
                    }
                }
            }

            if (path != null) {
                LOG.debug("Found actual filename to be: " + path);
            }
        }
        return path;
    }

    /**
     * Read a file either from filesystem or from repository, returns an
     * InputStream. It is important to note that it is up to the caller of this
     * method to properly close the input stream when done.
     *
     * @param site
     * @param path
     * @param fileName
     * @return
     */
    private InputStream readFile(SiteInfo site, String path, String fileName) {
        if (sourceType.equals(SOURCE_TYPE_FS)) {
            try {
                String fullFilenamePath = checkFileExistsIgnoreExtensionCase(path + "/" + fileName);
                if (fullFilenamePath == null) {
                    return null;
                } else {
                    return new FileInputStream(fullFilenamePath);
                }
            } catch (java.io.FileNotFoundException e) {
                return null;
            }
        } else {
            FileInfo repoFileFolder = getRepoFileFolder(site, path + "/"
                    + fileName);
            if (repoFileFolder == null) {
                return null;
            } else {
                ContentReader rawReader = contentService
                        .getRawReader(repoFileFolder.getContentData()
                                .getContentUrl());
                if (rawReader.exists()) {
                    return rawReader.getContentInputStream();
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Import documents which were successfully read into Alfresco, return a
     * list with the result This class expects all input data to already have
     * been validated. Therefore it will not be checked again
     *
     */
    public Set<ByggRedaDocument> importDocuments(SiteInfo site,
                                                 String sourcePath, final Set<ByggRedaDocument> documentSet) {
        LOG.info("Starting import of documents from ByggReda");
        Set<ByggRedaDocument> result = new HashSet<ByggRedaDocument>();
        Iterator<ByggRedaDocument> it = documentSet.iterator();
        while (it.hasNext()) {
            ByggRedaDocument next = it.next();
            if (next.isReadSuccessfully()) {
                ByggRedaDocument importedDocument = importDocument(site,
                        sourcePath, next);
                result.add(importedDocument);
            } else {
                result.add(next);
            }
        }
        LOG.info("Finished import of documents from ByggReda");
        return result;
    }

    /**
     * Import a document
     *
     */
    private ByggRedaDocument importDocument(SiteInfo site, String sourcePath,
                                            ByggRedaDocument document) {
        final String currentDestinationPath = destinationPath + "/"
                + document.getPath();
        UserTransaction trx = getTransactionService()
                .getNonPropagatingUserTransaction();

        try {
            trx.begin();
            // Check if file exists already
            FileInfo repoFileFolder = getRepoFileFolder(site,
                    currentDestinationPath + "/" + document.getFileName());
            if (repoFileFolder != null) {
                if (this.updateExisting) {
                    try {
                        String fullFilenamePath = checkFileExistsIgnoreExtensionCase(sourcePath + "/" + document.getFileName());
                        if (fullFilenamePath == null) {
                            throw new java.io.FileNotFoundException();
                        }
                        File f = new File(fullFilenamePath);
                        if (!f.exists()) {
                            throw new java.io.FileNotFoundException();
                        }
                        if (!f.exists()) {
                            throw new java.io.FileNotFoundException();
                        }
                        LOG.debug("File "
                                + document.getFileName()
                                + " already exists, attempting to creating a new version at "
                                + currentDestinationPath);
                        final NodeRef workingCopy = checkOutCheckInService
                                .checkout(repoFileFolder.getNodeRef());

                        addProperties(workingCopy, document, true);

                        createFile(workingCopy, site, sourcePath, document);

                        final Map<String, Serializable> properties = new HashMap<String, Serializable>();
                        properties.put(VersionModel.PROP_VERSION_TYPE,
                                VersionType.MAJOR);
                        NodeRef checkin = checkOutCheckInService.checkin(
                                workingCopy, properties);
                        document.setNodeRef(checkin);
                        if (checkin != null && nodeService.exists(checkin)) {
                            document.setReadSuccessfully(true);
                            document.setStatusMsg("Filen " + sourcePath + "/" + document.getFileName() + " uppdaterades till ny version");
                            trx.commit();
                        } else {
                            document.setReadSuccessfully(false);
                            document.setStatusMsg("Uppdatering av fil "
                                    + currentDestinationPath + "/" + document.getFileName() + " misslyckades.");
                            LOG.error(document.getStatusMsg());
                            throw new Exception(document.getStatusMsg());
                        }
                    } catch (java.io.FileNotFoundException e) {
                        document.setReadSuccessfully(false);
                        document.setStatusMsg("Inläsning av fil misslyckades, filen "
                                + sourcePath
                                + "/" + document.getFileName() + " kunde inte hittas.");
                        LOG.error(document.getStatusMsg());
                        throw new Exception(document.getStatusMsg(), e);
                    } catch (FileExistsException e) {
                        document.setReadSuccessfully(false);
                        document.setStatusMsg("Inläsning av fil misslyckades, målfilen "
                                + currentDestinationPath + " finns redan.");
                        LOG.error(document.getStatusMsg());
                        throw new Exception(document.getStatusMsg(), e);
                    } catch (Exception e) {
                        document.setReadSuccessfully(false);
                        document.setStatusMsg(e.getMessage());
                        LOG.error("Error importing document "
                                + document.getRecordNumber(), e);
                        throw new Exception(document.getStatusMsg(), e);
                    }
                } else {
                    document.setReadSuccessfully(false);
                    document.setStatusMsg("Filen existerar redan, hoppar över.");
                    LOG.debug("File already exists");
                    trx.commit();
                }
            } else {
                try {
                    String fullFilenamePath = checkFileExistsIgnoreExtensionCase(sourcePath + "/" + document.getFileName());
                    if (fullFilenamePath == null) {
                        throw new java.io.FileNotFoundException();
                    }
                    File f = new File(fullFilenamePath);
                    if (!f.exists()) {
                        throw new java.io.FileNotFoundException();
                    }
                    NodeRef folderNodeRef = createFolder(destinationPath, document.getPath(), document.getOriginalPath(), site);
                    final FileInfo fileInfo = fileFolderService.create(folderNodeRef, document.getFileName(),
                            AkDmModel.TYPE_AKDM_BYGGREDA_DOC);
                    document.setNodeRef(fileInfo.getNodeRef());
                    addProperties(document.getNodeRef(), document, false);
                    createFile(document.getNodeRef(), site, sourcePath, document);
                    createVersionHistory(document.getNodeRef());
                    document.setReadSuccessfully(true);
                    LOG.debug("Imported document " + document.getRecordDisplay());
                    trx.commit();
                } catch (java.io.FileNotFoundException e) {
                    document.setReadSuccessfully(false);
                    document.setStatusMsg("Inläsning av fil misslyckades, filen "
                            + sourcePath
                            + "/" + document.getFileName() + " kunde inte hittas.");
                    LOG.error(document.getStatusMsg());
                    throw new Exception(document.getStatusMsg(), e);
                } catch (FileExistsException e) {
                    document.setReadSuccessfully(false);
                    document.setStatusMsg("Inläsning av fil misslyckades, målfilen "
                            + currentDestinationPath + " finns redan.");
                    LOG.error(document.getStatusMsg());
                    throw new Exception(document.getStatusMsg(), e);
                } catch (Exception e) {
                    document.setReadSuccessfully(false);
                    document.setStatusMsg("Fel vid inläsning av fil, systemmeddelande: "
                            + e.getMessage());
                    LOG.error("Error importing document "
                            + document.getRecordNumber(), e);
                    throw new Exception(document.getStatusMsg(), e);
                }
            }
        } catch (Exception e) {
            try {
                if (trx.getStatus() == Status.STATUS_ACTIVE) {
                    trx.rollback();
                } else {
                    LOG.error("The transaction was not active", e);
                }
                return document;
            } catch (Exception e2) {
                LOG.error("Exception: ", e);
                LOG.error("Exception while rolling back transaction", e2);
                throw new RuntimeException(e2);
            }

        }
        return document;

    }

    /**
     * Creates version history if it does not already exist
     *
     * @param nodeRef
     */
    private void createVersionHistory(final NodeRef nodeRef) {
        final VersionHistory versionHistory = getVersionService()
                .getVersionHistory(nodeRef);

        if (versionHistory == null) {
            // check it in again, with supplied version history note
            final Map<String, Serializable> properties = new HashMap<String, Serializable>();
            properties.put(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR);
            versionService.createVersion(nodeRef, properties);
        }
    }

    /**
     * Create a ByggReda file in the repository
     *
     * @param nodeRef
     * @param site
     * @param sourcePath
     * @param document
     * @throws java.io.FileNotFoundException
     */
    private void createFile(NodeRef nodeRef, SiteInfo site, String sourcePath,
                            ByggRedaDocument document) throws java.io.FileNotFoundException {
        InputStream inputStream = readFile(site, sourcePath, document.getFileName());
        if (inputStream != null) {
            try {
                final ContentWriter writer = contentService.getWriter(nodeRef,
                        ContentModel.PROP_CONTENT, true);

                writer.setMimetype(document.getMimetype());

                writer.putContent(inputStream);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        } else {
            throw new java.io.FileNotFoundException(
                    "Input file could not be read. " + sourcePath + "/"
                            + document.getFileName());
        }
    }

    /**
     * Create new or return existing folder
     *
     * @param filepath
     * @param site
     * @return
     */
    private NodeRef createFolder(final String basePath,
                                 final String folderPath, final String folderTitles,
                                 final SiteInfo site) {
        NodeRef rootNodeRef = fileFolderService.searchSimple(site.getNodeRef(),
                SiteService.DOCUMENT_LIBRARY);

        String[] parts = StringUtils.delimitedListToStringArray(basePath, "/");

        for (String part : parts) {
            part = StringUtils.trimWhitespace(part);

            NodeRef folder = fileFolderService.searchSimple(rootNodeRef, part);

            while (folder == null) {
                folder = fileFolderService.create(rootNodeRef, part,
                        ContentModel.TYPE_FOLDER).getNodeRef();
                nodeService.setProperty(folder, ContentModel.PROP_TITLE, part);
            }

            rootNodeRef = folder;
        }
        if (folderPath != null) {
            parts = StringUtils.delimitedListToStringArray(folderPath, "/");
            String[] titles;
            if (folderTitles != null) {
                titles = StringUtils.delimitedListToStringArray(folderTitles,
                        "#/#");
            } else {
                titles = new String[0];
            }
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                String title;
                if (titles.length >= i + 1) {
                    title = titles[i];
                } else {
                    title = part;
                }
                part = StringUtils.trimWhitespace(part);

                NodeRef folder = fileFolderService.searchSimple(rootNodeRef,
                        part);

                while (folder == null) {
                    folder = fileFolderService.create(rootNodeRef, part,
                            ContentModel.TYPE_FOLDER).getNodeRef();
                    nodeService.setProperty(folder, ContentModel.PROP_TITLE,
                            title);
                }

                rootNodeRef = folder;
            }
        }
        return rootNodeRef;
    }

    /**
     * Add properties to a node in the repository for a ByggReda document
     *
     * @param nodeRef
     * @param document
     */
    private void addProperties(final NodeRef nodeRef,
                               final ByggRedaDocument document, final Boolean workingCopy) {
        final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();

        if (workingCopy) {
            final String name = nodeService.getProperty(nodeRef,
                    ContentModel.PROP_NAME).toString();

            final String workingCopyName = createWorkingCopyName(document.getFileName());

            if (!name.equalsIgnoreCase(workingCopyName)) {
                /*
                 * Keep generated working copy name or else we will get an error
                 * addProperty(properties, ContentModel.PROP_NAME,
                 * document.fileName);
                 */
            }
        } else {
            addProperty(properties, ContentModel.PROP_NAME, document.getFileName());
        }

        // final String checksum = _serviceUtils.getChecksum(document.file);
        // Alfresco general properties
        addProperty(properties, ContentModel.PROP_AUTO_VERSION_PROPS, true);
        addProperty(properties, ContentModel.PROP_AUTO_VERSION, true);
        addProperty(properties, ContentModel.PROP_TITLE, document.getTitle());
        addProperty(properties, ContentModel.PROP_CREATOR,
                AuthenticationUtil.SYSTEM_USER_NAME);
        addProperty(properties, ContentModel.PROP_MODIFIER,
                AuthenticationUtil.SYSTEM_USER_NAME);
        // addProperty(properties, ContentModel.PROP_MODIFIED,
        // document.createdDate);
        // addProperty(properties, ContentModel.PROP_CREATED,
        // document.createdDate);
        // addProperty(properties, ContentModel.PROP_DESCRIPTION,
        // document.description);

        // Common

        addProperty(properties, AkDmModel.PROP_AKDM_DOC_STATUS,
                DOC_STATUS_COMPLETE);
        addProperty(properties, AkDmModel.PROP_AKDM_DOC_SECRECY,
                DOC_SECRECY_PUBLIC);
        // ByggReda
        addProperty(properties, AkDmModel.PROP_AKDM_BYGGREDA_FILM, document.getFilm());
        addProperty(properties, AkDmModel.PROP_AKDM_BYGGREDA_SERIAL_NUMBER, document.getSerialNumber());
        addProperty(properties, AkDmModel.PROP_AKDM_BYGGREDA_RECORD_NUMBER, document.getRecordNumber());
        addProperty(properties, AkDmModel.PROP_AKDM_BYGGREDA_RECORD_YEAR, document.getRecordYear());
        addProperty(properties, AkDmModel.PROP_AKDM_BYGGREDA_RECORD_DISPLAY, document.getRecordDisplay());
        addProperty(properties, AkDmModel.PROP_AKDM_BYGGREDA_BUILDING_DESCR, document.getBuildingDescription());
        addProperty(properties,
                AkDmModel.PROP_AKDM_BYGGREDA_LAST_BUILDING_DESCR, document.getLastBuildingDescription());
        addProperty(properties, AkDmModel.PROP_AKDM_BYGGREDA_ADDRESS, document.getAddress());
        addProperty(properties, AkDmModel.PROP_AKDM_BYGGREDA_LAST_ADDRESS, document.getLastAddress());
        addProperty(properties, AkDmModel.PROP_AKDM_BYGGREDA_DECISION, document.getDecision());
        addProperty(properties, AkDmModel.PROP_AKDM_BYGGREDA_FOR, document.getForA());
        addProperty(properties, AkDmModel.PROP_AKDM_BYGGREDA_ISSUE_PURPOSE, document.getIssuePurpose());
        addProperty(properties, AkDmModel.PROP_AKDM_BYGGREDA_NOTE, document.getNote());
        addProperty(properties, AkDmModel.PROP_AKDM_BYGGREDA_RECORDS, document.getRecords());

        nodeService.addProperties(nodeRef, properties);
    }

    /**
     * Add a single property to a property map
     *
     * @param properties
     * @param key
     * @param value
     */
    private void addProperty(final Map<QName, Serializable> properties,
                             final QName key, Serializable value) {
        // if no text, just exit
        if (value == null) {
            return;
        }

        // if no text, just exit
        if (!StringUtils.hasText(value.toString())) {
            return;
        }

        // trim all trailing spaces for strings
        if (value instanceof String) {
            value = StringUtils.trimTrailingWhitespace(value.toString());
        }

        properties.put(key, value);
    }

    private String createWorkingCopyName(String name) {
        if (this.getWorkingCopyLabel() != null
                && this.getWorkingCopyLabel().length() != 0) {
            if (name != null && name.length() != 0) {
                final int index = name.lastIndexOf(EXTENSION_CHARACTER);
                if (index > 0) {
                    // Insert the working copy label before the file extension
                    name = name.substring(0, index) + " "
                            + getWorkingCopyLabel() + name.substring(index);
                } else {
                    // Simply append the working copy label onto the end of the
                    // existing
                    // name
                    name = name + " " + getWorkingCopyLabel();
                }
            } else {
                name = getWorkingCopyLabel();
            }
        }

        return name;
    }

    public String getWorkingCopyLabel() {
        return MSG_WORKING_COPY_LABEL;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        ByggRedaUtil.sourceType = sourceType;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        ByggRedaUtil.siteName = siteName;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        ByggRedaUtil.destinationPath = destinationPath;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        ByggRedaUtil.logPath = logPath;
    }

    public SiteService getSiteService() {
        return siteService;
    }

    public void setSiteService(SiteService siteService) {
        ByggRedaUtil.siteService = siteService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        ByggRedaUtil.nodeService = nodeService;
    }

    public FileFolderService getFileFolderService() {
        return fileFolderService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        ByggRedaUtil.fileFolderService = fileFolderService;
    }

    public ContentService getContentService() {
        return contentService;
    }

    public void setContentService(ContentService contentService) {
        ByggRedaUtil.contentService = contentService;
    }

    public VersionService getVersionService() {
        return versionService;
    }

    public void setVersionService(VersionService versionService) {
        ByggRedaUtil.versionService = versionService;
    }

    public Set<ByggRedaDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<ByggRedaDocument> documents) {
        this.documents = documents;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        ByggRedaUtil.outputPath = outputPath;
    }

    public Properties getGlobalProperties() {
        return globalProperties;
    }

    public void setGlobalProperties(Properties globalProperties) {
        ByggRedaUtil.globalProperties = globalProperties;
    }

    public CheckOutCheckInService getCheckOutCheckInService() {
        return checkOutCheckInService;
    }

    public void setCheckOutCheckInService(
            CheckOutCheckInService checkOutCheckInService) {
        ByggRedaUtil.checkOutCheckInService = checkOutCheckInService;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void setUpdateExisting(boolean updateExisting) {
        this.updateExisting = updateExisting;
        // TODO Auto-generated method stub

    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getMetaFileName() {
        return metaFileName;
    }

    public void setMetaFileName(String metaFileName) {
        this.metaFileName = metaFileName;
    }

    public String getRunAs() {
        return runAs;
    }

    public void setRunAs(String runAs) {
        this.runAs = runAs;
    }
}
