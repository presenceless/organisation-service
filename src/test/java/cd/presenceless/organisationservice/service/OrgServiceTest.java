package cd.presenceless.organisationservice.service;

import cd.presenceless.organisationservice.entity.Document;
import cd.presenceless.organisationservice.entity.Organisation;
import cd.presenceless.organisationservice.repository.OrganisationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
class OrgServiceTest {
    @Autowired
    private OrganisationRepository repository;
    @Autowired
    private OrganisationService organisationService;

    // @Test
    // void contextLoads() {
    // }

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    public void testSaveAttachment() throws Exception {
        Organisation organisation = mock(Organisation.class);
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello, world!".getBytes());
        Document product = organisationService.saveDocument(organisation, mockFile);
        assertNotNull(product.getId());
        assertEquals("test.txt", product.getFileName());
        assertEquals("text/plain", product.getFileType());
    }

    @Test
    public void testSaveFiles() throws Exception {
        Organisation organisation = mock(Organisation.class);
        MockMultipartFile mockFile1 = new MockMultipartFile(
                "file", "test1.pdf", "text/plain", "Hello, world!".getBytes());
        MockMultipartFile mockFile2 = new MockMultipartFile(
                "file", "test2.txt", "text/plain", "Goodbye, world!".getBytes());
        organisationService.save(organisation, new MockMultipartFile[]{mockFile1, mockFile2});
        List<Organisation> products = organisationService.getAllFiles();

        System.out.println("Saved files:");
        for (Organisation product : products) {
            System.out.println(product.getDocuments());
        }
        assertEquals(2, products.size());
        assertEquals("test1.pdf", products.get(0).getDocuments().get(0).getFileName());
        assertEquals("test2.txt", products.get(1).getDocuments().get(1).getFileName());
    }

    @Test
    public void testSaveAttachmentInvalidName() {
        Organisation organisation = mock(Organisation.class);

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "../test.txt", "text/plain", "Hello, world!".getBytes());
        assertThrows(Exception.class, () -> organisationService.saveDocument(organisation, mockFile));
    }

    @Test
    public void testSaveAttachmentTooLarge() {
        Organisation organisation = mock(Organisation.class);
        byte[] bytes = new byte[1024 * 1024 * 10];

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", bytes);
        assertThrows(Exception.class, () -> organisationService.saveDocument(organisation, mockFile));
    }
}
