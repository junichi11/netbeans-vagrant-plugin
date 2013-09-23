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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.ui.VagrantInitPanel;
import org.netbeans.modules.vagrant.utils.StringUtils;
import org.netbeans.modules.vagrant.utils.UiUtils;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle.Messages;

@Messages("CTL_VagrantInitAction=Vagrant init")
public final class VagrantInitAction extends VagrantAction {

    private static final long serialVersionUID = -7941158918859555393L;
    private VagrantInitPanel panel;
    private static final Logger LOGGER = Logger.getLogger(VagrantInitAction.class.getName());

    public VagrantInitAction() {
        super(Bundle.CTL_VagrantInitAction(), VagrantUtils.getIcon(VagrantUtils.INIT_ICON_16));
    }

    @Override
    public void actionPerformed(Project project) {
        // open panel
        VagrantInitPanel initPanel = getPanel();
        String error = ""; // NOI18N
        try {
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

        try {
            Vagrant vagrant = Vagrant.getDefault();
            vagrant.init(project, initPanel.getBoxName(), ""); // NOI18N
        } catch (InvalidVagrantExecutableException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
    }

    private VagrantInitPanel getPanel() {
        if (panel == null) {
            panel = new VagrantInitPanel();
        }
        return panel;
    }
}
