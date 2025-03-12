package com.ksrd.__NETH_MESABOTH_SPRING_HOMEWORK001.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketUpdateStatus {
  private List<Long> ticketIds;
  private Boolean paymentStatus;
}
