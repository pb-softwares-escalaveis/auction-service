package org.infnet.auctionservice.mocks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter@Setter
@AllArgsConstructor
public class UserMock {
    private UUID id;
    private String name;
    private String email;
    private Boolean allowed;
}