package hei.school.soratra.endpoint.rest.controller.health;

import hei.school.soratra.PojaGenerated;
import hei.school.soratra.file.BucketComponent;
import hei.school.soratra.repository.model.ListURL;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@PojaGenerated
@RestController
@AllArgsConstructor
public class SoratraController {

  private final BucketComponent bucketComponent;

  private static final String SORATRA_KEY = "soratra/";

  @PutMapping(value = "/soratra/{id}")
  public ResponseEntity<?> convertSoratra(@PathVariable String id, @RequestBody byte[] file) {
    try {
      File fileToUpload = convertToTempFile(file);

      bucketComponent.upload(fileToUpload, SORATRA_KEY + id);
      String allUpperCase = convertToUpperCase(fileToUpload.getName());

      File fileTransformed = new File(id);
      try (FileOutputStream fos = new FileOutputStream(fileTransformed)) {
        fos.write(allUpperCase.getBytes());
      }

      bucketComponent.upload(fileTransformed, SORATRA_KEY + "UpperCase/" + id);

      return ResponseEntity.ok().body(null);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Erreur lors du traitement du fichier text: " + e.getMessage());
    }
  }

  @GetMapping(value = "/soratra/{id}")
  public ResponseEntity<ListURL> convertSoratra(@PathVariable String id) {
    try {
      String originalUrl =
          bucketComponent.presign(SORATRA_KEY + id + ".txt", Duration.ofMinutes(4)).toString();
      String transformedUrl =
          bucketComponent
              .presign(SORATRA_KEY + "UpperCase/" + id + ".txt", Duration.ofMinutes(4))
              .toString();
      ListURL listUrl = new ListURL(originalUrl, transformedUrl);
      return ResponseEntity.ok(listUrl);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  private File convertToTempFile(byte[] file) throws IOException {
    File tempFile = File.createTempFile("temp-fichier", ".txt");
    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
      fos.write(file);
    }
    return tempFile;
  }

  private static String convertToUpperCase(String file) throws IOException {
    File fichier = new File(file);

    StringBuilder contenu = new StringBuilder();

    try (Scanner scanner = new Scanner(fichier)) {
      while (scanner.hasNextLine()) {
        String ligne = scanner.nextLine();
        contenu.append(ligne.toUpperCase()).append("\n");
      }
    }

    return contenu.toString();
  }
}
