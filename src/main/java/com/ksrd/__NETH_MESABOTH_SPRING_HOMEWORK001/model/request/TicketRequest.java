package com.ksrd.__NETH_MESABOTH_SPRING_HOMEWORK001.model.request;

import java.time.LocalDate;

import com.ksrd.__NETH_MESABOTH_SPRING_HOMEWORK001.model.entity.TICKET_STATUS;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequest {
  private String passengerName;
  private LocalDate travelDate;
  private String sourceStation;
  private String destinationStation;
  private int price;
  private boolean paymentStatus;
  private TICKET_STATUS ticketStatus;
  private String seatNumber;
}
