/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.vagrant.command;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.api.extexecution.input.InputProcessor;
import org.netbeans.api.extexecution.input.InputProcessors;
import org.netbeans.api.extexecution.input.LineProcessor;
import org.netbeans.api.project.Project;
import org.netbeans.modules.vagrant.VagrantStatus;
import org.netbeans.modules.vagrant.VagrantVersion;
import org.netbeans.modules.vagrant.Versionable;
import org.netbeans.modules.vagrant.options.VagrantOptions;
import org.netbeans.modules.vagrant.preferences.VagrantPreferences;
import org.netbeans.modules.vagrant.utils.StringUtils;
import org.netbeans.modules.vagrant.utils.UiUtils;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.InputOutput;

/**
 *
 * @author junichi11
 */
public final class Vagrant {

    public enum BOX {

        ADD("add"), // NOI18N
        LIST("list"), // NOI18N
        REMOVE("remove"); // NOI18N
        private final String command;

        private BOX(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }
    }

    public enum PLUGIN {

        INSTALL("install"), // NOI18N
        LICENCE("license"), // NOI18N
        LIST("list"), // NOI18N
        UNINSTALL("uninstall"), // NOI18N
        UPDATE("update"); // NOI18N
        private final String command;

        private PLUGIN(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }
    }
    private static final Logger LOGGER = Logger.getLogger(Vagrant.class.getName());
    // commands
    public static final String BOX_COMMAND = "box"; // NOI18N
    public static final String DESTROY_COMMAND = "destroy"; // NOI18N
    public static final String HALT_COMMAND = "halt"; // NOI18N
    public static final String HELP_COMMAND = "help"; // NOI18N
    public static final String INIT_COMMAND = "init"; // NOI18N
    public static final String LIST_COMMANDS_COMMAND = "list-commands"; // NOI18N v1.5.0+
    public static final String PLUGIN_COMMAND = "plugin"; // NOI18N
    public static final String PROVISION_COMMAND = "provision"; // NOI18N
    public static final String RELOAD_COMMAND = "reload"; // NOI18N
    public static final String RESUME_COMMAND = "resume"; // NOI18N
    public static final String SHARE_COMMAND = "share"; // NOI18N v1.5.0+
    public static final String SSH_COMMAND = "ssh"; // NOI18N
    public static final String SSH_CONFIG_COMMAND = "ssh-config"; // NOI18N
    public static final String STATUS_COMMAND = "status"; // NOI18N
    public static final String SUSPEND_COMMAND = "suspend"; // NOI18N
    public static final String UP_COMMAND = "up"; // NOI18N
    // params
    private static final String FORCE_PARAM = "--force"; // NOI18N
    private static final String HELP_PARAM = "--help"; // NOI18N
    private static final String VERSION_PARAM = "--version"; // NOI18N
    // vagrant
    private final String path;
    public static final String NAME = "vagrant"; // NOI18N
    public static final String LONG_NAME = NAME + VagrantUtils.getScriptExt();
    private File workDir = null;
    private List<String> additionalParameters = new ArrayList<String>();
    private String command = ""; // NOI18N
    private String title = ""; // NOI18N
    // descriptor
    private ExecutionDescriptor descriptor;
    private static final ExecutionDescriptor DEFAULT_EXECUTION_DESCRIPTOR = new ExecutionDescriptor()
            .optionsPath(UiUtils.OPTIONS_PATH)
            .controllable(true)
            .frontWindow(true)
            .frontWindowOnError(true)
            .inputVisible(true)
            .showProgress(true);
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private Project project;
    private boolean noInfo = false;
    private final List<String> fullCommand = new ArrayList<String>();
    private Versionable version;

    public Vagrant(String path) {
        this.path = path;
    }

    /**
     * Get Vagrant instance for the path in Options.
     *
     * @return Vagrant instance
     * @throws InvalidVagrantExecutableException
     */
    public static Vagrant getDefault() throws InvalidVagrantExecutableException {
        VagrantOptions options = VagrantOptions.getInstance();
        String vagrantPath = options.getVagrantPath();
        String error = validate(vagrantPath);
        if (error == null) {
            Vagrant vagrant = new Vagrant(vagrantPath);
            return vagrant;
        }
        UiUtils.showOptions();
        throw new InvalidVagrantExecutableException(error);
    }

