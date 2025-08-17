package com.locngo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.locngo.dto.CreateLieuImageDto;
import com.locngo.dto.LieuImageByIdDto;
import com.locngo.entity.Lieu;
import com.locngo.entity.LieuImage;
import com.locngo.exceptions.ImageNotFoundException;
import com.locngo.exceptions.LieuNotFoundException;
import com.locngo.repository.LieuImageRepository;
import com.locngo.repository.LieuRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class LieuImageServiceTest {

  @Mock private LieuImageRepository lieuImageRepository;

  @Mock private LieuRepository lieuRepository;

  @InjectMocks private LieuImageService lieuImageService;

  private Lieu sampleLieu;
  private LieuImage sampleImage;

  @BeforeEach
  void setup() {
    sampleLieu = new Lieu();
    sampleLieu.setId(1);

    sampleImage = new LieuImage();
    sampleImage.setId(10);
    sampleImage.setLieu(sampleLieu);
    sampleImage.setImageUrl("http://image.url");
  }

  @Test
  void testFindByLieuId_success() {
    when(lieuRepository.findById(1)).thenReturn(Optional.of(sampleLieu));
    when(lieuImageRepository.findByLieuId(1)).thenReturn(List.of(sampleImage));

    List<LieuImageByIdDto> result = lieuImageService.findByLieuId(1);

    assertEquals(1, result.size());
    assertEquals("http://image.url", result.getFirst().imageUrl());
    assertEquals(1, result.getFirst().lieuId());
  }

  @Test
  void testFindByLieuId_notFound() {
    when(lieuRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(LieuNotFoundException.class, () -> lieuImageService.findByLieuId(99));
  }

  @Test
  void testAddImageToLieu() {
    CreateLieuImageDto dto = new CreateLieuImageDto(0, "http://image.url", sampleLieu);

    lieuImageService.addImageToLieu(dto);

    ArgumentCaptor<LieuImage> captor = ArgumentCaptor.forClass(LieuImage.class);
    verify(lieuImageRepository).save(captor.capture());

    LieuImage saved = captor.getValue();
    assertEquals("http://image.url", saved.getImageUrl());
    assertEquals(sampleLieu, saved.getLieu());
  }

  @Test
  void testDeleteByLieuId() {
    lieuImageService.deleteByLieuId(1);

    verify(lieuImageRepository).deleteByLieuId(1);
  }

  @Test
  void testGetById_success() {
    when(lieuImageRepository.findById(10)).thenReturn(Optional.of(sampleImage));

    LieuImageByIdDto result = lieuImageService.getById(10);

    assertEquals(10, result.id());
    assertEquals("http://image.url", result.imageUrl());
    assertEquals(1, result.lieuId());
  }

  @Test
  void testGetById_notFound() {
    when(lieuImageRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(ImageNotFoundException.class, () -> lieuImageService.getById(99));
  }

  @Test
  void testSaveImageOfLieuToS3_success() throws Exception {
    MultipartFile mockFile = mock(MultipartFile.class);
    lenient().when(mockFile.getOriginalFilename()).thenReturn("image.jpg");
    lenient().when(mockFile.getBytes()).thenReturn("content".getBytes());

    // Mock du repository pour le lieu
    when(lieuRepository.findById(1)).thenReturn(Optional.of(sampleLieu));

    // On spy le service pour mocker connectS3
    LieuImageService spyService = Mockito.spy(lieuImageService);
    doReturn("http://s3.url/image.jpg").when(spyService).connectS3(mockFile);

    String url = spyService.saveImageOfLieuToS3(mockFile, 1);

    assertEquals("http://s3.url/image.jpg", url);
    verify(spyService).addImageToLieu(any(CreateLieuImageDto.class));
  }

  @Test
  void testSaveImageOfLieuToS3_lieuNotFound() {
    MultipartFile mockFile = mock(MultipartFile.class);
    when(lieuRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(
        LieuNotFoundException.class, () -> lieuImageService.saveImageOfLieuToS3(mockFile, 99));
  }
}
