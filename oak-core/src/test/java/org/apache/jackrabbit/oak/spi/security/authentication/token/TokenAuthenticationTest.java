/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.oak.spi.security.authentication.token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.jcr.Credentials;
import javax.jcr.GuestCredentials;
import javax.jcr.SimpleCredentials;
import javax.security.auth.login.LoginException;

import org.apache.jackrabbit.api.security.authentication.token.TokenCredentials;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.oak.AbstractSecurityTest;
import org.apache.jackrabbit.oak.security.authentication.token.TokenProviderImpl;
import org.apache.jackrabbit.oak.spi.security.ConfigurationParameters;
import org.apache.jackrabbit.oak.spi.security.authentication.Authentication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * TokenAuthenticationTest...
 */
public class TokenAuthenticationTest extends AbstractSecurityTest {

    TokenAuthentication authentication;
    TokenProviderImpl tokenProvider;
    String userId;

    @Before
    public void before() throws Exception {
        super.before();

        root = adminSession.getLatestRoot();
        tokenProvider = new TokenProviderImpl(root,
                ConfigurationParameters.EMPTY,
                getUserConfiguration());

        userId = "testUser";
        getUserManager().createUser(userId, "pw");
        root.commit();
        authentication = new TokenAuthentication(tokenProvider);
    }

    @After
    public void after() throws Exception {
        try {
            Authorizable a = getUserManager().getAuthorizable(userId);
            if (a != null) {
                a.remove();
                root.commit();
            }
        } finally {
            super.after();
        }
    }

    @Test
    public void testAuthenticateWithoutTokenProvider() throws Exception {
        Authentication authentication = new TokenAuthentication(null);

        assertFalse(authentication.authenticate(new TokenCredentials("token")));
    }

    @Test
    public void testAuthenticateWithInvalidCredentials() throws Exception {
        List<Credentials> invalid = new ArrayList<Credentials>();
        invalid.add(new GuestCredentials());
        invalid.add(new SimpleCredentials(userId, new char[0]));

        for (Credentials creds : invalid) {
            assertFalse(authentication.authenticate(creds));
        }
    }

    @Test
    public void testAuthenticateWithInvalidTokenCredentials() throws Exception {
        try {
            authentication.authenticate(new TokenCredentials(UUID.randomUUID().toString()));
            fail("LoginException expected");
        } catch (LoginException e) {
            // success
        }
    }

    @Test
    public void testAuthenticate() throws Exception {
        TokenInfo info = tokenProvider.createToken(userId, Collections.<String, Object>emptyMap());
        assertTrue(authentication.authenticate(new TokenCredentials(info.getToken())));
    }

    @Test
    public void testGetTokenInfoBeforeAuthenticate() {
        try {
            authentication.getTokenInfo();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            // success
        }
    }

    @Test
    public void testGetTokenInfoAfterAuthenticate() throws Exception {
        TokenInfo info = tokenProvider.createToken(userId, Collections.<String, Object>emptyMap());
        authentication.authenticate(new TokenCredentials(info.getToken()));

        TokenInfo info2 = authentication.getTokenInfo();
        assertNotNull(info2);
        assertEquals(info.getUserId(), info2.getUserId());
    }
}