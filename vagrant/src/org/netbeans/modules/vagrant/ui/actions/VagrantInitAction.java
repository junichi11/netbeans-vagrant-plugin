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
package org.netbeans.modules.vagrant.ui.actions;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.vagrant.api.VagrantProjectImpl;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.preferences.VagrantPreferences;
import org.netbeans.modules.vagrant.ui.VagrantInitPanel;
import org.netbeans.modules.vagrant.utils.StringUtils;
import org.netbeans.modules.vagrant.utils.UiUtils;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Vagrant",
        id = "org.netbeans.modules.vagrant.ui.actions.VagrantInitAction")
@ActionRegistration(
        displayName = "#CTL_VagrantInitAction", lazy = false)
@Messages("CTL_VagrantInitAction=Vagrant init")
public final class VagrantInitAction extends VagrantAction implements ChangeListener {

    private static final long serialVersionUID = -7941158918859555393L;
    private VagrantInitPanel panel;
    private static final Logger LOGGER = Logger.getLogger(VagrantInitAction.class.getName());
    private Project project;

    public VagrantInitAction() {
        super(Bundle.CTL_VagrantInitAction(), VagrantUtils.getIcon(VagrantUtils.INIT_ICON_16));
    }

    @Override
    public void actionPerformed(Project project) {
        if (existsVagrant(project)) {
            return;
        }

        this.project = project;
        VagrantInitPanel initPanel = getPanel();
        initPanel.setVagrantRoot(""); // NOI18N

        String error = ""; // NOI18N
        try {
            // open panel
            DialogDescriptor descriptor = initPanel.showDialog();
            if (descriptor.getValue() != DialogDescriptor.OK_OPTION) {
                return;
            }
        } catch (Exception ex) {
            UiUtils.showOptions();
            error = ex.getMessage();
            NotifyDescriptor.Message message = new NotifyDescriptor.Message(error, NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(message);
        }

        if (!StringUtils.isEmpty(error)) {
            return;
        }

        runInit(project);
    }

    @NbBundle.Messages({
        "# {0} - working directory",
        "VagrantInitAction.run.init.info=vagrant init - {0}"
    })
    private void runInit(Project project) {
        String vagrantRootPath = getPanel().getVagrantRoot();
        try {
            Vagrant vagrant = Vagrant.getDefault();
            // set working directory
            if (!StringUtils.isEmpty(vagrantRootPath)) {
                vagrant.workDir(new File(vagrantRootPath));
            }
            // init
            vagrant.init(VagrantProjectImpl.create(project), getBoxName(), ""); // NOI18N
        } catch (InvalidVagrantExecutableException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
        LOGGER.log(Level.INFO, Bundle.VagrantInitAction_run_init_info(vagrantRootPath));

        setVagrantRootPath(project, vagrantRootPath);
    }

    private String getBoxName() {
        String boxName = getPanel().getBoxName();
        int indexOfWhiteSpace = boxName.indexOf(" "); // NOI18N
        if (indexOfWhiteSpace == -1) {
            return boxName;
        }
        return boxName.substring(0, indexOfWhiteSpace);
    }

    private boolean existsVagrant(Project project) {
        String vagrantPath = VagrantPreferences.getVagrantAbsolutePath(project);
        if (!StringUtils.isEmpty(vagrantPath)) {
            showWarningDialog();
            return true;
        } else {
            if (VagrantUtils.hasVagrantfile(project.getProjectDirectory())) {
                showWarningDialog();
                return true;
            }
        }
        return false;
    }

    @NbBundle.Messages("VagrantInitAction.vagrantfile.exists=Vagrantfile already exists or Vagrant root is already set to project properties")
    private void showWarningDialog() {
        NotifyDescriptor.Message message = new NotifyDescriptor.Message(Bundle.VagrantInitAction_vagrantfile_exists(), NotifyDescriptor.WARNING_MESSAGE);
        DialogDisplayer.getDefault().notify(message);
    }

    private void setVagrantRootPath(Project project, String vagrantRootPath) {
        if (StringUtils.isEmpty(vagrantRootPath)) {
            return;
        }
        // set path to project properties
        // XXX check Vagrantfile?
        VagrantPreferences.setVagrantPath(project, vagrantRootPath);
    }

    @NbBundle.Messages({
        "VagrantInitAction.vagrant.path.notAbsolute=The path must be absolute path.",
        "VagrantInitAction.vagrant.root.notFound=Existing path must be set.",
        "VagrantInitAction.vagrant.root.notDirectory=The path must be directory",
        "VagrantInitAction.vagrant.root.hasVagrantfile=Vagrantfile already exists"})
    private void validate() {
        String vagrantRootPath = getPanel().getVagrantRoot();
        if (StringUtils.isEmpty(vagrantRootPath)) {
            getPanel().setOKButtonEnabled(true);
            getPanel().setError(" "); // NOI18N
            return;
        }

        File vagrantRoot = new File(vagrantRootPath);
        // absolute path?
        if (!vagrantRoot.isAbsolute()) {
            getPanel().setOKButtonEnabled(false);
            getPanel().setError(Bundle.VagrantInitAction_vagrant_path_notAbsolute());
            return;
        }

        // existing path?
        if (!vagrantRoot.exists()) {
            getPanel().setOKButtonEnabled(false);
            getPanel().setError(Bundle.VagrantInitAction_vagrant_root_notFound());
            return;
        }

        // directory?
        if (!vagrantRoot.isDirectory()) {
            getPanel().setOKButtonEnabled(false);
            getPanel().setError(Bundle.VagrantInitAction_vagrant_root_notDirectory());
            return;
        }

        // has Vagrantfile?
        if (VagrantUtils.hasVagrantfile(FileUtil.toFileObject(vagrantRoot))) {
            getPanel().setOKButtonEnabled(false);
            getPanel().setError(Bundle.VagrantInitAction_vagrant_root_hasVagrantfile());
            return;
        }

        // everything ok
        getPanel().setOKButtonEnabled(true);
        getPanel().setError(" "); // NOI18N
    }

    private VagrantInitPanel getPanel() {
        if (panel == null) {
            panel = new VagrantInitPanel();
            // add listener
            panel.addChangeListener(this);
        }
        return panel;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        validate();
    }
}
