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

import java.awt.Cursor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.options.VagrantOptions;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

public final class BoxesPanel extends VagrantCategoryPanel {

    private static final long serialVersionUID = 6099328973907741899L;
    private static final String CATEGORY_NAME = "Boxes"; // NOI18N
    private List<String> boxList;

    public BoxesPanel() {
        initComponents();
        init();
    }

    private void init() {
        // execute vagrant box list
        setBoxList();
        setBoxesTable();
        valid();
    }

    public String getBoxesUrl() {
        return boxesUrlTextField.getText().trim();
    }

    public void setBoxesUrl(String url) {
        boxesUrlTextField.setText(url);
    }

    private void setBoxList() {
        VagrantOptions options = VagrantOptions.getInstance();
        if (!options.getVagrantPath().isEmpty()) {
            Vagrant vagrant;
            try {
                vagrant = Vagrant.getDefault();
                boxList = vagrant.getBoxList();
            } catch (InvalidVagrantExecutableException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            boxList = Collections.emptyList();
        }
    }

    private void setBoxesTable() {
        DefaultTableModel model = (DefaultTableModel) boxesTable.getModel();
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i > -1; i--) {
            model.removeRow(i);
        }
        for (String box : boxList) {
            model.addRow(new String[]{box});
        }
    }

    private int getTableRowCount() {
        DefaultTableModel model = (DefaultTableModel) boxesTable.getModel();
        return model.getRowCount();
    }

    @Override
    void load() {
        setBoxesUrl(getOptions().getBoxesUrl());
    }

    @Override
    void store() {
        getOptions().setBoxesUrl(getBoxesUrl());
    }

    @Override
    public void reload() {
        init();
    }

    @Override
    boolean valid() {
        VagrantOptions options = VagrantOptions.getInstance();
        boolean isEmpty = options.getVagrantPath().isEmpty();
        addButton.setEnabled(!isEmpty);
        removeButton.setEnabled(!isEmpty);

        // TODO check whether form is consistent and complete
        return !isEmpty;
    }

    @Override
    public String getCategoryName() {
        return CATEGORY_NAME;
    }

