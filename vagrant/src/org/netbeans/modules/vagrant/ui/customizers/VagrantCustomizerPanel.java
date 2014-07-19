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

import java.io.File;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.vagrant.preferences.VagrantPreferences;
import org.netbeans.modules.vagrant.ui.options.GeneralPanel;
import org.netbeans.modules.vagrant.ui.project.ProjectClosedAction;
import org.netbeans.modules.vagrant.utils.StringUtils;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public final class VagrantCustomizerPanel extends JPanel {

    private static final long serialVersionUID = 2703353433319973808L;
    private final Project project;
    private String errorMessage;
    private boolean isValid;
    private final ChangeSupport changeSuport = new ChangeSupport(this);
    private static final String VAGRANT_LAST_FOLDER_SUFFIX = ".vagrant-root"; // NOI18N

    /**
     * Creates new form VagrantCustomizerPanel
     */
    public VagrantCustomizerPanel(Project project) {
        this.project = project;
        initComponents();
        init();
    }

    private void init() {
        vagrantRootPathTextField.getDocument().addDocumentListener(new DefaultDocumentListener());
    }

    @NbBundle.Messages({
        "VagrantCustomizerPanel.not.absolute=The path must be absolute path.",
        "VagrantCustomizerPanel.notFound.path=Existing path must be set.",
        "VagrantCustomizerPanel.not.directory=The path must be directory.",
        "VagrantCustomizerPanel.notFound.vagrantfile=Vagrant root must be had Vagrantfile."})
    public void validateFields() {
        isValid = false;
        String vagrantRootPath = getVagrantRootPath();
        if (StringUtils.isEmpty(vagrantRootPath)) {
            isValid = true;
            errorMessage = ""; // NOI18N
            return;
        }

        File file = new File(vagrantRootPath);
        if (!file.isAbsolute()) {
            errorMessage = Bundle.VagrantCustomizerPanel_not_absolute();
            return;
        }

        if (!file.exists()) {
            errorMessage = Bundle.VagrantCustomizerPanel_notFound_path();
            return;
        }

        if (!file.isDirectory()) {
            errorMessage = Bundle.VagrantCustomizerPanel_not_directory();
            return;
        }

        File vagrantfile = new File(file, VagrantUtils.VAGRANTFILE);
        File lowercaseVagrantfile = new File(file, VagrantUtils.VAGRANTFILE.toLowerCase());
        if (!vagrantfile.exists() && !lowercaseVagrantfile.exists()) {
            errorMessage = Bundle.VagrantCustomizerPanel_notFound_vagrantfile();
            return;
        }

        // everything ok
        isValid = true;
        errorMessage = ""; // NOI18N
    }

    public String getErrorMessage() {
        validateFields();
        return errorMessage;
    }

    public boolean valid() {
        validateFields();
        return isValid;
    }

    public void addChangeListener(ChangeListener listener) {
        changeSuport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSuport.removeChangeListener(listener);
    }

    private void fireChange() {
        changeSuport.fireChange();
    }

    public String getVagrantRootPath() {
        return vagrantRootPathTextField.getText().trim();
    }

    public ProjectClosedAction getProjectClosedAction() {
        if (projectClosedNoneRadioButton.isSelected()) {
            return ProjectClosedAction.NONE;
        }
        if (projectClosedHaltRadioButton.isSelected()) {
            return ProjectClosedAction.HALT;
        }
        if (projectClosedHaltAskRadioButton.isSelected()) {
            return ProjectClosedAction.HALT_ASK;
        }
        return ProjectClosedAction.NONE;
    }

    private void setProjectClosedAction(ProjectClosedAction action) {
        projectClosedbuttonGroup.clearSelection();
        switch (action) {
            case NONE:
                projectClosedNoneRadioButton.setSelected(true);
                break;
            case HALT:
                projectClosedHaltRadioButton.setSelected(true);
                break;
            case HALT_ASK:
                projectClosedHaltAskRadioButton.setSelected(true);
                break;
            default:
                projectClosedNoneRadioButton.setSelected(true);
                break;
        }
    }

    private boolean isSaveRunCommandHistories() {
        return saveRunCommandHistoriesCheckBox.isSelected();
    }

    public void load() {
        vagrantRootPathTextField.setText(VagrantPreferences.getVagrantPath(project));
        setProjectClosedAction(VagrantPreferences.getProjectClosedAction(project));
        saveRunCommandHistoriesCheckBox.setSelected(VagrantPreferences.isSaveRunCommandHistoriesOnClose(project));
    }

    public void save() {
        VagrantPreferences.setVagrantPath(project, getVagrantRootPath());
        VagrantPreferences.setProjectClosedAction(project, getProjectClosedAction());
        VagrantPreferences.setSaveRunCommandHistoriesOnClose(project, isSaveRunCommandHistories());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectClosedbuttonGroup = new javax.swing.ButtonGroup();
        vagrantRootPathLabel = new javax.swing.JLabel();
        vagrantRootPathTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        projectClosedLabel = new javax.swing.JLabel();
        projectClosedNoneRadioButton = new javax.swing.JRadioButton();
        projectClosedHaltRadioButton = new javax.swing.JRadioButton();
        projectClosedHaltAskRadioButton = new javax.swing.JRadioButton();
        saveRunCommandHistoriesCheckBox = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(vagrantRootPathLabel, org.openide.util.NbBundle.getMessage(VagrantCustomizerPanel.class, "VagrantCustomizerPanel.vagrantRootPathLabel.text")); // NOI18N

        vagrantRootPathTextField.setText(org.openide.util.NbBundle.getMessage(VagrantCustomizerPanel.class, "VagrantCustomizerPanel.vagrantRootPathTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(VagrantCustomizerPanel.class, "VagrantCustomizerPanel.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(projectClosedLabel, org.openide.util.NbBundle.getMessage(VagrantCustomizerPanel.class, "VagrantCustomizerPanel.projectClosedLabel.text")); // NOI18N

        projectClosedbuttonGroup.add(projectClosedNoneRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(projectClosedNoneRadioButton, org.openide.util.NbBundle.getMessage(VagrantCustomizerPanel.class, "VagrantCustomizerPanel.projectClosedNoneRadioButton.text")); // NOI18N

        projectClosedbuttonGroup.add(projectClosedHaltRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(projectClosedHaltRadioButton, org.openide.util.NbBundle.getMessage(VagrantCustomizerPanel.class, "VagrantCustomizerPanel.projectClosedHaltRadioButton.text")); // NOI18N

        projectClosedbuttonGroup.add(projectClosedHaltAskRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(projectClosedHaltAskRadioButton, org.openide.util.NbBundle.getMessage(VagrantCustomizerPanel.class, "VagrantCustomizerPanel.projectClosedHaltAskRadioButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(saveRunCommandHistoriesCheckBox, org.openide.util.NbBundle.getMessage(VagrantCustomizerPanel.class, "VagrantCustomizerPanel.saveRunCommandHistoriesCheckBox.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(projectClosedNoneRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(projectClosedHaltRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(projectClosedHaltAskRadioButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(saveRunCommandHistoriesCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(vagrantRootPathLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(vagrantRootPathTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(browseButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(projectClosedLabel)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vagrantRootPathLabel)
                    .addComponent(vagrantRootPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(projectClosedLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectClosedNoneRadioButton)
                    .addComponent(projectClosedHaltRadioButton)
                    .addComponent(projectClosedHaltAskRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveRunCommandHistoriesCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages("VagrantCustomizerPanel.browse_title=Select Vagrant Root")
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        File vagrant = new FileChooserBuilder(GeneralPanel.class.getName() + VAGRANT_LAST_FOLDER_SUFFIX)
                .setTitle(Bundle.VagrantCustomizerPanel_browse_title())
                .setDirectoriesOnly(true)
                .showOpenDialog();
        if (vagrant != null) {
            vagrant = FileUtil.normalizeFile(vagrant);
            String vagrantPath = vagrant.getAbsolutePath();
            vagrantRootPathTextField.setText(vagrantPath);
        }
    }//GEN-LAST:event_browseButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JRadioButton projectClosedHaltAskRadioButton;
    private javax.swing.JRadioButton projectClosedHaltRadioButton;
    private javax.swing.JLabel projectClosedLabel;
    private javax.swing.JRadioButton projectClosedNoneRadioButton;
    private javax.swing.ButtonGroup projectClosedbuttonGroup;
    private javax.swing.JCheckBox saveRunCommandHistoriesCheckBox;
    private javax.swing.JLabel vagrantRootPathLabel;
    private javax.swing.JTextField vagrantRootPathTextField;
    // End of variables declaration//GEN-END:variables

    //~ Inner class
    private class DefaultDocumentListener implements DocumentListener {

        public DefaultDocumentListener() {
        }

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
    }
}
