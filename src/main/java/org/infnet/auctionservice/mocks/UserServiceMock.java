package org.infnet.auctionservice.mocks;

import org.apache.catalina.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceMock {

    public boolean isUserAllowed(Long id) {
        return switch (id.toString()) {
            case "1", "2" -> true;
            case "666"-> false;
            default -> throw new IllegalArgumentException("User not found");
        };
    }
}