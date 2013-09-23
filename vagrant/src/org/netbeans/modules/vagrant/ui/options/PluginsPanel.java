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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.options.VagrantOptions;
import org.netbeans.modules.vagrant.utils.StringUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

public final class PluginsPanel extends VagrantCategoryPanel {

    private static final long serialVersionUID = 2529012115187810189L;
    private static final String CATEGORY_NAME = "Plugins"; // NOI18N
    private List<String> pluginList;
    private static final Logger LOGGER = Logger.getLogger(PluginsPanel.class.getName());

    public PluginsPanel() {
        initComponents();
        init();
    }

    private void init() {
        // execute vagrant plugin list
        setPluginList();
        setPluginsTable();
        valid();
    }

    private void setPluginList() {
        VagrantOptions options = VagrantOptions.getInstance();
        if (!options.getVagrantPath().isEmpty()) {
            Vagrant vagrant;
            try {
                vagrant = Vagrant.getDefault();
                pluginList = vagrant.getPluginList();
            } catch (InvalidVagrantExecutableException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage());
            }
        } else {
            pluginList = Collections.emptyList();
        }
    }

    private void setPluginsTable() {
        DefaultTableModel model = (DefaultTableModel) pluginsTable.getModel();
        // clear
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i > -1; i--) {
            model.removeRow(i);
        }

        // add
        for (String plugin : pluginList) {
            model.addRow(new String[]{plugin});
        }
    }

    private void runCommand(final Vagrant.PLUGIN command) {
        if (command != Vagrant.PLUGIN.UNINSTALL && command != Vagrant.PLUGIN.UPDATE) {
            return;
        }

        int selectedRow = pluginsTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // get selected value
        TableModel model = pluginsTable.getModel();
        String selectedValue = (String) model.getValueAt(selectedRow, 0);
        final String pluginName = selectedValue.replaceAll("\\(.+\\)", "").trim(); // NOI18N
        if (StringUtils.isEmpty(pluginName)) {
            return;
        }

        // show confirmation dialog
        if (command == Vagrant.PLUGIN.UNINSTALL) {
            NotifyDescriptor.Confirmation message = new NotifyDescriptor.Confirmation(
                    Bundle.PluginsPanel_uninstall_confirmation(pluginName),
                    NotifyDescriptor.OK_CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(message) != NotifyDescriptor.OK_OPTION) {
                return;
            }
        }

        // run command
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Vagrant vagrant = Vagrant.getDefault();
                    Future<Integer> result = vagrant.plugin(command, Arrays.asList(pluginName));
                    try {
                        result.get();
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }

                    // reaload
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            AddPluginsPanel.getDefault().removeInstalledPlugin(pluginName);
                            reload();
                        }
                    });
                } catch (InvalidVagrantExecutableException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage());
                }
            }
        }).start();
    }

    @Override
    void load() {
        // noop
    }

    @Override
    void store() {
        // noop
    }

    @Override
    public void reload() {
        init();
    }

    @Override
    boolean valid() {
        VagrantOptions options = VagrantOptions.getInstance();
        boolean isEmpty = options.getVagrantPath().isEmpty();
        installButton.setEnabled(!isEmpty);
        uninstallButton.setEnabled(!isEmpty);
        updateButton.setEnabled(!isEmpty);
        return true;
    }

    @Override
    public String getCategoryName() {
        return CATEGORY_NAME;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pluginsLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pluginsTable = new javax.swing.JTable();
        installButton = new javax.swing.JButton();
        uninstallButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        reloadButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(pluginsLabel, org.openide.util.NbBundle.getMessage(PluginsPanel.class, "PluginsPanel.pluginsLabel.text")); // NOI18N

        pluginsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(pluginsTable);

        org.openide.awt.Mnemonics.setLocalizedText(installButton, org.openide.util.NbBundle.getMessage(PluginsPanel.class, "PluginsPanel.installButton.text")); // NOI18N
        installButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(uninstallButton, org.openide.util.NbBundle.getMessage(PluginsPanel.class, "PluginsPanel.uninstallButton.text")); // NOI18N
        uninstallButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uninstallButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(updateButton, org.openide.util.NbBundle.getMessage(PluginsPanel.class, "PluginsPanel.updateButton.text")); // NOI18N
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(reloadButton, org.openide.util.NbBundle.getMessage(PluginsPanel.class, "PluginsPanel.reloadButton.text")); // NOI18N
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(installButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(uninstallButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(updateButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(reloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pluginsLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pluginsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(installButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uninstallButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updateButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reloadButton)
                        .addGap(0, 114, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void installButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_installButtonActionPerformed
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AddPluginsPanel panel = AddPluginsPanel.getDefault();
                DialogDescriptor discriptor = panel.showDialog();
                if (discriptor.getValue() == DialogDescriptor.OK_OPTION) {
                    panel.runVagrantPluginInstall();
                    reload();
                }
            }
        });

    }//GEN-LAST:event_installButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        runCommand(Vagrant.PLUGIN.UPDATE);
    }//GEN-LAST:event_updateButtonActionPerformed

    @NbBundle.Messages({
        "# {0} - plugin name",
        "PluginsPanel.uninstall.confirmation=Do you really want to uninstall {0}?"
    })
    private void uninstallButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uninstallButtonActionPerformed
        runCommand(Vagrant.PLUGIN.UNINSTALL);
    }//GEN-LAST:event_uninstallButtonActionPerformed

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        reload();
    }//GEN-LAST:event_reloadButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton installButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel pluginsLabel;
    private javax.swing.JTable pluginsTable;
    private javax.swing.JButton reloadButton;
    private javax.swing.JButton uninstallButton;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
