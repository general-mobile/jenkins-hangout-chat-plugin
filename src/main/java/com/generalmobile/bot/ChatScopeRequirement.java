package com.generalmobile.bot;

import com.google.common.collect.ImmutableList;
import com.google.jenkins.plugins.credentials.oauth.GoogleOAuth2ScopeRequirement;

import java.util.Collection;

/**
 * The required OAuth2 scopes for managing Google Drive files.
 */
public class ChatScopeRequirement extends GoogleOAuth2ScopeRequirement {
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getScopes() {
        return ImmutableList.of("https://www.googleapis.com/auth/chat.bot");
    }
}

