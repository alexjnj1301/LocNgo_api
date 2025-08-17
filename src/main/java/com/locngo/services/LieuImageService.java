package com.locngo.services;

import com.locngo.dto.CreateLieuImageDto;
import com.locngo.dto.LieuImageByIdDto;
import com.locngo.entity.LieuImage;
import com.locngo.exceptions.ImageNotFoundException;
import com.locngo.exceptions.LieuNotFoundException;
import com.locngo.repository.LieuImageRepository;
import com.locngo.repository.LieuRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Service
public class LieuImageService {
  @Autowired private LieuImageRepository lieuImageRepository;
  @Autowired private LieuRepository lieuRepository;

  private final String bucketName = System.getenv("AWS_BUCKET_NAME");
  private final String regionName = System.getenv("AWS_REGION_NAME");
  private final String accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
  private final String secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");

  public List<LieuImageByIdDto> findByLieuId(int lieuId) {
    var lieu =
        this.lieuRepository
            .findById(lieuId)
            .orElseThrow(() -> new LieuNotFoundException("Lieu not found with id " + lieuId));
    List<LieuImage> lieuImages = this.lieuImageRepository.findByLieuId(lieu.getId());

    return lieuImages.stream()
        .map(
            image ->
                new LieuImageByIdDto(image.getId(), image.getImageUrl(), image.getLieu().getId()))
        .collect(Collectors.toList());
  }

  public void addImageToLieu(CreateLieuImageDto createLieuImageDto) {
    var lieuImage =
        new LieuImage(createLieuImageDto.id(), createLieuImageDto.url(), createLieuImageDto.lieu());
    this.lieuImageRepository.save(lieuImage);
  }

  public void deleteByLieuId(int lieuId) {
    this.lieuImageRepository.deleteByLieuId(lieuId);
  }

  public LieuImageByIdDto getById(int imageId) {
    var lieuImage =
        this.lieuImageRepository
            .findById(imageId)
            .orElseThrow(() -> new ImageNotFoundException("Image not found with id " + imageId));
    return new LieuImageByIdDto(
        lieuImage.getId(), lieuImage.getImageUrl(), lieuImage.getLieu().getId());
  }

  public String saveImageOfLieuToS3(MultipartFile file, int lieuId) throws Exception {
    var lieu =
        this.lieuRepository
            .findById(lieuId)
            .orElseThrow(() -> new LieuNotFoundException("Lieu not found with id " + lieuId));

    String urlOfFileSaved = connectS3(file);

    var createLieuImageDto = new CreateLieuImageDto(0, urlOfFileSaved, lieu);
    addImageToLieu(createLieuImageDto);

    return urlOfFileSaved;
  }

  public String connectS3(MultipartFile file) throws Exception {
    AwsCredentials credentials = AwsBasicCredentials.create(this.accessKeyId, this.secretAccessKey);

    var urlOfFileSaved = "";
    try {
      S3Client s3Client =
          S3Client.builder()
              .region(Region.of(this.regionName))
              .credentialsProvider(StaticCredentialsProvider.create(credentials))
              .build();

      Map<String, String> metadata = new HashMap<>();
      metadata.put("key", "url");

      var timestamp = System.currentTimeMillis();
      // Ensure the file name is unique by appending a timestamp
      var fileName = timestamp + file.getOriginalFilename();

      s3Client.putObject(
          b -> b.bucket(this.bucketName).key(fileName).metadata(metadata),
          software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

      // get the URL of the file saved
      urlOfFileSaved =
          s3Client.utilities().getUrl(b -> b.bucket(this.bucketName).key(fileName)).toString();

      s3Client.close();
    } catch (Exception e) {
      throw new Exception();
    }

    return urlOfFileSaved;
  }
}
