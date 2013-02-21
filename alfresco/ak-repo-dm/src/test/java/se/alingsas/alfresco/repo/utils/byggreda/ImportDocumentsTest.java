package se.alingsas.alfresco.repo.utils.byggreda;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.site.SiteInfo;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class ImportDocumentsTest {
	final Mockery context = new Mockery();
	final SiteInfo siteInfo = context.mock(SiteInfo.class);
	final StoreRef storeRef = new StoreRef("workspace://SpacesStore");
	final String dummyNodeId = "cafebabe-cafe-babe-cafe-babecafebabe";
	final NodeRef dummyNodeRef = new NodeRef(storeRef, dummyNodeId);
	final FileFolderService fileFolderService = context
			.mock(FileFolderService.class);
	
	final Set<ByggRedaDocument> documents = new HashSet<ByggRedaDocument>();
	static final int NUM_SUCCESSFUL_DOCS = 10;
	static final int NUM_FAILED_DOCS = 2;
	static final String destinationPath = "Dest";

	@Before
	public void setUp() throws Exception {

		for (int i = 0; i < NUM_SUCCESSFUL_DOCS; i++) {
			ByggRedaDocument document = new ByggRedaDocument();
			document.readSuccessfully = true;
			document.film = Integer.toString(i);
			document.serialNumber = Integer.toString(i);
			document.recordNumber = Integer.toString(i) + "."
					+ Integer.toString(i);
			document.buildingDescription = "A";
			document.lastBuildingDescription = "A";
			document.address = "A";
			document.lastAddress = "A";
			document.decision = "A";
			document.forA = "A";
			document.issuePurpose = "A";
			document.note = "A";
			document.records = "A";
			document.fileName = "test.pdf";
			documents.add(document);
		}

		for (int i = 0; i < NUM_FAILED_DOCS; i++) {
			ByggRedaDocument document = new ByggRedaDocument();
			document.readSuccessfully = false;
			document.statusMsg = "Error";
			document.film = Integer.toString(i);
			document.serialNumber = Integer.toString(i);
			document.recordNumber = Integer.toString(i) + "."
					+ Integer.toString(i);
			document.buildingDescription = "A";
			document.lastBuildingDescription = "A";
			document.address = "A";
			document.lastAddress = "A";
			document.decision = "A";
			document.forA = "A";
			document.issuePurpose = "A";
			document.note = "A";
			document.records = "A";
			document.fileName = "test.pdf";
			documents.add(document);
		}
		
	}

	/*@Test
	public void testImportDocuments() {
		assertEquals(NUM_SUCCESSFUL_DOCS + NUM_FAILED_DOCS, documents.size());
		ImportDocuments id = new ImportDocuments();
		id.setFileFolderService(fileFolderService);
		
		Set<ByggRedaDocument> importDocuments = id.importDocuments(siteInfo, destinationPath, documents);
		assertNotNull(importDocuments);
		assertEquals(documents.size(), importDocuments.size());
		Iterator<ByggRedaDocument> it = documents.iterator();
		while (it.hasNext()) {
			ByggRedaDocument next = it.next();
			if (next.readSuccessfully) {
				assertEquals(dummyNodeRef, next.nodeRef);
			} else {
				assertNull(next.nodeRef);
			}
		}		
	}*/

}
