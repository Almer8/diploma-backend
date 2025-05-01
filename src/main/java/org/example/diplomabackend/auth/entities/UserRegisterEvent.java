package org.example.diplomabackend.auth.entities;


import org.springframework.context.ApplicationEvent;

public class UserRegisterEvent extends ApplicationEvent {
   private final Long id;

   public UserRegisterEvent(Object source, Long id) {
      super(source);
      this.id = id;
   }
   public Long getId() {
      return id;
   }
}
