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
package org.netbeans.modules.vagrant.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.vagrant.api.VagrantProjectGlobal;
import org.netbeans.modules.vagrant.preferences.VagrantPreferences;
import org.netbeans.modules.vagrant.ui.options.GeneralPanel;
import org.netbeans.modules.vagrant.utils.UiUtils;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class AddVagrantProjcetPanel extends javax.swing.JPanel {

    private final ChangeSupport changeSuport = new ChangeSupport(this);
    private final Collection<? extends VagrantProjectGlobal> allProjects = VagrantPreferences.getAllProjects();

    /**
     * Creates new form AddVagrantProjcetPanel
     */
    public AddVagrantProjcetPanel() {
        initComponents();
        init();
    }

    private void init() {
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                processUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                processUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                processUpdate();
            }

            private void processUpdate() {
                fireChange();
            }
        };
        displayNameTextField.getDocument().addDocumentListener(documentListener);
        vagrantRootTextField.getDocument().addDocumentListener(documentListener);
        setErrorMessage(" "); // NOI18N
        fireChange();
    }

    public void addChangeListener(ChangeListener changeListener) {
        changeSuport.addChangeListener(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        changeSuport.removeChangeListener(changeListener);
    }

    private void fireChange() {
        try {
            if (!validateDisplayName()) {
                return;
            }
            if (!validateVagrantRoot()) {
                return;
            }
            setErrorMessage("");
        } finally {
            changeSuport.fireChange();
        }
    }

    private void setErrorMessage(String errorMessage) {
        errorLabel.setText(errorMessage);
    }

    @NbBundle.Messages("AddVagrantProjcetPanel.validate.displayName.empty=Display name is empty.")
    private boolean validateDisplayName() {
        String displayName = getDisplayName();
        if (displayName == null || displayName.isEmpty()) {
            setErrorMessage(Bundle.AddVagrantProjcetPanel_validate_displayName_empty());
            return false;
        }
        return true;
    }

    @NbBundle.Messages({
        "AddVagrantProjcetPanel.validate.vagrantRoot.empty=Vagrant Root is empty.",
        "AddVagrantProjcetPanel.validate.vagrantRoot.notFound=The file doesn't exist.",
        "AddVagrantProjcetPanel.validate.vagrantRoot.absolutePath=Please use the absolute path.",
        "AddVagrantProjcetPanel.validate.vagrantRoot.vagrantfile=There is no Vagrantfile.",
        "AddVagrantProjcetPanel.validate.vagrantRoot.existingVagrantRoot=The vagrant root already exists."
    })
    private boolean validateVagrantRoot() {
        String vagrantRoot = getVagrantRoot();
        if (vagrantRoot == null || vagrantRoot.isEmpty()) {
            setErrorMessage(Bundle.AddVagrantProjcetPanel_validate_vagrantRoot_empty());
            return false;
        }
        for (VagrantProjectGlobal project : allProjects) {
            if(project.getVagrantRootPath().equals(vagrantRoot)) {
                setErrorMessage(Bundle.AddVagrantProjcetPanel_validate_vagrantRoot_existingVagrantRoot());
                return false;
            }
        }
        File file = new File(vagrantRoot);
        if (!file.exists()) {
            setErrorMessage(Bundle.AddVagrantProjcetPanel_validate_vagrantRoot_notFound());
            return false;
        }
        if (!file.isAbsolute()) {
            setErrorMessage(Bundle.AddVagrantProjcetPanel_validate_vagrantRoot_absolutePath());
            return false;
        }
        if (!VagrantUtils.hasVagrantfile(FileUtil.toFileObject(file))) {
            setErrorMessage(Bundle.AddVagrantProjcetPanel_validate_vagrantRoot_vagrantfile());
            return false;
        }
        return true;
    }

    public String getDisplayName() {
        return displayNameTextField.getText().trim();
    }

    public String getVagrantRoot() {
        return vagrantRootTextField.getText().trim();
    }

    public void setDisplayName(String displayName) {
        displayNameTextField.setText(displayName);
    }

    public void setVagrantRoot(String vagrantRoot) {
        vagrantRootTextField.setText(vagrantRoot);
    }

    public String getErrorMessage() {
        return errorLabel.getText().trim();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        displayNameLabel = new JLabel();
        displayNameTextField = new JTextField();
        vagrantRootLabel = new JLabel();
        vagrantRootTextField = new JTextField();
        errorLabel = new JLabel();
        browseButton = new JButton();

        Mnemonics.setLocalizedText(displayNameLabel, NbBundle.getMessage(AddVagrantProjcetPanel.class, "AddVagrantProjcetPanel.displayNameLabel.text")); // NOI18N

        displayNameTextField.setText(NbBundle.getMessage(AddVagrantProjcetPanel.class, "AddVagrantProjcetPanel.displayNameTextField.text")); // NOI18N

        Mnemonics.setLocalizedText(vagrantRootLabel, NbBundle.getMessage(AddVagrantProjcetPanel.class, "AddVagrantProjcetPanel.vagrantRootLabel.text")); // NOI18N

        vagrantRootTextField.setText(NbBundle.getMessage(AddVagrantProjcetPanel.class, "AddVagrantProjcetPanel.vagrantRootTextField.text")); // NOI18N

        Mnemonics.setLocalizedText(errorLabel, NbBundle.getMessage(AddVagrantProjcetPanel.class, "AddVagrantProjcetPanel.errorLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(browseButton, NbBundle.getMessage(AddVagrantProjcetPanel.class, "AddVagrantProjcetPanel.browseButton.text")); // NOI18N
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(displayNameLabel)
                            .addComponent(vagrantRootLabel))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(displayNameTextField)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(vagrantRootTextField, GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(browseButton)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(errorLabel)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(displayNameLabel)
                    .addComponent(displayNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(vagrantRootLabel)
                    .addComponent(vagrantRootTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorLabel)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        File vagrant = new FileChooserBuilder(GeneralPanel.class.getName() + UiUtils.VAGRANT_LAST_FOLDER_SUFFIX)
                .setTitle(Bundle.VagrantInitPanel_browse_title())
                .setDirectoriesOnly(true)
                .showOpenDialog();
        if (vagrant != null) {
            vagrant = FileUtil.normalizeFile(vagrant);
            String vagrantPath = vagrant.getAbsolutePath();
            vagrantRootTextField.setText(vagrantPath);
        }
    }//GEN-LAST:event_browseButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton browseButton;
    private JLabel displayNameLabel;
    private JTextField displayNameTextField;
    private JLabel errorLabel;
    private JLabel vagrantRootLabel;
    private JTextField vagrantRootTextField;
    // End of variables declaration//GEN-END:variables
}
