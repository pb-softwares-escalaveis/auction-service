package org.infnet.auctionservice.mocks;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceMock {
    UserMock usuario1 = new UserMock(UUID.fromString("bddfe29d-3bd1-47e5-bf4b-03a50c65d534"), "Nicholas Puggian", "nicholas.wright@al.infnet.edu.br", true);
    UserMock usuario2 = new UserMock(UUID.fromString("51bd71ce-a60e-40cb-84ff-998688f9acdb"), "Guilherme Pirozi", "guilherme.pirozi@al.infnet.edu.br", true);
    UserMock usuario3 = new UserMock(UUID.fromString("64c58708-95cf-4859-aa16-bf1c6c880f7e"), "Jair Messias Bolsonaro", "jmbolsonaro@brasil.com", false);

    public UserMock getUser(UUID id) {
        return switch (id.toString()) {
            case "bddfe29d-3bd1-47e5-bf4b-03a50c65d534" -> usuario1;
            case "51bd71ce-a60e-40cb-84ff-998688f9acdb" -> usuario2;
            case "64c58708-95cf-4859-aa16-bf1c6c880f7e" -> usuario3;
            default -> throw new IllegalArgumentException("Invalid id: " + id);
        };
    }
}