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
package org.netbeans.modules.vagrant.preferences;

import com.google.gson.Gson;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.vagrant.command.RunCommandHistory;
import org.netbeans.modules.vagrant.ui.project.ProjectClosedAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author junichi11
 */
public final class VagrantPreferences {

    private static final String VAGRANT_PATH = "vagrant-path"; // NOI18N
    private static final String PROJECT_CLOSED_ACTION = "project-closed-action"; // NOI18N
    private static final String SAVE_RUN_COMMAND_HISTORIES = "save-run-command-histories"; // NOI18N
    private static final String RUN_COMMAND_HISTORY = "run-command-history"; // NOI18N

    private VagrantPreferences() {
    }

    public static String getVagrantAbsolutePath(Project project) {
        FileObject projectDirectory = project.getProjectDirectory();
        String vagrantPath = getVagrantPath(project);
        if (vagrantPath == null) {
            return null;
        }

        File file = new File(vagrantPath);
        if (file.isAbsolute()) {
            return vagrantPath;
        }

        if (projectDirectory != null) {
            FileObject fileObject = projectDirectory.getFileObject(vagrantPath);
            if (fileObject != null) {
                file = FileUtil.toFile(fileObject);
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    public static String getVagrantPath(Project project) {
        return getPreferences(project).get(VAGRANT_PATH, null);
    }

    public static void setVagrantPath(Project project, String path) {
        getPreferences(project).put(VAGRANT_PATH, path);
    }

    public static ProjectClosedAction getProjectClosedAction(Project project) {
        String actionName = getPreferences(project).get(PROJECT_CLOSED_ACTION, ProjectClosedAction.NONE.toString()); // NOI18N
        ProjectClosedAction action = ProjectClosedAction.toEnum(actionName);
        if (action == null) {
            return ProjectClosedAction.NONE;
        }
        return action;
    }

    public static void setProjectClosedAction(Project project, ProjectClosedAction action) {
        getPreferences(project).put(PROJECT_CLOSED_ACTION, action.toString());
    }

    public static boolean isSaveRunCommandHistoriesOnClose(Project project) {
        return getPreferences(project).getBoolean(SAVE_RUN_COMMAND_HISTORIES, false);
    }

    public static void setSaveRunCommandHistoriesOnClose(Project project, boolean isSave) {
        getPreferences(project).putBoolean(SAVE_RUN_COMMAND_HISTORIES, isSave);
    }

    public static RunCommandHistory getRunCommandHistory(Project project) {
        String json = getPreferences(project, false).get(RUN_COMMAND_HISTORY, null);
        if (json == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(json, RunCommandHistory.class);
    }

    public static void setRunCommandHistory(Project project, RunCommandHistory history, boolean isFlush) {
        if (history == null) {
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(history, RunCommandHistory.class);
        getPreferences(project, false).put(RUN_COMMAND_HISTORY, json);
        if (isFlush) {
            // NB may be closed before save history
            // Is there better way?
            try {
                flush(project, false);
            } catch (BackingStoreException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private static void flush(Project project, boolean isShared) throws BackingStoreException {
        getPreferences(project, isShared).flush();
    }

    private static Preferences getPreferences(Project project) {
        return getPreferences(project, true);
    }

    private static Preferences getPreferences(Project project, boolean isShared) {
        return ProjectUtils.getPreferences(project, VagrantPreferences.class, isShared);
    }
}
