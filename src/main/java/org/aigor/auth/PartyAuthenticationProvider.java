/**
 * Copyright (C) Zoomdata, Inc. 2012-2017. All rights reserved.
 */
package org.aigor.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
@Qualifier("partyAccess")
@Slf4j
public class PartyAuthenticationProvider implements ReactiveAuthenticationManager {
    private static final String MOTTO = "Glory to Ukraine";

    @Override
    public Mono<Authentication> authenticate(Authentication authentication)
            throws AuthenticationException {

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (MOTTO.equals(password)) {
            log.info("User '{}' joined the party!", name);
            return Mono.just(new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>()));
        } else {
            return Mono.empty();
        }
    }
}