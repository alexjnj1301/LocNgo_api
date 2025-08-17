package com.locngo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locngo.dto.*;
import com.locngo.entity.Lieu;
import com.locngo.entity.LieuImage;
import com.locngo.entity.User;
import com.locngo.exceptions.LieuNotFoundException;
import com.locngo.repository.LieuRepository;
import com.locngo.repository.ServicesRepository;
import com.locngo.utils.JwtUtils;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LieuServiceTest {

  @InjectMocks private LieuService lieuService;

  @Mock private LieuRepository lieuRepository;
  @Mock private LieuServicesService lieuServicesService;
  @Mock private ServicesRepository servicesRepository;
  @Mock private ReservationService reservationService;
  @Mock private LieuImageService lieuImageService;
  @Mock private JwtUtils jwtUtils;
  @Mock private UserService userService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private Lieu lieu;
  private User user;

  @BeforeEach
  void setUp() {
    lieuService = new LieuService(objectMapper);
    MockitoAnnotations.openMocks(this);

    user = new User();
    user.setId(1);
    user.setEmail("test@example.com");

    lieu = new Lieu();
    lieu.setId(1);
    lieu.setName("Lieu Test");
    lieu.setFavorite_picture("http://image.jpg");
    lieu.setProprietor((double) user.getId());
  }

  @Test
  void testFindById_success() {
    // Préparer le mock
    Lieu lieu = new Lieu();
    lieu.setId(1);
    lieu.setName("Test Lieu");
    lieu.setReservations(new ArrayList<>());
    lieu.setImages(new ArrayList<>());
    lieu.setServices(new ArrayList<>());

    when(lieuRepository.findById(1)).thenReturn(Optional.of(lieu));

    // Appel du service
    var result = lieuService.findById(1);

    // Vérifications
    assertEquals(lieu.getId(), result.id());
    assertEquals(lieu.getName(), result.name());
  }

  @Test
  void testFindById_notFound() {
    when(lieuRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(LieuNotFoundException.class, () -> lieuService.findById(1));
  }

  @Test
  void testFindAll_success() throws NoSuchFieldException, IllegalAccessException {
    Lieu lieu = new Lieu();
    lieu.setId(1);
    lieu.setName("Test Lieu");
    lieu.setFavorite_picture("");
    lieu.setReservations(new ArrayList<>());
    lieu.setImages(new ArrayList<>());
    lieu.setServices(new ArrayList<>());

    when(lieuRepository.findAll()).thenReturn(List.of(lieu));
    when(lieuRepository.findById(lieu.getId())).thenReturn(Optional.of(lieu));

    String replacement = "http://replacement.jpg";
    var field = LieuService.class.getDeclaredField("replacementImageLink");
    field.setAccessible(true);
    field.set(lieuService, replacement);

    var results = lieuService.findAll();
    assertEquals(1, results.size());
    assertEquals(lieu.getId(), results.getFirst().id());
    assertEquals(replacement, results.getFirst().favorite_picture());
  }

  @Test
  void testDeleteById_success() {
    when(lieuRepository.findById(1)).thenReturn(Optional.of(lieu));

    lieuService.deleteById(1);

    verify(lieuServicesService).deleteByLieuId(1);
    verify(reservationService).deleteByLieuId(1);
    verify(lieuImageService).deleteByLieuId(1);
    verify(lieuRepository).delete(lieu);
  }

  @Test
  void testDeleteById_notFound() {
    when(lieuRepository.findById(1)).thenReturn(Optional.empty());
    assertThrows(LieuNotFoundException.class, () -> lieuService.deleteById(1));
  }

  @Test
  void testGetFavoritePicture_success() {
    when(lieuRepository.findById(1)).thenReturn(Optional.of(lieu));

    var result = lieuService.getFavoritePicture(1);

    assertEquals(lieu.getFavorite_picture(), result);
  }

  @Test
  void testGetFavoritePicture_notFound() {
    when(lieuRepository.findById(1)).thenReturn(Optional.empty());
    assertThrows(LieuNotFoundException.class, () -> lieuService.getFavoritePicture(1));
  }

  @Test
  void testSetLieuFavoritePicture_success() {
    // Arrange
    int lieuId = 1;
    int imageId = 2;
    String imageUrl = "http://img.jpg";

    LieuImageByIdDto mockImage = new LieuImageByIdDto(imageId, imageUrl, lieuId);
    when(lieuImageService.getById(imageId)).thenReturn(mockImage);

    Lieu mockLieu = new Lieu();
    mockLieu.setId(lieuId);
    mockLieu.setImages(new ArrayList<>()); // <- initialisation
    mockLieu.getImages().add(new LieuImage(imageId, imageUrl, mockLieu));

    when(lieuRepository.findById(lieuId)).thenReturn(Optional.of(mockLieu));
    when(lieuRepository.save(any(Lieu.class))).thenAnswer(i -> i.getArgument(0));

    SetLieuFavoritePictureDto dto = new SetLieuFavoritePictureDto(lieuId, imageId);

    // Act
    lieuService.setLieuFavoritePicture(dto);

    // Assert
    assertEquals(imageUrl, mockLieu.getFavorite_picture());
    verify(lieuRepository, times(1)).save(mockLieu);
  }

  @Test
  void testSetLieuFavoritePicture_lieuNotFound() {
    // Arrange
    SetLieuFavoritePictureDto favoritePictureDto = new SetLieuFavoritePictureDto(1, 1);

    when(lieuRepository.findById(1)).thenReturn(java.util.Optional.empty());

    // Act & Assert
    assertThrows(
        LieuNotFoundException.class, () -> lieuService.setLieuFavoritePicture(favoritePictureDto));

    verify(lieuRepository, never()).save(any(Lieu.class));
  }

  @Test
  void testFindByProprietorId_userNotFound() {
    when(userService.findById(1)).thenReturn(null);
    assertThrows(LieuNotFoundException.class, () -> lieuService.findByProprietorId(1, "token"));
  }

  @Test
  void testFindByProprietorId_noLieux() {
    when(userService.findById(1)).thenReturn(user);
    when(lieuRepository.findByProprietor((double) user.getId()))
        .thenReturn(Collections.emptyList());
    assertThrows(LieuNotFoundException.class, () -> lieuService.findByProprietorId(1, "token"));
  }
}