    /**
     * Add change listener.
     *
     * @param listener
     */
    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    /**
     * Remove change listener.
     *
     * @param listener
     */
    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    /**
     * Set project.
     *
     * @param project
     * @return Vagrant
     */
    public Vagrant setProject(Project project) {
        this.project = project;
        return this;
    }

    @NbBundle.Messages({
        "Vagrant.path.invalid.empty=Vagrant path is empty"
    })
    private static String validate(String vagrantPath) {
        if (StringUtils.isEmpty(vagrantPath)) {
            return Bundle.Vagrant_path_invalid_empty();
        }
        return null;
    }

    public Vagrant workDir(File workDir) {
        this.workDir = workDir;
        return this;
    }

    public Vagrant noInfo(boolean noInfo) {
        this.noInfo = noInfo;
        return this;
    }

    private void additionalParameters(List<String> additionalParameters) {
        this.additionalParameters.addAll(additionalParameters);
    }

    @NbBundle.Messages({
        "# {0} - subcommand",
        "Vagrant.run.box=Vagrant (box {0})"
    })
    public Future<Integer> box(BOX subcommand, List<String> params) {
        ArrayList<String> allParams = new ArrayList<String>();
        allParams.add(subcommand.getCommand());
        allParams.addAll(params);
        return runCommand(null, BOX_COMMAND, Bundle.Vagrant_run_box(subcommand.getCommand()), allParams);
    }

    @NbBundle.Messages({
        "# {0} - subcommand",
        "Vagrant.run.plugin=Vagrant (plugin {0})"
    })
    public Future<Integer> plugin(PLUGIN subcommand, List<String> params) {
        ArrayList<String> allParams = new ArrayList<String>();
        allParams.add(subcommand.getCommand());
        allParams.addAll(params);
        return runCommand(null, PLUGIN_COMMAND, Bundle.Vagrant_run_plugin(subcommand.getCommand()), allParams);
    }

    @NbBundle.Messages("Vagrant.run.init=Vagrant (init)")
    public Future<Integer> init(Project project, String boxName, String boxUrl) {
        ArrayList<String> params = new ArrayList<String>();
        if (!boxName.isEmpty()) {
            params.add(boxName);
        } else {
            return null;
        }

        if (!boxUrl.isEmpty()) {
            params.add(boxUrl);
        }

        return runCommand(project, INIT_COMMAND, Bundle.Vagrant_run_init(), params);
    }

    @NbBundle.Messages("Vagrant.run.up=Vagrant (up)")
    public Future<Integer> up(Project project) {
        return runCommand(project, UP_COMMAND, Bundle.Vagrant_run_up());
    }

    public Future<Integer> up(Project project, String name) {
        return runCommand(project, UP_COMMAND, Bundle.Vagrant_run_up(), Arrays.asList(name));
    }

    @NbBundle.Messages("Vagrant.run.reload=Vagrant (reload)")
    public Future<Integer> reload(Project project) {
        return runCommand(project, RELOAD_COMMAND, Bundle.Vagrant_run_reload());
    }

    public Future<Integer> reload(Project project, String name) {
        return runCommand(project, RELOAD_COMMAND, Bundle.Vagrant_run_reload(), Arrays.asList(name));
    }

    @NbBundle.Messages("Vagrant.run.halt=Vagrant (halt)")
    public Future<Integer> halt(Project project) {
        return runCommand(project, HALT_COMMAND, Bundle.Vagrant_run_halt());
    }

    public Future<Integer> halt(Project project, String name) {
        return runCommand(project, HALT_COMMAND, Bundle.Vagrant_run_halt(), Arrays.asList(name));
    }

    @NbBundle.Messages("Vagrant.run.suspend=Vagrant (suspend)")
    public Future<Integer> suspend(Project project) {
        return runCommand(project, SUSPEND_COMMAND, Bundle.Vagrant_run_suspend());
    }

