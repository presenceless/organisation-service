package cd.presenceless.organisationservice.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class FileServiceImpl implements FileService {

    @Value("${gcp.bucket.name}")
    private String bucketName;

    private final Storage storage;

    public FileServiceImpl(Storage storage) {
        this.storage = storage;
    }

    /**
     * @return a list of files by organisation
     */
    @Override
    public List<String> getFilesByOrg(Long orgId) {
        List<String> list = new ArrayList<>();
        storage.list(bucketName).iterateAll().forEach(blob -> {
            if (blob.getName().contains(orgId.toString() + "+" )) {
                list.add(blob.getName());
            }
        });
        return list;
    }

    /**
     * @param fileName the name of the file to download
     * @return the file as a ByteArrayResource
     */
    @Override
    public ByteArrayResource downloadFile(String fileName) {
        Blob blob = storage.get(bucketName, fileName);

        return new ByteArrayResource(
                blob.getContent());
    }

    /**
     * @param fileName the name of the file to delete
     * @return true if the file was deleted, false otherwise
     */
    @Override
    public boolean deleteFile(String fileName) {

        Blob blob = storage.get(bucketName, fileName);

        return blob.delete();
    }

    /**
     * @param orgId the organisation id
     * @param file the file to upload
     * @throws IOException if the file cannot be uploaded
     */
    @Override
    public Map<String, String> uploadFile(MultipartFile file, Long orgId) throws IOException {
        BlobId blobId = BlobId.of(bucketName,
                orgId.toString() + "+" + Objects.requireNonNull(file.getOriginalFilename()));

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).
                setContentType(file.getContentType()).build();
        Blob blob = storage.create(blobInfo, file.getBytes());

        return Map.of(
                "mediaLink", blob.getMediaLink(),
                "fileName", blob.getName());
    }
}
