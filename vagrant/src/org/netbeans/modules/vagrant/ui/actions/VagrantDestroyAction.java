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
import org.netbeans.modules.vagrant.api.VagrantProjectImpl;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.preferences.VagrantPreferences;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Vagrant",
        id = "org.netbeans.modules.vagrant.ui.actions.VagrantDestroyAction")
@ActionRegistration(
        displayName = "#CTL_VagrantDestroyAction", lazy = false)
@Messages("CTL_VagrantDestroyAction=Vagrant destroy")
public final class VagrantDestroyAction extends VagrantAction {

    private static final long serialVersionUID = 1019938024932905534L;
    private static final Logger LOGGER = Logger.getLogger(VagrantDestroyAction.class.getName());

    public VagrantDestroyAction() {
        super(Bundle.CTL_VagrantDestroyAction(), VagrantUtils.getIcon(VagrantUtils.DESTROY_ICON_16));
    }

    @NbBundle.Messages("VagrantDestroyAction.confirmation.message=Do you really want to destroy?")
    @Override
    public void actionPerformed(Project project) {
        // ** require TTY **
        // so, show comfirmation dialog in NetBeans side
        NotifyDescriptor.Confirmation confirmation = new NotifyDescriptor.Confirmation(
                Bundle.VagrantDestroyAction_confirmation_message(),
                NotifyDescriptor.OK_CANCEL_OPTION);
        if (DialogDisplayer.getDefault().notify(confirmation) != NotifyDescriptor.OK_OPTION) {
            return;
        }

        try {
            Vagrant vagrant = Vagrant.getDefault();
            vagrant.destroy(VagrantProjectImpl.create(project));
        } catch (InvalidVagrantExecutableException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }

        // clear settings
        VagrantPreferences.setVagrantPath(project, ""); // NOI18N
    }
}
