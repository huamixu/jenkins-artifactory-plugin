package org.jfrog.hudson.pipeline.steps;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.jfrog.hudson.pipeline.docker.DockerAgentUtils;
import org.jfrog.hudson.util.JenkinsBuildInfoLog;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by romang on 5/2/16.
 */
public class DockerPush extends AbstractStepImpl {

    private final String imageTag;
    private String username;
    private String password;

    @DataBoundConstructor
    public DockerPush(String imageTag, String username, String password) {
        this.imageTag = imageTag;
        this.username = username;
        this.password = password;
    }

    public String getImageTag() {
        return imageTag;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static class Execution extends AbstractSynchronousStepExecution<Boolean> {
        private static final long serialVersionUID = 1L;

        @Inject(optional = true)
        private transient DockerPush step;

        @StepContextParameter
        private transient TaskListener listener;

        @StepContextParameter
        private transient Run build;

        @StepContextParameter
        private transient Launcher launcher;

        @StepContextParameter
        private transient FilePath ws;

        @Override
        protected Boolean run() throws Exception {
            JenkinsBuildInfoLog log = new JenkinsBuildInfoLog(listener);
            log.info("Pushing image: " + step.getImageTag());

            DockerAgentUtils.pushImage(launcher, step.getImageTag(), step.getUsername(), step.getPassword());
            log.info("Successfully pushed image.");
            return true;
        }
    }

    @Extension
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(DockerPush.Execution.class);
        }

        @Override
        public String getFunctionName() {
            return "dockerPush";
        }

        @Override
        public String getDisplayName() {
            return "Artifactory docker push";
        }
    }

}