    public Future<Integer> suspend(Project project, String name) {
        return runCommand(project, SUSPEND_COMMAND, Bundle.Vagrant_run_suspend(), Arrays.asList(name));
    }

    @NbBundle.Messages("Vagrant.run.resume=Vagrant (resume)")
    public Future<Integer> resume(Project project) {
        return runCommand(project, RESUME_COMMAND, Bundle.Vagrant_run_resume());
    }

    public Future<Integer> resume(Project project, String name) {
        return runCommand(project, RESUME_COMMAND, Bundle.Vagrant_run_resume(), Arrays.asList(name));
    }

    @NbBundle.Messages("Vagrant.run.share=Vagrant (share)")
    public Future<Integer> share(Project project) {
        return runCommand(project, SHARE_COMMAND, Bundle.Vagrant_run_share());
    }

    @NbBundle.Messages("Vagrant.run.status=Vagrant (status)")
    public Future<Integer> status(Project project) {
        return runCommand(project, STATUS_COMMAND, Bundle.Vagrant_run_status());
    }

    @NbBundle.Messages("Vagrant.run.ssh=Vagrant (ssh)")
    public Future<Integer> ssh(Project project) {
        return runCommand(project, SSH_COMMAND, Bundle.Vagrant_run_ssh());
    }

    public Future<Integer> ssh(Project project, String name) {
        return runCommand(project, SSH_COMMAND, Bundle.Vagrant_run_ssh(), Arrays.asList(name));
    }

    @NbBundle.Messages("Vagrant.run.ssh.config=Vagrant (ssh-config)")
    public Future<Integer> sshConfig(Project project) {
        return runCommand(project, SSH_CONFIG_COMMAND, Bundle.Vagrant_run_ssh_config());
    }

    @NbBundle.Messages("Vagrant.run.destroy=Vagrant (destroy)")
    public Future<Integer> destroy(Project project) {
        // require a TTY
        return runCommand(project, DESTROY_COMMAND, Bundle.Vagrant_run_destroy(), Collections.singletonList(FORCE_PARAM));
    }

    public Future<Integer> destroy(Project project, String name) {
        // require a TTY
        return runCommand(project, DESTROY_COMMAND, Bundle.Vagrant_run_destroy(), Arrays.asList(FORCE_PARAM, name));
    }

    @NbBundle.Messages("Vagrant.run.provision=Vagrant (provision)")
    public Future<Integer> provisiton(Project project) {
        return runCommand(project, PROVISION_COMMAND, Bundle.Vagrant_run_provision());
    }

    public Future<Integer> provisiton(Project project, String name) {
        return runCommand(project, PROVISION_COMMAND, Bundle.Vagrant_run_provision(), Arrays.asList(name));
    }

    @NbBundle.Messages("Vagrant.run.version=Vagrant (--version)")
    public Future<Integer> version() {
        return runCommand(null, VERSION_PARAM, Bundle.Vagrant_run_version());
    }

