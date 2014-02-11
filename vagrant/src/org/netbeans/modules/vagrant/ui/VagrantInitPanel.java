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
package org.netbeans.modules.vagrant.ui;

import java.awt.Dialog;
import java.io.File;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.ui.options.GeneralPanel;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class VagrantInitPanel extends JPanel {

    private static final long serialVersionUID = 8375589254881469226L;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private DialogDescriptor dialogDescriptor;
    private static final String VAGRANT_LAST_FOLDER_SUFFIX = ".vagrant-root"; // NOI18N

    /**
     * Creates new form VagrantInitPanel
     */
    public VagrantInitPanel() {
        initComponents();
        init();
    }

    @NbBundle.Messages("VagrantInitPanel.vagrant.root.path=Default(empty) is project directory.")
    private void init() {
        try {
            // get available boxes
            Vagrant vagrant = Vagrant.getDefault();
            List<String> boxList = vagrant.getBoxList();
            for (String box : boxList) {
                String[] boxNameSplit = VagrantUtils.boxNameSplit(box);
                if (boxNameSplit != null) {
                    boxNameComboBox.addItem(boxNameSplit[0]);
                }
            }
        } catch (InvalidVagrantExecutableException ex) {
        }
        vagrantRootTextField.getDocument().addDocumentListener(new DefaultDocumentListener());
        errorLabel.setText(Bundle.VagrantInitPanel_vagrant_root_path());
    }

    public String getBoxName() {
        return (String) boxNameComboBox.getSelectedItem();
    }

    @NbBundle.Messages({
        "VagrantInitPanel.dialog.title=Select box",
        "VagrantInitPanel.dialog.error=Please add boxes."
    })
    public DialogDescriptor showDialog() throws Exception {
        int itemCount = boxNameComboBox.getItemCount();
        if (itemCount == 0) {
            throw new Exception(Bundle.VagrantInitPanel_dialog_error());
        }
        this.dialogDescriptor = new DialogDescriptor(this, Bundle.VagrantInitPanel_dialog_title());
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        dialog.pack();
        dialog.setVisible(true);
        return dialogDescriptor;
    }

    public void setOKButtonEnabled(boolean isEnabled) {
        if (dialogDescriptor == null) {
            return;
        }
        dialogDescriptor.setValid(isEnabled);
    }

    public String getVagrantRoot() {
        return vagrantRootTextField.getText().trim();
    }

    public void setVagrantRoot(String path) {
        vagrantRootTextField.setText(path);
    }

    public void setError(String message) {
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        errorLabel.setText(message);
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    private void fireChange() {
        changeSupport.fireChange();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        boxNameLabel = new javax.swing.JLabel();
        boxNameComboBox = new javax.swing.JComboBox<String>();
        vagrantRootLabel = new javax.swing.JLabel();
        vagrantRootTextField = new javax.swing.JTextField();
        errorLabel = new javax.swing.JLabel();
        browseButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(boxNameLabel, org.openide.util.NbBundle.getMessage(VagrantInitPanel.class, "VagrantInitPanel.boxNameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(vagrantRootLabel, org.openide.util.NbBundle.getMessage(VagrantInitPanel.class, "VagrantInitPanel.vagrantRootLabel.text")); // NOI18N

        vagrantRootTextField.setText(org.openide.util.NbBundle.getMessage(VagrantInitPanel.class, "VagrantInitPanel.vagrantRootTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(VagrantInitPanel.class, "VagrantInitPanel.errorLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(VagrantInitPanel.class, "VagrantInitPanel.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vagrantRootLabel)
                            .addComponent(boxNameLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(boxNameComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 229, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(vagrantRootTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(browseButton))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(errorLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boxNameLabel)
                    .addComponent(boxNameComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vagrantRootLabel)
                    .addComponent(vagrantRootTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages("VagrantInitPanel.browse_title=Select Vagrant Root")
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        File vagrant = new FileChooserBuilder(GeneralPanel.class.getName() + VAGRANT_LAST_FOLDER_SUFFIX)
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
    private javax.swing.JComboBox<String> boxNameComboBox;
    private javax.swing.JLabel boxNameLabel;
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JLabel vagrantRootLabel;
    private javax.swing.JTextField vagrantRootTextField;
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
