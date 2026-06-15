package org.infnet.auctionservice.projection;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_projections")
@Getter @Setter
@NoArgsConstructor
public class UserProjection {
    @Id
    private UUID id;
    private String fullName;
    private String profilePic;
    private String email;
    private Float score;
    private String country;
    private String state;
    private String city;
    private Instant createdAt;
}
