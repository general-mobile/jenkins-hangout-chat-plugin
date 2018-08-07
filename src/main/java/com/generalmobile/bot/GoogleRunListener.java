package com.generalmobile.bot;

import com.google.jenkins.plugins.credentials.oauth.GoogleRobotCredentials;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.tasks.Publisher;

import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.logging.Logger;

@Extension
@SuppressWarnings("rawtypes")
public class GoogleRunListener extends RunListener<AbstractBuild> {

    private static final Logger logger = Logger.getLogger(GoogleRunListener.class.getName());

    public GoogleRunListener() {
        super(AbstractBuild.class);
    }

    @Override
    public void onCompleted(AbstractBuild r, TaskListener listener) {
        FineGrainedNotifier manager = getNotifier(r.getProject(), listener);
        if (manager != null)
            manager.completed(r);
        super.onCompleted(r, listener);
    }

    @Override
    public void onStarted(AbstractBuild r, TaskListener listener) {
        // getNotifier(r.getProject()).started(r);
        // super.onStarted(r, listener);
    }

    @Override
    public void onDeleted(AbstractBuild r) {
        // getNotifier(r.getProject()).deleted(r);
        // super.onDeleted(r);
    }

    @Override
    public void onFinalized(AbstractBuild r) {
        FineGrainedNotifier manager = getNotifier(r.getProject(), null);
        if (manager != null)
            manager.finalized(r);
        super.onFinalized(r);
    }

    FineGrainedNotifier getNotifier(AbstractProject project, TaskListener listener) {
        try {

            Map<Descriptor<Publisher>, Publisher> map = project.getPublishersList().toMap();
            for (Publisher publisher : map.values()) {
                if (publisher instanceof GoogleChatNotifier) {
                    GoogleRobotCredentials credentials = GoogleRobotCredentials.getById(((GoogleChatNotifier) publisher).getCredentialsId());
                    return new GoogleChatManager(((GoogleChatNotifier) publisher).authorize(credentials), (GoogleChatNotifier) publisher);
                }
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return new DisabledNotifier();
    }

}