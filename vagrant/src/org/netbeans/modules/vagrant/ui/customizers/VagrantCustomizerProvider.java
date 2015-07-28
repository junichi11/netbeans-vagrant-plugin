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
package org.netbeans.modules.vagrant.ui.customizers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.Lookup;

/**
 *
 * @author junichi11
 */
public class VagrantCustomizerProvider implements ProjectCustomizer.CompositeCategoryProvider, ChangeListener {

    private VagrantCustomizerPanel panel;
    private Lookup context;
    private Category category;
    private Project project;
    private static final String CATEGORY_NAME = "Vagrant"; // NOI18N
    private static final Logger LOGGER = Logger.getLogger(VagrantCustomizerProvider.class.getName());

    @ProjectCustomizer.CompositeCategoryProvider.Registrations({
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-j2ee-clientproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-j2ee-earproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-j2ee-ejbjarproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-java-j2seproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-php-project", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-web-project", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org.netbeans.modules.web.clientproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-web-clientproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-ruby-rubyproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "com-tropyx-nb_puppet", position = 5000),})
    public static VagrantCustomizerProvider createVagrant() {
        return new VagrantCustomizerProvider();
    }

    private VagrantCustomizerProvider() {
    }

    @Override
    public ProjectCustomizer.Category createCategory(Lookup context) {
        return Category.create(CATEGORY_NAME, CATEGORY_NAME, VagrantUtils.getIcon(VagrantUtils.VAGRANT_ICON_16).getImage());
    }

    @Override
    public JComponent createComponent(ProjectCustomizer.Category category, Lookup context) {
        this.category = category;
        this.context = context;
        // add listener
        category.setOkButtonListener(new OkButtonActionListener());
        return getPanel();
    }

    private void validate() {
        category.setErrorMessage(getPanel().getErrorMessage());
        category.setValid(getPanel().valid());
    }

    private VagrantCustomizerPanel getPanel() {
        assert context != null;
        Project currentProject = context.lookup(Project.class);
        if (panel == null || project != currentProject) {
            project = currentProject;
            panel = new VagrantCustomizerPanel(project);
            panel.addChangeListener(this);
            panel.load();
        }
        return panel;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        validate();
    }

    //~ Inner class
    private class OkButtonActionListener implements ActionListener {

        public OkButtonActionListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!category.isValid()) {
                return;
            }
            getPanel().save();
        }
    }
}
