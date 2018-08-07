package com.generalmobile.bot;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.chat.v1.HangoutsChat;
import com.google.api.services.chat.v1.model.ListSpacesResponse;
import com.google.api.services.chat.v1.model.Message;
import com.google.api.services.chat.v1.model.Space;
import com.google.jenkins.plugins.credentials.domains.DomainRequirementProvider;
import com.google.jenkins.plugins.credentials.domains.RequiresDomain;
import com.google.jenkins.plugins.credentials.oauth.GoogleRobotCredentials;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.*;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@RequiresDomain(value = ChatScopeRequirement.class)
public final class GoogleChatNotifier extends Notifier {


    private String credentialsId;
    private String room;
    private boolean startNotification;
    private boolean notifySuccess;
    private boolean notifyAborted;
    private boolean notifyNotBuilt;
    private boolean notifyUnstable;
    private boolean notifyRegression;
    private boolean notifyFailure;
    private boolean notifyBackToNormal;
    private boolean notifyRepeatedFailure;
    private boolean includeTestSummary;
    private boolean includeFailedTests;

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public String getRoom() {
        return room;
    }

    public boolean isIncludeFailedTests() {
        return includeFailedTests;
    }

    public void setIncludeFailedTests(boolean includeFailedTests) {
        this.includeFailedTests = includeFailedTests;
    }

    public boolean getStartNotification() {
        return startNotification;
    }

    public boolean getNotifySuccess() {
        return notifySuccess;
    }

    public boolean getNotifyAborted() {
        return notifyAborted;
    }

    public boolean getNotifyFailure() {
        return notifyFailure;
    }

    public boolean getNotifyNotBuilt() {
        return notifyNotBuilt;
    }

    public boolean getNotifyUnstable() {
        return notifyUnstable;
    }

    public boolean getNotifyRegression() {
        return notifyRegression;
    }

    public boolean getNotifyBackToNormal() {
        return notifyBackToNormal;
    }

    public boolean includeTestSummary() {
        return includeTestSummary;
    }

    public boolean includeFailedTests() {
        return includeFailedTests;
    }

    public boolean getNotifyRepeatedFailure() {
        return notifyRepeatedFailure;
    }

