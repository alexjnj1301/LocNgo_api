package com.locngo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.locngo.dto.AttendeeDto;
import com.locngo.dto.SimpleReservationDto;
import com.locngo.entity.Attendees;
import com.locngo.entity.Reservation;
import com.locngo.entity.ReservationAttendee;
import com.locngo.repository.AttendeesRepository;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AttendeesServiceTest {

  @Mock private AttendeesRepository attendeesRepository;

  @InjectMocks private AttendeesService attendeesService;

  @Spy private ObjectMapper objectMapper = new ObjectMapper();

  private Attendees sampleAttendee;

  @BeforeEach
  void setup() {
    // Exemple d'attendee avec une réservation
    sampleAttendee = new Attendees();
    sampleAttendee.setId(1);
    sampleAttendee.setName("Doe");
    sampleAttendee.setFirstname("John");

    Reservation reservation = new Reservation();
    reservation.setId(100);
    // write the date in a format that Date constructor accepts
    reservation.setStart_date(
        Date.from(LocalDate.of(2025, 8, 17).atStartOfDay().toInstant(java.time.ZoneOffset.UTC)));
    reservation.setEnd_date(
        Date.from(LocalDate.of(2025, 8, 20).atStartOfDay().toInstant(java.time.ZoneOffset.UTC)));
    reservation.setNb_person(2);
    reservation.setReference("REF123");

    ReservationAttendee resAttendee = new ReservationAttendee();
    resAttendee.setReservation(reservation);

    sampleAttendee.setReservations(List.of(resAttendee));
  }

  @Test
  void testFindById() {
    when(attendeesRepository.findById(1)).thenReturn(sampleAttendee);

    AttendeeDto dto = attendeesService.findById(1);

    assertNotNull(dto);
    assertEquals("Doe", dto.name());
    assertEquals(1, dto.reservations().size());
    SimpleReservationDto resDto = dto.reservations().getFirst();
    assertEquals("REF123", resDto.reference());

    verify(attendeesRepository).findById(1);
  }

  @Test
  void testFindAll() {
    when(attendeesRepository.findAll()).thenReturn(List.of(sampleAttendee));

    List<AttendeeDto> allDtos = attendeesService.findAll();

    assertEquals(1, allDtos.size());
    assertEquals("John", allDtos.getFirst().firstname());
    verify(attendeesRepository).findAll();
  }

  @Test
  void testCreateAttendees() {
    when(attendeesRepository.save(sampleAttendee)).thenReturn(sampleAttendee);

    AttendeeDto dto = attendeesService.createAttendees(sampleAttendee);

    assertNotNull(dto);
    assertEquals("John", dto.firstname());
    verify(attendeesRepository).save(sampleAttendee);
  }

  @Test
  void testDeleteOrphans() {
    Attendees orphan = new Attendees();
    orphan.setId(2);
    orphan.setReservations(Collections.emptyList());

    when(attendeesRepository.findAll()).thenReturn(List.of(sampleAttendee, orphan));

    attendeesService.deleteOrphans();

    // Vérifie que deleteAll a été appelé avec la bonne liste
    verify(attendeesRepository)
        .deleteAll(
            argThat(
                iterable -> {
                  List<Attendees> list = new ArrayList<>();
                  iterable.forEach(list::add);
                  return list.size() == 1 && list.getFirst().getId() == 2;
                }));
    verify(attendeesRepository).flush();
  }
}