    /**
     * Get vagrant version.
     *
     * @return version
     */
    public Versionable getVersion() {
        if (version == null) {
            try {
                String versionNumber = getVersionNumber();
                version = new VagrantVersion(versionNumber);
            } catch (InvalidVagrantExecutableException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return version;
    }

    /**
     * Get Vagrant version.
     *
     * @return Vagrant version
     * @throws InvalidVagrantExecutableException
     */
    @NbBundle.Messages("Vagrant.version.error=Not Vagrant script.")
    public String getVersionNumber() throws InvalidVagrantExecutableException {
        final VagrantLineProcessor lineProcessor = new VagrantLineProcessor();
        descriptor = getSilentDescriptor()
                .outProcessorFactory(getOutputProcessorFactory(lineProcessor));
        Future<Integer> result = version();
        try {
            getResult(result);
        } catch (InvalidVagrantExecutableException ex) {
            throw new InvalidVagrantExecutableException(Bundle.Vagrant_version_error());
        }
        return lineProcessor.getText();
    }

    /**
     * Get boxes.
     *
     * @return box list
     */
    public List<String> getBoxList() {
        final VagrantLineProcessor lineProcessor = new VagrantLineProcessor();
        descriptor = getSilentDescriptor()
                .outProcessorFactory(getOutputProcessorFactory(lineProcessor));
        Future<Integer> result = box(BOX.LIST, Collections.<String>emptyList());
        try {
            getResult(result);
        } catch (InvalidVagrantExecutableException ex) {
            Exceptions.printStackTrace(ex);
        }
        return lineProcessor.getList();
    }

    /**
     * Get plubins. Format is "plugin_name (version number)".
     *
     * @return plugin list
     */
    public List<String> getPluginList() {
        final VagrantLineProcessor lineProcessor = new VagrantLineProcessor();
        descriptor = getSilentDescriptor()
                .outProcessorFactory(getOutputProcessorFactory(lineProcessor));
        Future<Integer> result = plugin(PLUGIN.LIST, Collections.<String>emptyList());
        try {
            getResult(result);
        } catch (InvalidVagrantExecutableException ex) {
            Exceptions.printStackTrace(ex);
        }
        return lineProcessor.getList();
    }

    /**
     * Get command list.
     *
     * @return command list
     */
    public List<String> getCommandListLines() throws InvalidVagrantExecutableException {
        Versionable v = getVersion();
        boolean isListCommands = false;
        if (v == null) {
            return Collections.emptyList();
        }
        if (v.getMajor() == 1 && v.getMinor() <= 4) {
            command = HELP_PARAM;
        } else {
            command = LIST_COMMANDS_COMMAND;
            isListCommands = true;
        }
        final VagrantLineProcessor lineProcessor = new VagrantLineProcessor();
        descriptor = getSilentDescriptor()
                .outProcessorFactory(getOutputProcessorFactory(lineProcessor));
        Future<Integer> result = ExecutionService.newService(getProcessBuilder(), descriptor, "").run(); // NOI18N
        getResult(result);

        return getCommands(lineProcessor.getList(), isListCommands);
    }

    private List<String> getCommands(List<String> lines) {
        return getCommands(lines, false);
    }

    private List<String> getCommands(List<String> lines, boolean isListCommands) {
        boolean isStartPosition = false;
        List<String> commands = new LinkedList<String>();
        for (String line : lines) {
            if (line.toLowerCase().matches("\\A.*(subcommands|common commands).*\\z")) { // NOI18N
                isStartPosition = true;
                continue;
            }

            if (line.isEmpty() && isListCommands) {
                isStartPosition = true;
                continue;
            }

            if (!isStartPosition) {
                continue;
            }

            if (line.isEmpty()) {
                break;
            }
            commands.add(line.trim());
        }
        return commands;
    }

    public List<String> getSubcommandListLines(List<String> subcommand) throws InvalidVagrantExecutableException {
        if (subcommand == null || subcommand.isEmpty()) {
            return Collections.emptyList();
        }
        subcommand.add(HELP_PARAM);
        setCommand(subcommand.get(0));
        subcommand.remove(0);
        final VagrantLineProcessor lineProcessor = new VagrantLineProcessor();
        descriptor = getSilentDescriptor()
                .outProcessorFactory(getOutputProcessorFactory(lineProcessor));
        additionalParameters = subcommand;
        Future<Integer> result = ExecutionService.newService(getProcessBuilder(), descriptor, "").run(); // NOI18N
        getResult(result);

        return getCommands(lineProcessor.getList());
    }

    /**
     * Get help for command.
     *
     * @param command
     * @return help
     * @throws InvalidVagrantExecutableException
     */
    public String getHelp(String command) throws InvalidVagrantExecutableException {
        noInfo(true);
        String[] commands = command.trim().split(" "); // NOI18N
        final VagrantLineProcessor lineProcessor = new VagrantLineProcessor();
        boolean isFirst = true;
        for (String c : commands) {
            if (isFirst) {
                setCommand(c);
                isFirst = false;
                continue;
            }
            addParam(c);
        }

        addParam(HELP_PARAM);
        descriptor = getSilentDescriptor()
                .outProcessorFactory(getOutputProcessorFactory(lineProcessor));
        Future<Integer> result = ExecutionService.newService(getProcessBuilder(), descriptor, "").run(); // NOI18N
        getResult(result);

        return lineProcessor.getText();
    }

    /**
     * Get statuses of VMs.
     *
     * @param project
     * @return statuses
     */
    public List<String> getStatuses(Project project) {
        List<String> statuses = new ArrayList<String>();
        if (project == null) {
            return statuses;
        }
        setWorkDir(project);

        VagrantLineProcessor lineProcessor = new VagrantLineProcessor();
        setCommand(STATUS_COMMAND);
        descriptor = getSilentDescriptor()
                .outProcessorFactory(getOutputProcessorFactory(lineProcessor));
        Future<Integer> result = ExecutionService.newService(getProcessBuilder(), descriptor, "").run(); // NOI18N

        try {
            getResult(result);
        } catch (InvalidVagrantExecutableException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }

        boolean isStart = false;
        for (String line : lineProcessor.getList()) {
            if (line.isEmpty()) {
                if (isStart) {
                    break;
                }
                isStart = true;
                continue;
            }

            if (isStart) {
                String status = getStatus(line);
                statuses.add(status);
            }
        }

        return statuses;
    }

    private void setWorkDir(Project project) {
        // set working directory
        String vagrantPath = VagrantPreferences.getVagrantPath(project);
        if (StringUtils.isEmpty(vagrantPath)) {
            workDir(FileUtil.toFile(project.getProjectDirectory()));
        } else {
            File vagrantRoot = new File(vagrantPath);
            if (vagrantRoot.exists()) {
                workDir(vagrantRoot);
            } else {
                VagrantPreferences.setVagrantPath(project, ""); // NOI18N
                LOGGER.log(Level.WARNING, "Vagrant root path is invalid. clear the path settings.");
                workDir(FileUtil.toFile(project.getProjectDirectory()));
            }
        }
    }

    /**
     * Get formatted status. (e.g. default: not created)
     *
     * @param status
     * @return formatted status
     */
    private String getStatus(String line) {
        String status = line.replaceAll(" +", " "); // NOI18N
        String[] split = status.split(" "); // NOI18N
        if (split.length >= 2) {
            split[0] = String.format("%s:", split[0]); // NOI18N
            StringBuilder sb = new StringBuilder();
            for (String string : split) {
                if (string.matches("^\\(.+\\)$")) { // NOI18N
                    break;
                }
                sb.append(" ").append(string); // NOI18N
            }
            return sb.toString();
        }
        return status;
    }

    @CheckForNull
    public SshInfo getSshInfo(Project project) throws InvalidVagrantExecutableException {
        if (project == null) {
            return null;
        }
        setWorkDir(project);
        VagrantLineProcessor lineProcessor = new VagrantLineProcessor();
        setCommand(SSH_CONFIG_COMMAND);
        descriptor = getSilentDescriptor()
                .outProcessorFactory(getOutputProcessorFactory(lineProcessor));
        Future<Integer> result = ExecutionService.newService(getProcessBuilder(), descriptor, "").run(); // NOI18N
        getResult(result);
        String hostName = ""; // NOI18N
        String user = ""; // NOI18N
        int port = -1;
        for (String line : lineProcessor.getList()) {
            line = line.trim().replace(" +", " "); // NOI18N
            if (line.startsWith("HostName ")) { // NOI18N
                hostName = line.replace("HostName ", ""); // NOI18N
            }

            if (line.startsWith("User ")) { // NOI18N
                user = line.replace("User ", ""); // NOI18N
            }

            if (line.startsWith("Port ")) { // NOI18N
                port = Integer.parseInt(line.replace("Port ", "")); // NOI18N
            }
        }
        if (!StringUtils.isEmpty(hostName) && !StringUtils.isEmpty(user) && port != -1) {
            return new SshInfo(hostName, user, port);
        }
        return null;
    }

    /**
     * Set command.
     *
     * @param command
     */
    private void setCommand(String command) {
        this.command = command;
    }

    /**
     * Add parameter.
     *
     * @param param
     */
    private void addParam(String param) {
        additionalParameters.add(param);
    }

    /**
     * Get run result.
     *
     * @param result
     * @throws InvalidVagrantExecutableException
     */
    private void getResult(Future<Integer> result) throws InvalidVagrantExecutableException {
        try {
            if (result != null) {
                result.get();
            }
        } catch (CancellationException ex) {
            // cancel
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ExecutionException ex) {
            throw new InvalidVagrantExecutableException("Could not run command.");
        }
    }

    /**
     * Check whether vagrant command is available.
     *
     * @return
     */
    public static boolean isAvailable() {
        String vagrantPath = VagrantOptions.getInstance().getVagrantPath();
        return isVagrantScript(vagrantPath, false);
    }

    /**
     * Check the path whether it is Vagrant.
     *
     * @param path absolute Vagrant path
     * @return true if it's Vagrant, false otherwise
     */
    public static boolean isVagrantScript(String path, boolean warn) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        Vagrant vagrant = new Vagrant(path)
                .noInfo(true);
        String version;
        try {
            version = vagrant.getVersionNumber();
        } catch (InvalidVagrantExecutableException ex) {
            if (warn) {
                VagrantUtils.showWarnigDialog(ex.getMessage());
            }
            return false;
        }
        return !StringUtils.isEmpty(version) && version.toLowerCase().contains("vagrant");
    }

    /**
     * Run command.
     *
     * @param project
     * @param command
     * @param title
     * @return
     */
    public Future<Integer> runCommand(Project project, String command, String title) {
        return runCommand(project, command, title, Collections.<String>emptyList());
    }

    /**
     * Run command.
     *
     * @param project
     * @param command
     * @param title
     * @param params
     * @return
     */
    @NbBundle.Messages("Vagrant.vagrant.root.invalid=Vagrant root is invalid. Please check Vagrant root settings.")
    public Future<Integer> runCommand(Project project, String command, String title, List<String> params) {
        if (project != null && workDir == null) {
            FileObject workingDirectory = project.getProjectDirectory();
            String vagrantPath = VagrantPreferences.getVagrantPath(project);
            if (workingDirectory != null) {
                // check only custom path
                boolean hasVagrantfile = true;
                if (!StringUtils.isEmpty(vagrantPath)) {
                    File vagrantRoot = new File(vagrantPath);
                    workingDirectory = FileUtil.toFileObject(vagrantRoot);
                    hasVagrantfile = VagrantUtils.hasVagrantfile(workingDirectory);
                }

                // vagrant root is invalid : show dialog
                if (workingDirectory == null || !hasVagrantfile) {
                    NotifyDescriptor.Message message = new NotifyDescriptor.Message(Bundle.Vagrant_vagrant_root_invalid(), NotifyDescriptor.WARNING_MESSAGE);
                    DialogDisplayer.getDefault().notify(message);
                    return null;
                }
                workDir = FileUtil.toFile(workingDirectory);
            }
        }
        List<String> commands = StringUtils.explode(command, " "); // NOI18N
        List<String> parameters = new ArrayList<String>();
        boolean isFirst = true;
        for (String c : commands) {
            if (isFirst) {
                setCommand(c);
                isFirst = false;
                continue;
            }
            parameters.add(c);
        }
        if (params != null) {
            parameters.addAll(params);
        }
        this.title = title;
        additionalParameters(parameters);

        return run(getExecutionDescriptor(project));
    }

    /**
     * Run command.
     *
     * @param descriptor
     * @return
     */
    private Future<Integer> run(ExecutionDescriptor descriptor) {
        Callable<Process> processBuilder = getProcessBuilder();
        if (processBuilder == null) {
            return null;
        }
        return ExecutionService.newService(processBuilder, descriptor, title).run();
    }

    private ExecutionDescriptor getExecutionDescriptor(Project project) {
        if (descriptor == null) {
            return DEFAULT_EXECUTION_DESCRIPTOR
                    .outProcessorFactory(getInfoProcessorFactory())
                    .postExecution(new RunnableImpl(project))
                    .preExecution(new RunnableImpl());
        }
        return descriptor;
    }

    /**
     * Get silent descriptor. Doesn't display the output.
     *
     * @return descriptor
     */
    private ExecutionDescriptor getSilentDescriptor() {
        return new ExecutionDescriptor()
                .inputOutput(InputOutput.NULL);
    }

    /**
     * Get ProcessBuilder.
     *
     * @return
     */
    private Callable<Process> getProcessBuilder() {
        // up command
        // XXX up command doesn't work fine with ExternalExecution API (ExeternalProcessBuilder)
        // VM status will be "aborted".
        // Use ProcessBuilder class(see: ProcessLounch class) as a workaround.
        if (UP_COMMAND.equals(command) || RESUME_COMMAND.equals(command)) {
            ArrayList<String> allParams = new ArrayList<String>();
            allParams.add(path);
            allParams.add(command);
            allParams.addAll(additionalParameters);
            fullCommand.addAll(allParams);
            ProcessLaunch processLaunch = new ProcessLaunch(allParams);
            if (workDir != null) {
                processLaunch.workingDirectory(workDir);
            }
            return processLaunch;
        }

        // other commands
        ExternalProcessBuilder processBuilder = createProcessBuilder();
        if (processBuilder == null) {
            return null;
        }
        processBuilder = processBuilder.addArgument(command);
        fullCommand.add(command);

        for (String param : additionalParameters) {
            processBuilder = processBuilder.addArgument(param);
        }
        fullCommand.addAll(additionalParameters);

        if (workDir != null) {
            processBuilder = processBuilder.workingDirectory(workDir);
        }
        return processBuilder;
    }

    /**
     * Create ProcessBuilder.
     *
     * @return ExternalProcessBuilder
     */
    private ExternalProcessBuilder createProcessBuilder() {
        fullCommand.add(path);
        return new ExternalProcessBuilder(path);
    }

    /**
     * Get InputProcessorFactory for output.
     *
     * @param lineProcessor
     * @return InputProcessorFactory
     */
    private ExecutionDescriptor.InputProcessorFactory getOutputProcessorFactory(final LineProcessor lineProcessor) {
        return new ExecutionDescriptor.InputProcessorFactory() {
            @Override
            public InputProcessor newInputProcessor(InputProcessor defaultProcessor) {
                return InputProcessors.ansiStripping(InputProcessors.bridge(lineProcessor));
            }
        };
    }

    /**
     * Get InputProcessFactory for infomation.
     *
     * @return InputProcessFactory
     */
    private ExecutionDescriptor.InputProcessorFactory getInfoProcessorFactory() {
        if (noInfo) {
            return null;
        }
        return new ExecutionDescriptor.InputProcessorFactory() {
            @Override
            public InputProcessor newInputProcessor(InputProcessor defaultProcessor) {
                if (SHARE_COMMAND.equals(command)) {
                    return InputProcessors.proxy(
                            new InfoInputProcessor(defaultProcessor, fullCommand),
                            defaultProcessor,
                            InputProcessors.ansiStripping(InputProcessors.bridge(new VagrantShareLineProcessor())));
                }
                return InputProcessors.proxy(new InfoInputProcessor(defaultProcessor, fullCommand), defaultProcessor);
            }
        };
    }

    /**
     * Notify change when run command.
     */
    void fireChange() {
        if (command == null) {
            return;
        }
        if (command.equals(UP_COMMAND)
                || command.equals(INIT_COMMAND)
                || command.equals(RELOAD_COMMAND)
                || command.equals(HALT_COMMAND)
                || command.equals(SUSPEND_COMMAND)
                || command.equals(RESUME_COMMAND)
                || command.equals(DESTROY_COMMAND)) {
            changeSupport.fireChange();
        }
    }

    //~ Inner classes
    private static class VagrantLineProcessor implements LineProcessor {

        private final ArrayList<String> list = new ArrayList<String>();

        @Override
        public void processLine(String line) {
            list.add(line);
        }

        @Override
        public void reset() {
        }

        @Override
        public void close() {
        }

        public List<String> getList() {
            return list;
        }

        public String getText() {
            StringBuilder sb = new StringBuilder();
            for (String string : list) {
                sb.append(string).append("\n"); // NOI18N
            }
            return sb.toString().trim();
        }
    }

    private static class VagrantShareLineProcessor implements LineProcessor {

        private static final Pattern URL_PATTERN = Pattern.compile("\\A.+(?<http>http://.+vagrantshare.com).*\\z"); // NOI18N

        @Override
        public void processLine(String line) {
            Matcher matcher = URL_PATTERN.matcher(line);
            if (matcher.find()) {
                String vagrantshareUrl = matcher.group("http"); // NOI18N
                if (vagrantshareUrl == null) {
                    return;
                }
                try {
                    URL url = new URL(vagrantshareUrl);
                    VagrantOptions options = VagrantOptions.getInstance();
                    if (options.isShareOpenUrl()) {
                        HtmlBrowser.URLDisplayer.getDefault().showURL(url);
                    }

                    if (options.isShareCopyUrl()) {
                        Toolkit toolkit = Toolkit.getDefaultToolkit();
                        Clipboard clipboard = toolkit.getSystemClipboard();
                        clipboard.setContents(new StringSelection(vagrantshareUrl), null);
                    }
                } catch (MalformedURLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        @Override
        public void reset() {
        }

        @Override
        public void close() {
        }

    }

    /**
     * From org.netbeans.modules.php.api.executable
     */
    private static class InfoInputProcessor implements InputProcessor {

        private final InputProcessor defaultProcessor;
        private char lastChar;

        public InfoInputProcessor(InputProcessor defaultProcessor, List<String> fullCommand) {
            this.defaultProcessor = defaultProcessor;
            try {
                defaultProcessor.processInput(getFullCommand(fullCommand).toCharArray());
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }

        @Override
        public void processInput(char[] chars) throws IOException {
            if (chars.length > 0) {
                lastChar = chars[chars.length - 1];
            }
        }

        @Override
        public void reset() throws IOException {
            // noop
        }

        @NbBundle.Messages("InfoInputProcessor.done=Done.")
        @Override
        public void close() throws IOException {
            StringBuilder msg = new StringBuilder(Bundle.InfoInputProcessor_done().length() + 2);
            if (!isNewLine(lastChar)) {
                msg.append("\n"); // NOI18N
            }
            msg.append(colorize(Bundle.InfoInputProcessor_done()));
            msg.append("\n"); // NOI18N
            defaultProcessor.processInput(msg.toString().toCharArray());
        }

        private String getFullCommand(List<String> fullCommand) {
            return colorize(StringUtils.implode(fullCommand, " ")) + "\n"; // NOI18N
        }

        private String colorize(String msg) {
            return "\033[1;30m" + msg + "\033[0m"; // NOI18N
        }

        private boolean isNewLine(char ch) {
            return ch == '\n' || ch == '\r' || ch == '\u0000'; // NOI18N
        }
    }

    private class RunnableImpl implements Runnable {

        private Project project;

        public RunnableImpl() {
        }

        public RunnableImpl(Project project) {
            this.project = project;
        }

        @Override
        public void run() {
            fireChange();
            if (project != null) {
                VagrantStatus status = Lookup.getDefault().lookup(VagrantStatus.class);
                if (status != null) {
                    Project p = project;
                    status.update(p);
                }
            }
            project = null;
        }
    }

    private static class ProcessLaunch implements Callable<Process> {

        private final List<String> commands;
        private File workDir;

        public ProcessLaunch(List<String> commands) {
            this.commands = commands;
        }

        public ProcessLaunch workingDirectory(File workDir) {
            this.workDir = workDir;
            return this;
        }

        @Override
        public Process call() throws Exception {
            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            if (workDir != null) {
                processBuilder.directory(workDir);
            }
            processBuilder.redirectErrorStream(true);
            return processBuilder.start();
        }
    }

}
