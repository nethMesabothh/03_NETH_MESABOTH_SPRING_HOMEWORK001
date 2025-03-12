package com.ksrd.__NETH_MESABOTH_SPRING_HOMEWORK001.controller;

import com.ksrd.__NETH_MESABOTH_SPRING_HOMEWORK001.model.entity.ApiResponse;
import com.ksrd.__NETH_MESABOTH_SPRING_HOMEWORK001.model.entity.TICKET_STATUS;
import com.ksrd.__NETH_MESABOTH_SPRING_HOMEWORK001.model.entity.Ticket;
import com.ksrd.__NETH_MESABOTH_SPRING_HOMEWORK001.model.entity.TicketUpdateStatus;
import com.ksrd.__NETH_MESABOTH_SPRING_HOMEWORK001.model.request.TicketRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


@RestController
@RequestMapping("api/v1/tickets")
public class TicketController {

  private final static List<Ticket> TICKETS = new ArrayList<>();
  private final static AtomicLong ATOMIC_LONG = new AtomicLong(4L);

  LocalDate localDate_1 = LocalDate.parse("2024-01-20");
  LocalDate localDate_2 = LocalDate.parse("2025-02-21");
  LocalDate localDate_3 = LocalDate.parse("2024-10-18");


  public TicketController() {
    TICKETS.add(new Ticket(
            1L, "Both", localDate_1, "Station E", "Station F", 150, false, TICKET_STATUS.CANCELLED, "C3"
    ));
    TICKETS.add(new Ticket(
            2L, "Mesa", localDate_2, "Station E", "Station F", 150, false, TICKET_STATUS.BOOKED, "C3"
    ));
    TICKETS.add(new Ticket(
            3L, "Neth", localDate_3, "Station E", "Station F", 150, false, TICKET_STATUS.COMPLETED, "C3"
    ));
  }


