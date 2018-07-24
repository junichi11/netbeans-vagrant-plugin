/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.vagrant;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.RunCommandHistory;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.options.VagrantOptions;
import org.netbeans.modules.vagrant.preferences.VagrantPreferences;
import org.netbeans.modules.vagrant.ui.VagrantStatusLineElement;
import org.netbeans.modules.vagrant.ui.project.NetBeansClosingDialog;
import org.netbeans.modules.vagrant.ui.project.ProjectClosedAction;
import org.netbeans.modules.vagrant.utils.StringUtils;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Pair;

public final class VagrantInstaller extends ModuleInstall {

    private static final Logger LOGGER = Logger.getLogger(VagrantInstaller.class.getName());
    private static final long serialVersionUID = -6837485626635439095L;

    @Override
    public void restored() {
        Lookup lookup = Lookup.getDefault();
        VagrantStatusLineElement lineElement = lookup.lookup(VagrantStatusLineElement.class);
        VagrantStatus vagrantStatus = lookup.lookup(VagrantStatus.class);
        if (lineElement != null && vagrantStatus != null) {
            vagrantStatus.addChangeListener(lineElement);
        }
    }

    @Override
    public void close() {
        // clear status
        Lookup lookup = Lookup.getDefault();
        VagrantStatus vagrantStatus = lookup.lookup(VagrantStatus.class);
        if (vagrantStatus == null) {
            return;
        }
        VagrantStatusLineElement lineElement = lookup.lookup(VagrantStatusLineElement.class);
        if (lineElement != null) {
            vagrantStatus.removeChangeListener(lineElement);
        }
        vagrantStatus.clear();

        // save run command histories
        OpenProjects projects = OpenProjects.getDefault();
        for (Project project : projects.getOpenProjects()) {
            if (VagrantPreferences.isSaveRunCommandHistoriesOnClose(project)) {
                RunCommandHistory history = RunCommandHistory.Factory.create(project);
                VagrantPreferences.setRunCommandHistory(project, history, true);
            }
        }

    }

    /**
     * Closing event. e.g. show dialog
     *
     * @return true if shutdown app, false otherwise (i.e. doesn't close NB)
     */
    @NbBundle.Messages({
        "VagrantInstaller.closing.confirmation=Vagrant is running... If you want to shutdown NetBeans, please click OK"
    })
    @Override
    public boolean closing() {
        // run halt command
        VagrantOptions options = VagrantOptions.getInstance();
        if (StringUtils.isEmpty(options.getVagrantPath())) {
            return Status.getInstance().setShutdown();
        }

        ArrayList<Pair<Project, StatusLine>> runningProjects = new ArrayList<>();
        ArrayList<Pair<Project, StatusLine>> haltAskProjects = new ArrayList<>();
        getRunningProejcts(haltAskProjects, runningProjects);

        // halt-ask?
        if (!haltAskProjects.isEmpty()) {
            // show confirmation dialog
            NetBeansClosingDialog closingDialog = new NetBeansClosingDialog(new JFrame(), true, haltAskProjects);
            closingDialog.setVisible(true);
            if (closingDialog.getStatus() == NetBeansClosingDialog.Status.SHUTDOWN) {
                haltProjects(runningProjects);
                return Status.getInstance().setShutdown();
            }
            return false;
        }

        haltProjects(runningProjects);
        return Status.getInstance().setShutdown();
    }

    private void getRunningProejcts(List<Pair<Project, StatusLine>> haltAskProjects, List<Pair<Project, StatusLine>> runningProjects) {
        if (VagrantOptions.getInstance().isCachedStatusOnClose()) {
            VagrantStatus vagrantStatus = Lookup.getDefault().lookup(VagrantStatus.class);
            if (vagrantStatus != null) {
                for (Pair<Project, StatusLine> pair : vagrantStatus.getAll()) {
                    separateRunningProjects(pair, haltAskProjects, runningProjects);
                }
            }
        } else {
            // get opened projects
            OpenProjects projects = OpenProjects.getDefault();
            for (Project project : projects.getOpenProjects()) {
                try {
                    Vagrant vagrant = Vagrant.getDefault();
                    List<StatusLine> statusLines = vagrant.getStatusLines(project);
                    // status confirmation
                    for (StatusLine statusLine : statusLines) {
                        separateRunningProjects(Pair.of(project, statusLine), haltAskProjects, runningProjects);
                        break;
                    }
                } catch (InvalidVagrantExecutableException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage());
                }
            }
        }
    }

    private void separateRunningProjects(Pair<Project, StatusLine> status, List<Pair<Project, StatusLine>> haltAskProjects, List<Pair<Project, StatusLine>> runningProjects) {
        if (status.second().getStatus().contains("running")) { // NOI18N
            ProjectClosedAction closedAction = VagrantPreferences.getProjectClosedAction(status.first());
            if (closedAction == ProjectClosedAction.HALT_ASK) {
                haltAskProjects.add(status);
            } else if (closedAction == ProjectClosedAction.HALT) {
                runningProjects.add(status);
            } else {
                // none: noop
            }
        }
    }

    private void haltProjects(List<Pair<Project, StatusLine>> runningProjects) {
        for (Pair<Project, StatusLine> status : runningProjects) {
            try {
                Vagrant vagrant = Vagrant.getDefault();
                String name = status.second().getName();
                vagrant.halt(status.first(), name);
            } catch (InvalidVagrantExecutableException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage());
            }
        }
    }

    public static class Status {

        private static final Status INSTANCE = new Status();
        private static boolean isShutdown = false;

        private Status() {
        }

        public static Status getInstance() {
            return INSTANCE;
        }

        /**
         *
         * @return {@code true}
         */
        private synchronized boolean setShutdown() {
            isShutdown = true;
            return true;
        }

        /**
         * Check whether closing method is invoked.
         *
         * @return {@code true} if status is shutdonw, {@code false} otherwise
         */
        public synchronized boolean isShutdown() {
            return isShutdown;
        }
    }

}
