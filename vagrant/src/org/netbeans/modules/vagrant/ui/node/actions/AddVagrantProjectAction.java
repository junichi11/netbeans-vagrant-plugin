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
package org.netbeans.modules.vagrant.ui.node.actions;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.vagrant.api.VagrantProjectGlobal;
import org.netbeans.modules.vagrant.api.VagrantProjectRegistry;
import org.netbeans.modules.vagrant.ui.AddVagrantProjcetPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "System",
        id = "org.netbeans.modules.vagrant.ui.node.actions.AddVagrantProjectAction"
)
@ActionRegistration(
        displayName = "#CTL_AddVagrantProjectAction"
)
@ActionReferences(
        @ActionReference(path = "Vagrant/Wizard", position = 100)
)
@Messages("CTL_AddVagrantProjectAction=Add Vagrant Project...")
public final class AddVagrantProjectAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent event) {
        AddVagrantProjcetPanel panel = new AddVagrantProjcetPanel();
        DialogDescriptor dialogDescriptor = new DialogDescriptor(
                panel,
                Bundle.CTL_AddVagrantProjectAction(),
                true,
                null
        );
        dialogDescriptor.setValid(false);
        ChangeListener changeListener = (ChangeEvent e) -> {
            String errorMessage = panel.getErrorMessage();
            dialogDescriptor.setValid(errorMessage.isEmpty());
        };
        panel.addChangeListener(changeListener);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        dialog.setVisible(true);
        if (dialogDescriptor.getValue() == DialogDescriptor.OK_OPTION) {
            // add project
            String displayName = panel.getDisplayName();
            String vagrantRoot = panel.getVagrantRoot();
            VagrantProjectGlobal project = VagrantProjectGlobal.create(displayName, vagrantRoot);
            assert project != null;
            VagrantProjectRegistry.getDefault().addProject(project);
        }
        panel.removeChangeListener(changeListener);
    }
}
