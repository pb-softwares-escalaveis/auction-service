package org.infnet.auctionservice.projection;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProjectionRepository extends JpaRepository<UserProjection, UUID> {
}
