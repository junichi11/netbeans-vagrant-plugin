/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
 */
package org.netbeans.modules.vagrant.api;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.vagrant.VagrantStatus;
import org.netbeans.modules.vagrant.VagrantStatusImpl;
import static org.netbeans.modules.vagrant.command.Vagrant.HALT_COMMAND;
import org.netbeans.modules.vagrant.preferences.VagrantPreferences;
import org.netbeans.modules.vagrant.utils.StringUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

public class VagrantProjectImpl implements VagrantProject {

    private final Project project;
    private static final Logger LOGGER = Logger.getLogger(VagrantProjectImpl.class.getName());

    public static VagrantProject create(Project project) {
        return new VagrantProjectImpl(project);
    }

    private VagrantProjectImpl(Project project) {
        this.project = project;
    }

    @Override
    public String getDisplayName() {
        FileObject projectDirectory = project.getProjectDirectory();
        if (projectDirectory != null) {
            return projectDirectory.getName();
        }
        return ""; // NOI18N
    }

    @Override
    public FileObject getVagrantRoot() {
        String vagrantRootPath = getVagrantRootPath();
        if (StringUtils.isEmpty(vagrantRootPath)) {
            return null;
        }
        File file = new File(vagrantRootPath);
        if (!file.exists()) {
            return null;
        }
        return FileUtil.toFileObject(file);
    }

    @Override
    public String getVagrantRootPath() {
        return VagrantPreferences.getVagrantAbsolutePath(project);
    }

    @Override
    public File getWorkingDirecotry() {
        String vagrantPath = VagrantPreferences.getVagrantAbsolutePath(project);
        if (StringUtils.isEmpty(vagrantPath)) {
            return FileUtil.toFile(project.getProjectDirectory());
        } else {
            File vagrantRoot = new File(vagrantPath);
            if (vagrantRoot.exists()) {
                return vagrantRoot;
            } else {
                VagrantPreferences.setVagrantPath(project, ""); // NOI18N
                LOGGER.log(Level.WARNING, "Vagrant root path is invalid. clear the path settings.");
                return FileUtil.toFile(project.getProjectDirectory());
            }
        }
    }

    @Override
    public VagrantStatus getVagrantStatus() {
        return Lookup.getDefault().lookup(VagrantStatusImpl.class);
    }

    @Override
    public boolean canUpdateStatus(String command) {
        if (command.equals(HALT_COMMAND)) {
            OpenProjects projects = OpenProjects.getDefault();
            return projects.isProjectOpen(project);
        }
        return true;
    }

    @Override
    public void updateStatus() {
        VagrantStatus vagrantStatus = getVagrantStatus();
        if (vagrantStatus != null) {
            vagrantStatus.update(project);
        }
    }

    @Override
    public String toString() {
        return "VagrantProjectImpl{" + "vagrantFilePath=" + getVagrantRootPath() + ", displayName=" + getDisplayName() + '}';
    }

}
