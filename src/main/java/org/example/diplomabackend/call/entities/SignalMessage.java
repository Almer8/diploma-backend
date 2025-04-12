package org.example.diplomabackend.call.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignalMessage {

    private String type;
    private Object payload;
    private String sender;
}
