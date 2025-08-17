package com.locngo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locngo.constants.ReservationStatus;
import com.locngo.dto.CreateReservation;
import com.locngo.dto.ReservationDto;
import com.locngo.entity.Attendees;
import com.locngo.entity.Lieu;
import com.locngo.entity.Reservation;
import com.locngo.entity.ReservationAttendee;
import com.locngo.entity.User;
import com.locngo.exceptions.ReservationNotFoundException;
import com.locngo.repository.ReservationRepository;
import com.locngo.utils.JwtUtils;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

class ReservationServiceTest {

  @Mock private ReservationRepository reservationRepository;

  @Mock private AttendeesService attendeesService;

  @Mock private ReservationAttendeeService reservationAttendeeService;

  @Mock private JwtUtils jwtUtils;

  @Mock private UserService userService;

  @InjectMocks private ReservationService reservationService;

  @Spy private ObjectMapper objectMapper = new ObjectMapper();

  private Reservation reservation;
  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    reservationService = new ReservationService(objectMapper);
    // injecter manuellement les mocks
    reservationService.reservationRepository = reservationRepository;
    reservationService.attendeesService = attendeesService;
    reservationService.reservationAttendeeService = reservationAttendeeService;
    reservationService.jwtUtils = jwtUtils;
    reservationService.userService = userService;

    user = new User();
    user.setId(1);
    user.setEmail("test@example.com");
    user.setLastname("Doe");

    Lieu lieu = new Lieu();
    lieu.setId(10);
    lieu.setName("Lieu Test");
    lieu.setAddress("Address");
    lieu.setCity("City");
    lieu.setPostal_code("12345");
    lieu.setPrice("100.0");
    lieu.setDescription("Desc");
    lieu.setFavorite_picture("http://img.jpg");
    lieu.setReservations(new ArrayList<>());

    reservation = new Reservation();
    reservation.setId(1);
    reservation.setUser(user);
    reservation.setStatus(ReservationStatus.PENDING.name());
    reservation.setNb_person(1);
    reservation.setLieu(lieu);
    reservation.setAttendees(new ArrayList<>());

    Attendees attendee = new Attendees();
    attendee.setId(1);
    attendee.setName("Doe");
    attendee.setFirstname("John");

    ReservationAttendee reservationAttendee = new ReservationAttendee();
    reservationAttendee.setReservation(reservation);
    reservationAttendee.setAttendee(attendee);

    reservation.getAttendees().add(reservationAttendee);
  }

  @Test
  void testFindById_success() {
    when(reservationRepository.findById(1)).thenReturn(Optional.of(reservation));

    ReservationDto dto = reservationService.findById(1);

    assertEquals(reservation.getId(), dto.id());
    assertEquals(reservation.getStatus(), dto.status());
    assertEquals(1, dto.attendees().size());
    verify(reservationRepository, times(1)).findById(1);
  }

  @Test
  void testFindById_notFound() {
    when(reservationRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(ReservationNotFoundException.class, () -> reservationService.findById(99));
  }

  @Test
  void testCreateReservation_success() {
    reservation.setAttendees(new ArrayList<>());
    CreateReservation dto = new CreateReservation(0, null, null, null, 0, null, null);

    when(jwtUtils.getEmailFromJwtToken("token123")).thenReturn(user.getEmail());
    when(userService.findByEmail(user.getEmail())).thenReturn(user);
    when(reservationRepository.save(any())).thenReturn(reservation);

    ReservationDto result = reservationService.createReservation(dto, "token123");

    assertEquals(reservation.getId(), result.id());
    verify(reservationRepository, times(1)).save(any());
  }

  @Test
  void testDeleteById_success() {
    when(reservationRepository.findById(1)).thenReturn(Optional.of(reservation));

    reservationService.deleteById(1);

    verify(reservationRepository, times(1)).delete(reservation);
  }

  @Test
  void testDeleteById_notFound() {
    when(reservationRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> reservationService.deleteById(99));
  }

  @Test
  void testUpdateReservationStatus_success() {
    when(reservationRepository.findById(1)).thenReturn(Optional.of(reservation));
    when(reservationRepository.save(any())).thenReturn(reservation);

    ReservationDto updated =
        reservationService.updateReservationStatus(1, ReservationStatus.CONFIRMED);

    assertEquals(ReservationStatus.CONFIRMED.name(), updated.status());
    verify(reservationRepository, times(1)).save(reservation);
  }

  @Test
  void testUpdateReservationStatus_notFound() {
    when(reservationRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(
        ReservationNotFoundException.class,
        () -> reservationService.updateReservationStatus(99, ReservationStatus.CONFIRMED));
  }

  @Test
  void testGetReservationsByUserId_accessDenied() {
    String token = "token123";
    when(jwtUtils.getEmailFromJwtToken(token)).thenReturn("other@example.com");
    when(jwtUtils.getRolesFromJwtToken(token)).thenReturn(List.of());
    when(userService.findByEmail("other@example.com")).thenReturn(user);

    assertThrows(
        AccessDeniedException.class,
        () -> reservationService.getReservationsByUserId(user.getId() + 1, token));
  }
}
