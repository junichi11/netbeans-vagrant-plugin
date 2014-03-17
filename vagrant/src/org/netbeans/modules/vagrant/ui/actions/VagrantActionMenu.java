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

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "Tools",
        id = "org.netbeans.modules.vagrant.ui.actions.VagrantAction")
@ActionRegistration(
        menuText = "#CTL_VagrantAction",
        lazy = false,
        displayName = "#CTL_VagrantAction")
@ActionReferences({
    @ActionReference(path = "Projects/Actions", position = 2550),
    @ActionReference(path = "Menu/Tools", position = 1800),})
@Messages("CTL_VagrantAction=Vagrant")
public final class VagrantActionMenu extends AbstractAction implements Presenter.Menu, Presenter.Popup {

    private static final long serialVersionUID = -6126034701221769491L;
    public static final List<? extends VagrantAction> ALL_ACTIONS = Arrays.asList(
            new VagrantUpAction(),
            new VagrantReloadAction(),
            new VagrantSuspendAction(),
            new VagrantResumeAction(),
            new VagrantHaltAction(),
            new VagrantDestroyAction(),
            new VagrantStatusAction(),
            new VagrantInitAction(),
            new VagrantSshAction(),
            new VagrantSshConfigAction(),
            new VagrantProvisitonAction(),
            new VagrantBoxAddAction(),
            new VagrantPluginInstallAction(),
            new VagrantRunCommandAction(),
            new VagrantOpenOptionsAction());
    private JMenu menu;
    private JMenu popupMenu;

    public VagrantActionMenu() {
        super(Bundle.CTL_VagrantAction());
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // do nothing
    }

    @Override
    public JMenuItem getMenuPresenter() {
        return getMenu();
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return getPopupMenu();
    }

    private JMenu getMenu() {
        if (menu == null) {
            menu = new JMenu(Bundle.CTL_VagrantAction());
            addAllActions(menu);
        }
        menu.setIcon(VagrantUtils.getIcon(VagrantUtils.VAGRANT_ICON_16));
        return menu;
    }

    private JMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = new JMenu(Bundle.CTL_VagrantAction());
            addAllActions(popupMenu);
        }
        popupMenu.setIcon(VagrantUtils.getIcon(VagrantUtils.VAGRANT_ICON_16));
        return popupMenu;
    }

    private void addAllActions(JMenu menu) {
        menu.removeAll();
        for (VagrantAction action : ALL_ACTIONS) {
            menu.add(action);
        }
    }
}