    @DataBoundConstructor
    public GoogleChatNotifier(String credentialsId, String room, boolean startNotification, boolean notifySuccess, boolean notifyAborted, boolean notifyNotBuilt, boolean notifyUnstable, boolean notifyRegression, boolean notifyFailure, boolean notifyBackToNormal, boolean notifyRepeatedFailure, boolean includeTestSummary, boolean includeFailedTests) {
        super();
        this.credentialsId = checkNotNull(credentialsId);
        this.room = checkNotNull(room);
        this.startNotification = startNotification;
        this.notifySuccess = notifySuccess;
        this.notifyAborted = notifyAborted;
        this.notifyNotBuilt = notifyNotBuilt;
        this.notifyUnstable = notifyUnstable;
        this.notifyRegression = notifyRegression;
        this.notifyFailure = notifyFailure;
        this.notifyBackToNormal = notifyBackToNormal;
        this.notifyRepeatedFailure = notifyRepeatedFailure;
        this.includeTestSummary = includeTestSummary;
        this.includeFailedTests = includeFailedTests;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        return true;
    }

    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        Map<Descriptor<Publisher>, Publisher> map = build.getProject().getPublishersList().toMap();
        for (Publisher publisher : map.values()) {
            if (publisher instanceof GoogleChatNotifier) {
                try {
                    listener.getLogger().println("Deneme " + credentialsId);
                    GoogleRobotCredentials credentials = GoogleRobotCredentials.getById(getCredentialsId());
                    GoogleChatManager manager = new GoogleChatManager(((GoogleChatNotifier) publisher).authorize(credentials), (GoogleChatNotifier) publisher);

                    if (startNotification)
                        manager.sendStartMessage(build, listener);

                } catch (GeneralSecurityException ex) {
                    listener.getLogger().println(ex.getMessage());
                    build.setResult(Result.FAILURE);
                    return false;
                }
            }
        }
        return super.prebuild(build, listener);
    }

    public Credential authorize(GoogleRobotCredentials credentials) throws GeneralSecurityException {
        GoogleRobotCredentials googleRobotCredentials = credentials.forRemote(getRequirement());
        return googleRobotCredentials.getGoogleCredential(getRequirement());
    }

    private ChatScopeRequirement getRequirement() {
        return DomainRequirementProvider.of(getClass(), ChatScopeRequirement.class);
    }


    public String getCredentialsId() {
        return credentialsId;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String credentialsId;
        private String room;
        private boolean startNotification;
        private boolean notifySuccess;
        private boolean notifyAborted;
        private boolean notifyNotBuilt;
        private boolean notifyUnstable;
        private boolean notifyRegression;
        private boolean notifyFailure;
        private boolean notifyBackToNormal;
        private boolean notifyRepeatedFailure;
        private boolean includeTestSummary;
        private boolean includeFailedTests;


        @Override
        public String getDisplayName() {
            return "Hangouts Chat Notifier";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public Publisher newInstance(StaplerRequest sr, JSONObject formData) throws FormException {

            String credentialsId = sr.getParameter("credentialsId");
            String room = sr.getParameter("room");
            boolean startNotification = "true".equals(sr.getParameter("googleChatStartNotification"));
            boolean notifySuccess = "true".equals(sr.getParameter("googleChatNotifySuccess"));
            boolean notifyAborted = "true".equals(sr.getParameter("googleChatNotifyAborted"));
            boolean notifyNotBuilt = "true".equals(sr.getParameter("googleChatNotifyNotBuilt"));
            boolean notifyUnstable = "true".equals(sr.getParameter("googleChatNotifyUnstable"));
            boolean notifyRegression = "true".equals(sr.getParameter("googleChatNotifyRegression"));
            boolean notifyFailure = "true".equals(sr.getParameter("googleChatNotifyFailure"));
            boolean notifyBackToNormal = "true".equals(sr.getParameter("googleChatNotifyBackToNormal"));
            boolean notifyRepeatedFailure = "true".equals(sr.getParameter("googleChatNotifyRepeatedFailure"));
            boolean includeTestSummary = "true".equals(sr.getParameter("includeTestSummary"));
            boolean includeFailedTests = "true".equals(sr.getParameter("includeFailedTests"));

            return new GoogleChatNotifier(credentialsId, room, startNotification, notifySuccess, notifyAborted, notifyNotBuilt, notifyUnstable, notifyRegression, notifyFailure, notifyBackToNormal, notifyRepeatedFailure, includeTestSummary, includeFailedTests);
        }

        @Override
        public boolean configure(StaplerRequest sr, JSONObject json) throws FormException {
            credentialsId = sr.getParameter("credentialsId");
            room = sr.getParameter("room");
            startNotification = "true".equals(sr.getParameter("googleChatStartNotification"));
            notifySuccess = "true".equals(sr.getParameter("googleChatNotifySuccess"));
            notifyAborted = "true".equals(sr.getParameter("googleChatNotifyAborted"));
            notifyNotBuilt = "true".equals(sr.getParameter("googleChatNotifyNotBuilt"));
            notifyUnstable = "true".equals(sr.getParameter("googleChatNotifyUnstable"));
            notifyRegression = "true".equals(sr.getParameter("googleChatNotifyRegression"));
            notifyFailure = "true".equals(sr.getParameter("googleChatNotifyFailure"));
            notifyBackToNormal = "true".equals(sr.getParameter("googleChatNotifyBackToNormal"));
            notifyRepeatedFailure = "true".equals(sr.getParameter("googleChatNotifyRepeatedFailure"));
            includeTestSummary = "true".equals(sr.getParameter("includeTestSummary"));
            includeFailedTests = "true".equals(sr.getParameter("includeFailedTests"));
            save();

            return super.configure(sr, json);
        }
    }
}
