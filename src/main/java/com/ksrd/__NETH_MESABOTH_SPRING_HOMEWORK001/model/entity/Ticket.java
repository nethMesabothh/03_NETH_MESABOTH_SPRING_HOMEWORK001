package com.ksrd.__NETH_MESABOTH_SPRING_HOMEWORK001.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
  private Long ticketId;
  private String passengerName;
  private LocalDate travelDate;
  private String sourceStation;
  private String destinationStation;
  private Integer price;
  private Boolean paymentStatus ;
  private TICKET_STATUS ticketStatus;
  private String seatNumber;

}