  @Operation(summary = "Get all tickets")
  @GetMapping
  public ResponseEntity<ApiResponse<Map<String, Object>>> getAllTickets(@RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "10") Integer size) {

    if (page < 0 || size < 1) {
      throw new IllegalArgumentException("Page must be >= 0 and size >= 1");
    }

    int skip = (page - 1) * size; // 2
    int limit = size;
    int totalPages = (int) Math.ceil((double) TICKETS.size() / size);

    List<Ticket> paginationTicket = TICKETS.stream().skip(skip).limit(limit).toList();

    Map<String, Object> pagination = new HashMap<>();
    pagination.put("totalElement", TICKETS.size());
    pagination.put("currentPage", page);
    pagination.put("pageSize", size);
    pagination.put("totalPage", totalPages);

    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("items", paginationTicket);
    payload.put("pagination", pagination);

    ApiResponse<Map<String, Object>> response = new ApiResponse<>(
            true,
            "All tickets retrieved successfully",
            HttpStatus.OK,
            payload,
            LocalDateTime.now()
    );

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Operation(summary = "Create a new ticket")
  @PostMapping
  public ResponseEntity<ApiResponse<Map<String, Object>>> createTicket(@RequestBody TicketRequest request) {
    Ticket ticket = new Ticket(ATOMIC_LONG.getAndIncrement(), request.getPassengerName(), request.getTravelDate(), request.getSourceStation(), request.getDestinationStation(), request.getPrice(), request.isPaymentStatus(), request.getTicketStatus(), request.getSeatNumber());

    TICKETS.add(ticket);

    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("items", ticket);

    ApiResponse<Map<String, Object>> response = new ApiResponse<>(
            true,
            "Ticket created successfully",
            HttpStatus.CREATED,
            payload,
            LocalDateTime.now()
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(summary = "Get a ticket by ID")
  @GetMapping("/{ticket-id}")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getTicketById(@PathVariable("ticket-id") Long ticketId) {

    for (Ticket ticket : TICKETS) {
      if (ticket.getTicketId().equals(ticketId)) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("items", ticket);
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                true,
                "Ticket retrieved successfully",
                HttpStatus.OK,
                payload,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
      }
    }

    ApiResponse<Map<String, Object>> response = new ApiResponse<>(
            false,
            "no ticket found with ID : " + ticketId,
            HttpStatus.NOT_FOUND,
            null,
            LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @Operation(summary = "Search tickets by passenger name")
  @GetMapping("/search")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getTicketByName(@RequestParam String name) {

    List<Ticket> list = new ArrayList<>();
    for (Ticket ticket : TICKETS) {

      String normalizedPassengerName = ticket.getPassengerName().trim().toLowerCase();
      String normalizedSearchTerm = name.trim().toLowerCase();

      if (normalizedPassengerName.contains(normalizedSearchTerm)) {
        list.add(ticket);
      }
    }

    if (list.isEmpty()) {
      ApiResponse<Map<String, Object>> response = new ApiResponse<>(
              true,
              "Ticket searched not found!",
              HttpStatus.NOT_FOUND,
              null,
              LocalDateTime.now()
      );

      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("items", list);
    ApiResponse<Map<String, Object>> response = new ApiResponse<>(
            true,
            "Ticket searched successfully",
            HttpStatus.OK,
            payload,
            LocalDateTime.now()
    );

    return ResponseEntity.status(HttpStatus.OK).body(response);

  }

  @Operation(summary = "Filter tickets by status and travel date")
  @GetMapping("/filter")
  public ResponseEntity<ApiResponse<Map<String, Object>>> filterTicketByStatusAndDate(@RequestParam TICKET_STATUS ticketStatus, @RequestParam LocalDate date) {
    List<Ticket> filterTicket = TICKETS.stream().filter(ticket -> ticket.getTicketStatus().equals(ticketStatus)).filter(ticket -> ticket.getTravelDate().equals(date)).toList();

    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("items", filterTicket);
    ApiResponse<Map<String, Object>> response = new ApiResponse<>(
            true,
            "Ticket searched successfully",
            HttpStatus.OK,
            payload,
            LocalDateTime.now()
    );

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Update an existing ticket by ID")
  @PutMapping("/{ticket-id}")
  public ResponseEntity<ApiResponse<Map<String, Object>>> updateTicketById(@PathVariable("ticket-id") Long ticketId, @RequestBody TicketRequest request) {

    for (Ticket ticket : TICKETS) {
      if (ticket.getTicketId().equals(ticketId)) {
        ticket.setPassengerName(request.getPassengerName());
        ticket.setTravelDate(request.getTravelDate());
        ticket.setSourceStation(request.getSourceStation());
        ticket.setDestinationStation(request.getDestinationStation());
        ticket.setPrice(request.getPrice());
        ticket.setPaymentStatus(request.isPaymentStatus());
        ticket.setTicketStatus(request.getTicketStatus());
        ticket.setSeatNumber(request.getSeatNumber());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("items", ticket);
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                true,
                "Ticket updated successfully",
                HttpStatus.OK,
                payload,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
      } else {
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                false,
                "no ticket found with ID : " + ticketId,
                HttpStatus.NOT_FOUND,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

      }
    }

    return null;
  }

  @Operation(summary = "Delete a ticket by ID")
  @DeleteMapping("/{ticket-id}")
  public ResponseEntity<ApiResponse<Map<String, Object>>> deleteTicketById(@PathVariable("ticket-id") Long ticketId) {

    for (Ticket ticket : TICKETS) {
      if (ticket.getTicketId().equals(ticketId)) {
        TICKETS.remove(ticket);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("items", ticket);
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                true,
                "Ticket deleted successfully",
                HttpStatus.OK,
                payload,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
      }
    }

    ApiResponse<Map<String, Object>> response = new ApiResponse<>(
            true,
            "Ticket deleted failed, ID not found!",
            HttpStatus.NOT_FOUND,
            null,
            LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @Operation(summary = "Bulk create tickets")
  @PostMapping("/bulk")
  public ResponseEntity<ApiResponse<Map<String, Object>>> createBulkTicket(@RequestBody List<TicketRequest> requests) {

    List<Ticket> tickets = new ArrayList<>();

    for (TicketRequest request : requests) {
      Ticket ticket = new Ticket(ATOMIC_LONG.getAndIncrement(), request.getPassengerName(), request.getTravelDate(), request.getSourceStation(), request.getDestinationStation(), request.getPrice(), request.isPaymentStatus(), request.getTicketStatus(), request.getSeatNumber());
      TICKETS.add(ticket);
      tickets.add(ticket);

    }
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("items", tickets);
    ApiResponse<Map<String, Object>> response = new ApiResponse<>(
            true,
            "Ticket created successfully",
            HttpStatus.CREATED,
            payload,
            LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.OK).body(response);

  }

  @Operation(summary = "Bulk update payment status for multiple tickets")
  @PatchMapping
  public ResponseEntity<ApiResponse<Map<String, Object>>> bulkUpdatePaymentStatus(@RequestBody TicketUpdateStatus request) {

    List<Ticket> updateTickets = new ArrayList<>();

    for (Long ticketIds : request.getTicketIds()) {
      Optional<Ticket> optionalTicket = TICKETS.stream().filter(ticket -> ticket.getTicketId().equals(ticketIds)).findFirst();

      if (optionalTicket.isPresent()) {
        Ticket ticket = optionalTicket.get();
        ticket.setPaymentStatus(request.getPaymentStatus());
        TICKETS.set(ticket.getTicketId().intValue() - 1, ticket);
        updateTickets.add(ticket);

      }
    }
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("items", updateTickets);
    ApiResponse<Map<String, Object>> response = new ApiResponse<>(
            true,
            "Ticket status updated successfully",
            HttpStatus.OK,
            payload,
            LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

}