    private VagrantOptions getOptions() {
        return VagrantOptions.getInstance();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        boxesLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        boxesTable = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        noteLabel = new javax.swing.JLabel();
        learnMoreVagrantBoxLabel = new javax.swing.JLabel();
        learnMoreVagrantboxesLabel = new javax.swing.JLabel();
        reloadButton = new javax.swing.JButton();
        boxesUrlLabel = new javax.swing.JLabel();
        boxesUrlTextField = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(boxesLabel, org.openide.util.NbBundle.getMessage(BoxesPanel.class, "BoxesPanel.boxesLabel.text")); // NOI18N

        boxesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }

            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(boxesTable);

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(BoxesPanel.class, "BoxesPanel.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(BoxesPanel.class, "BoxesPanel.removeButton.text")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(noteLabel, org.openide.util.NbBundle.getMessage(BoxesPanel.class, "BoxesPanel.noteLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(learnMoreVagrantBoxLabel, org.openide.util.NbBundle.getMessage(BoxesPanel.class, "BoxesPanel.learnMoreVagrantBoxLabel.text")); // NOI18N
        learnMoreVagrantBoxLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                learnMoreVagrantBoxLabelMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                learnMoreVagrantBoxLabelMousePressed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(learnMoreVagrantboxesLabel, org.openide.util.NbBundle.getMessage(BoxesPanel.class, "BoxesPanel.learnMoreVagrantboxesLabel.text")); // NOI18N
        learnMoreVagrantboxesLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                learnMoreVagrantboxesLabelMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                learnMoreVagrantboxesLabelMousePressed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(reloadButton, org.openide.util.NbBundle.getMessage(BoxesPanel.class, "BoxesPanel.reloadButton.text")); // NOI18N
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(boxesUrlLabel, org.openide.util.NbBundle.getMessage(BoxesPanel.class, "BoxesPanel.boxesUrlLabel.text")); // NOI18N

        boxesUrlTextField.setText(org.openide.util.NbBundle.getMessage(BoxesPanel.class, "BoxesPanel.boxesUrlTextField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(boxesLabel)
                            .addComponent(noteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(learnMoreVagrantboxesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(learnMoreVagrantBoxLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 231, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(removeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(reloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(boxesUrlLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(boxesUrlTextField)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(boxesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reloadButton)
                        .addGap(0, 44, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boxesUrlLabel)
                    .addComponent(boxesUrlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(learnMoreVagrantBoxLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(learnMoreVagrantboxesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void learnMoreVagrantboxesLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_learnMoreVagrantboxesLabelMouseEntered
        evt.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_learnMoreVagrantboxesLabelMouseEntered

    private void learnMoreVagrantboxesLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_learnMoreVagrantboxesLabelMousePressed
        try {
            HtmlBrowser.URLDisplayer.getDefault().showURL(new URL("http://www.vagrantbox.es")); // NOI18N
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_learnMoreVagrantboxesLabelMousePressed

    private void learnMoreVagrantBoxLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_learnMoreVagrantBoxLabelMouseEntered
        evt.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_learnMoreVagrantBoxLabelMouseEntered

    private void learnMoreVagrantBoxLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_learnMoreVagrantBoxLabelMousePressed
        try {
            HtmlBrowser.URLDisplayer.getDefault().showURL(new URL("http://docs.vagrantup.com/v2/boxes.html")); // NOI18N
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_learnMoreVagrantBoxLabelMousePressed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        if (!valid()) {
            init();
        }

        if (!getOptions().getBoxesUrl().endsWith(getBoxesUrl())) {
            store();
        }

        boolean isEmpty = false;
        if (getTableRowCount() == 0) {
            isEmpty = true;
        }

        AddBoxesPanel panel = AddBoxesPanel.getDefault(isEmpty);
        DialogDescriptor discriptor = panel.showDialog();
        if (discriptor.getValue() == DialogDescriptor.OK_OPTION) {
            panel.runVagrantBoxAdd();
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        reload();
    }//GEN-LAST:event_reloadButtonActionPerformed

    @NbBundle.Messages({
        "# {0} - box name",
        "BoxesPanel.remove.confirmation=Do you really want to remove {0}?"
    })
    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        int selectedRow = boxesTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        TableModel model = boxesTable.getModel();
        String selectedValue = (String) model.getValueAt(selectedRow, 0);
        NotifyDescriptor.Confirmation message = new NotifyDescriptor.Confirmation(
                Bundle.BoxesPanel_remove_confirmation(selectedValue),
                NotifyDescriptor.OK_CANCEL_OPTION);
        if (DialogDisplayer.getDefault().notify(message) != NotifyDescriptor.OK_OPTION) {
            return;
        }

        // get box name and provider
        String boxName = VagrantUtils.getBoxName(selectedValue);
        if (boxName == null) {
            return;
        }
        String provider = VagrantUtils.getProvider(selectedValue);
        provider = provider == null ? "" : provider;  // NOI18N

        // run command
        try {
            Vagrant vagrant = Vagrant.getDefault();
            Future<Integer> result = vagrant.box(Vagrant.BOX.REMOVE, Arrays.asList(boxName, provider));
            try {
                result.get();
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            reload();
        } catch (InvalidVagrantExecutableException ex) {
            // TODO
        }
    }//GEN-LAST:event_removeButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JLabel boxesLabel;
    private javax.swing.JTable boxesTable;
    private javax.swing.JLabel boxesUrlLabel;
    private javax.swing.JTextField boxesUrlTextField;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel learnMoreVagrantBoxLabel;
    private javax.swing.JLabel learnMoreVagrantboxesLabel;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JButton reloadButton;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables
}
