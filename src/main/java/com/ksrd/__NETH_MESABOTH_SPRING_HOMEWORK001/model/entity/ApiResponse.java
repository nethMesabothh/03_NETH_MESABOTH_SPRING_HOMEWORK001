package com.ksrd.__NETH_MESABOTH_SPRING_HOMEWORK001.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse <T>{
  private boolean success;
  private String message;
  private HttpStatus status;
  private T payload;
  private LocalDateTime timestamp;
}
