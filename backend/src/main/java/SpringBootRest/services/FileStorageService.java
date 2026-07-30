package SpringBootRest.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

    public String save(MultipartFile file) {

        try {

            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            String filename =
                    System.currentTimeMillis()
                            + "_"
                            + file.getOriginalFilename();


            Path destination = root.resolve(filename);

            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );


            return "/uploads/" + filename;


        } catch (IOException e) {

            throw new RuntimeException(
                    "Error saving file", e
            );
        }
    }
}
