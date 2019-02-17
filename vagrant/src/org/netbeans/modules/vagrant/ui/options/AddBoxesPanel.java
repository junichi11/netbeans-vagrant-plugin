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
package org.netbeans.modules.vagrant.ui.options;

import java.awt.Dialog;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.vagrant.boxes.VagrantBoxItem;
import org.netbeans.modules.vagrant.boxes.VagrantBoxesSupport;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.options.VagrantOptions;
import org.netbeans.modules.vagrant.ui.StripeListCellRenderer;
import org.netbeans.modules.vagrant.utils.StringUtils;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class AddBoxesPanel extends JPanel {

    private static final long serialVersionUID = -9188797761831058597L;
    private static final int MAX_RELOAD_COUNT = 5;
    private static final AddBoxesPanel INSTANCE = new AddBoxesPanel();
    private List<VagrantBoxItem> boxes;
    private final List<VagrantBoxItem> filteredBoxes = new LinkedList<>();
    private int reloadCount = 0;

    /**
     * Creates new form AddBoxesPanel
     */
    public AddBoxesPanel() {
        initComponents();
        init();
        addDocumentListener();
    }

    private void addDocumentListener() {
        filterTextField.getDocument().addDocumentListener(new DefaultDocumentListener());
    }

    public static AddBoxesPanel getDefault(boolean isEmpty) {
        INSTANCE.setDefaultValue(isEmpty);
        return INSTANCE;
    }

    private void init() {
        setVagrantboxesLabel();
        vagrantboxesList.setCellRenderer(new StripeListCellRenderer());
        vagrantboxesList.setModel(new DefaultListModel<>());
        setVagrantboxesTable();
    }

    private void setDefaultValue(boolean isEmpty) {
        if (isEmpty) {
            nameTextField.setText("lucid32"); // NOI18N
            urlTextField.setText("http://files.vagrantup.com/lucid32.box"); // NOI18N
        } else {
            nameTextField.setText(""); // NOI18N
            urlTextField.setText(""); // NOI18N
        }
        setVagrantboxesLabel();
    }

    public String getBoxName() {
        return nameTextField.getText().trim();
    }

    public String getBoxUrl() {
        return urlTextField.getText().trim();
    }

    public String getFilter() {
        return filterTextField.getText();
    }

    @NbBundle.Messages("AddBoxesPanel.dialog.title=Add Box")
    public DialogDescriptor showDialog() {
        DialogDescriptor dialogDescriptor = new DialogDescriptor(this, Bundle.AddBoxesPanel_dialog_title());
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        dialog.pack();
        dialog.setVisible(true);
        return dialogDescriptor;
    }

    public void runVagrantBoxAdd() {
        SwingUtilities.invokeLater(() -> {
            String boxName = getBoxName();
            String boxUrl = getBoxUrl();
            if (boxName.isEmpty() || boxUrl.isEmpty()) {
                return;
            }
            try {
                Vagrant vagrant = Vagrant.getDefault();
                vagrant.box(Vagrant.BOX.ADD, Arrays.asList(boxName, boxUrl));
            } catch (InvalidVagrantExecutableException ex) {
                VagrantUtils.showWarnigDialog(ex.getMessage());
            }
        });
    }

    private void setVagrantboxesLabel() {
        vagrantboxesLabel.setText(String.format("From: %s", VagrantOptions.getInstance().getBoxesUrl())); // NOI18N
    }

    @NbBundle.Messages("AddBoxesPanel.no.available.box=There is no available box.")
    private void setVagrantboxesTable() {
        // get boxes
        boxes = VagrantBoxesSupport.getBoxes();
        if (boxes.isEmpty()) {
            filteredBoxes.clear();
            DefaultListModel<String> model = (DefaultListModel<String>) vagrantboxesList.getModel();
            model.clear();
            model.add(0, Bundle.AddBoxesPanel_no_available_box());
            return;
        }
        updateVagrantboxesTable();
    }

    private void updateVagrantboxesTable() {
        DefaultListModel<String> model = (DefaultListModel<String>) vagrantboxesList.getModel();
        model.clear();
        filteredBoxes.clear();
        String filter = getFilter();
        boolean isEmpty = StringUtils.isEmpty(filter);

        // get filters
        filter = filter.replaceAll(" +", " "); // NOI18N
        String[] filters = filter.split(" "); // NOI18N

        // add
        int i = 0;
        for (VagrantBoxItem box : boxes) {
            String name = box.getName();
            if (isEmpty || StringUtils.containsAll(name.toLowerCase(), filters)) {
                model.add(i, String.format("<html><b>%s</b>  (<i>%s</i>)  %s</html>", name, box.getProvider(), box.getSize())); // NOI18N
                filteredBoxes.add(i, box);
                i++;
            }
        }
    }

    private void changeUrl() {
        if (boxes.isEmpty() || filteredBoxes.isEmpty()) {
            return;
        }

        int selectedIndex = vagrantboxesList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        VagrantBoxItem box = filteredBoxes.get(selectedIndex);
        urlTextField.setText(box.getUrl());
    }

    private void fireChange() {
        updateVagrantboxesTable();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameLabel = new javax.swing.JLabel();
        urlLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        urlTextField = new javax.swing.JTextField();
        vagrantboxesScrollPane = new javax.swing.JScrollPane();
        vagrantboxesList = new javax.swing.JList<String>();
        vagrantboxesLabel = new javax.swing.JLabel();
        reloadButton = new javax.swing.JButton();
        filterLabel = new javax.swing.JLabel();
        filterTextField = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(AddBoxesPanel.class, "AddBoxesPanel.nameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(urlLabel, org.openide.util.NbBundle.getMessage(AddBoxesPanel.class, "AddBoxesPanel.urlLabel.text")); // NOI18N

        nameTextField.setText(org.openide.util.NbBundle.getMessage(AddBoxesPanel.class, "AddBoxesPanel.nameTextField.text")); // NOI18N

        urlTextField.setText(org.openide.util.NbBundle.getMessage(AddBoxesPanel.class, "AddBoxesPanel.urlTextField.text")); // NOI18N

        vagrantboxesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        vagrantboxesList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                vagrantboxesListMouseClicked(evt);
            }
        });
        vagrantboxesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                vagrantboxesListValueChanged(evt);
            }
        });
        vagrantboxesScrollPane.setViewportView(vagrantboxesList);

        org.openide.awt.Mnemonics.setLocalizedText(vagrantboxesLabel, org.openide.util.NbBundle.getMessage(AddBoxesPanel.class, "AddBoxesPanel.vagrantboxesLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(reloadButton, org.openide.util.NbBundle.getMessage(AddBoxesPanel.class, "AddBoxesPanel.reloadButton.text")); // NOI18N
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(filterLabel, org.openide.util.NbBundle.getMessage(AddBoxesPanel.class, "AddBoxesPanel.filterLabel.text")); // NOI18N

        filterTextField.setText(org.openide.util.NbBundle.getMessage(AddBoxesPanel.class, "AddBoxesPanel.filterTextField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameLabel)
                            .addComponent(urlLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameTextField)
                            .addComponent(urlTextField)))
                    .addComponent(vagrantboxesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vagrantboxesLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(reloadButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(filterLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filterTextField)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(urlLabel)
                    .addComponent(urlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vagrantboxesLabel)
                    .addComponent(reloadButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vagrantboxesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterLabel))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void vagrantboxesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vagrantboxesListMouseClicked
        changeUrl();
    }//GEN-LAST:event_vagrantboxesListMouseClicked

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        reloadCount++;
        if (reloadCount > MAX_RELOAD_COUNT) {
            reloadButton.setEnabled(false);
            return;
        }
        setVagrantboxesTable();
    }//GEN-LAST:event_reloadButtonActionPerformed

    private void vagrantboxesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_vagrantboxesListValueChanged
        changeUrl();
    }//GEN-LAST:event_vagrantboxesListValueChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel filterLabel;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton reloadButton;
    private javax.swing.JLabel urlLabel;
    private javax.swing.JTextField urlTextField;
    private javax.swing.JLabel vagrantboxesLabel;
    private javax.swing.JList<String> vagrantboxesList;
    private javax.swing.JScrollPane vagrantboxesScrollPane;
    // End of variables declaration//GEN-END:variables

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
