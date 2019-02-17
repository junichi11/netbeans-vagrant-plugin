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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.plugins.VagrantPluginItem;
import org.netbeans.modules.vagrant.plugins.VagrantPluginsSupport;
import org.netbeans.modules.vagrant.ui.StripeListCellRenderer;
import org.netbeans.modules.vagrant.utils.StringUtils;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.HtmlBrowser;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public final class AddPluginsPanel extends JPanel {

    private static final long serialVersionUID = 3344008774071588301L;
    private static final int MAX_RELOAD_COUNT = 5;
    private static final AddPluginsPanel INSTANCE = new AddPluginsPanel();
    private List<VagrantPluginItem> plugins;
    private final List<VagrantPluginItem> filteredPlugins = new LinkedList<>();
    private final Set<String> installedPlugins = new HashSet<>();
    private int reloadCount = 0;

    /**
     * Creates new form AddBoxesPanel
     */
    public AddPluginsPanel() {
        initComponents();
        init();
        addDocumentListener();
    }

    private void addDocumentListener() {
        filterTextField.getDocument().addDocumentListener(new DefaultDocumentListener());
    }

    public static AddPluginsPanel getDefault() {
        return INSTANCE;
    }

    private void init() {
        availablePluginsList.setCellRenderer(new StripeListCellRenderer());
        availablePluginsList.setModel(new DefaultListModel<>());
        setInstalledPlugins();
        setPluginsTable();
    }

    public String getPlugin() {
        return pluginTextField.getText().trim();
    }

    public String getFilter() {
        return filterTextField.getText();
    }

    public void removeInstalledPlugin(String pluginName) {
        installedPlugins.remove(pluginName);
    }

    @NbBundle.Messages("AddPluginsPanel.dialog.title=Install plugin")
    public DialogDescriptor showDialog() {
        DialogDescriptor dialogDescriptor = new DialogDescriptor(this, Bundle.AddPluginsPanel_dialog_title());
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        dialog.pack();
        dialog.setVisible(true);
        return dialogDescriptor;
    }

    @NbBundle.Messages("AddPluginsPanel.validate.empty=Plugin name is empty.")
    public void runVagrantPluginInstall() {
        try {
            String plugin = getPlugin();
            if (StringUtils.isEmpty(plugin)) {
                showDialog();
                VagrantUtils.showWarnigDialog(Bundle.AddPluginsPanel_validate_empty());
                return;
            }

            Vagrant vagrant = Vagrant.getDefault();
            Future<Integer> result = vagrant.plugin(Vagrant.PLUGIN.INSTALL, Collections.singletonList(plugin));
            try {
                result.get();
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            installedPlugins.add(plugin);
            setPluginsTable();
        } catch (InvalidVagrantExecutableException ex) {
            VagrantUtils.showWarnigDialog(ex.getMessage());
        }
    }

    private void setInstalledPlugins() {
        try {
            Vagrant vagrant = Vagrant.getDefault();
            List<String> pluginList = vagrant.getPluginList();
            installedPlugins.clear();
            for (String plugin : pluginList) {
                installedPlugins.add(plugin.replaceAll("\\(.+\\)", "").trim()); // NOI18N
            }
        } catch (InvalidVagrantExecutableException ex) {
            VagrantUtils.showWarnigDialog(ex.getMessage());
        }
    }

    @NbBundle.Messages("AddPluginsPanel.no.available.plugin=There is no available plugin.")
    private void setPluginsTable() {
        // get plugins
        plugins = VagrantPluginsSupport.getPlugins();
        if (plugins.isEmpty()) {
            filteredPlugins.clear();
            DefaultListModel<String> model = (DefaultListModel<String>) availablePluginsList.getModel();
            model.clear();
            model.add(0, Bundle.AddPluginsPanel_no_available_plugin());
            return;
        }
        updatePluginsTable();
    }

    private void updatePluginsTable() {
        DefaultListModel<String> model = (DefaultListModel<String>) availablePluginsList.getModel();
        model.clear();
        filteredPlugins.clear();
        String filter = getFilter();
        boolean isEmpty = StringUtils.isEmpty(filter);

        // get filters
        filter = filter.replaceAll(" +", " "); // NOI18N
        String[] filters = filter.split(" "); // NOI18N

        // add
        int i = 0;
        for (VagrantPluginItem plugin : plugins) {
            String name = plugin.getName();
            // don't add installed plugins
            if (installedPlugins.contains(name)) {
                continue;
            }

            String category = plugin.getCategory();
            if (isEmpty || StringUtils.containsAll(name.toLowerCase() + " " + category.toLowerCase(), filters)) { // NOI18N
                model.add(i, String.format("<html><b>%s</b>  (<i>%s</i>)  <a href=\"%s\">%s</a></html>", name, plugin.getCategory(), plugin.getUrl(), plugin.getUrl())); // NOI18N
                filteredPlugins.add(i, plugin);
                i++;
            }
        }
    }

    private void changePlugin() {
        if (plugins.isEmpty() || filteredPlugins.isEmpty()) {
            return;
        }

        int selectedIndex = availablePluginsList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        VagrantPluginItem plugin = filteredPlugins.get(selectedIndex);
        pluginTextField.setText(plugin.getName());
    }

    private void fireChange() {
        updatePluginsTable();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        availablePluginsScrollPane = new javax.swing.JScrollPane();
        availablePluginsList = new javax.swing.JList<String>();
        availablePluginsLabel = new javax.swing.JLabel();
        reloadButton = new javax.swing.JButton();
        filterLabel = new javax.swing.JLabel();
        filterTextField = new javax.swing.JTextField();
        pluginLabel = new javax.swing.JLabel();
        pluginTextField = new javax.swing.JTextField();

        availablePluginsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        availablePluginsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                availablePluginsListMouseClicked(evt);
            }
        });
        availablePluginsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                availablePluginsListValueChanged(evt);
            }
        });
        availablePluginsScrollPane.setViewportView(availablePluginsList);

        org.openide.awt.Mnemonics.setLocalizedText(availablePluginsLabel, org.openide.util.NbBundle.getMessage(AddPluginsPanel.class, "AddPluginsPanel.availablePluginsLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(reloadButton, org.openide.util.NbBundle.getMessage(AddPluginsPanel.class, "AddPluginsPanel.reloadButton.text")); // NOI18N
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(filterLabel, org.openide.util.NbBundle.getMessage(AddPluginsPanel.class, "AddPluginsPanel.filterLabel.text")); // NOI18N

        filterTextField.setText(org.openide.util.NbBundle.getMessage(AddPluginsPanel.class, "AddPluginsPanel.filterTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(pluginLabel, org.openide.util.NbBundle.getMessage(AddPluginsPanel.class, "AddPluginsPanel.pluginLabel.text")); // NOI18N

        pluginTextField.setText(org.openide.util.NbBundle.getMessage(AddPluginsPanel.class, "AddPluginsPanel.pluginTextField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(availablePluginsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(availablePluginsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(reloadButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pluginLabel)
                            .addComponent(filterLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(filterTextField)
                            .addComponent(pluginTextField))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(availablePluginsLabel)
                    .addComponent(reloadButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(availablePluginsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pluginLabel)
                    .addComponent(pluginTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages("AddPluignsPanel.invalid.plugin.url=Plugin url is invalid.")
    private void availablePluginsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_availablePluginsListMouseClicked
        int clickCount = evt.getClickCount();
        if (clickCount == 1) {
            changePlugin();
        } else if (clickCount == 2) {
            int selectedIndex = availablePluginsList.getSelectedIndex();
            VagrantPluginItem item = filteredPlugins.get(selectedIndex);
            try {
                HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(item.getUrl())); // NOI18N
            } catch (MalformedURLException ex) {
                VagrantUtils.showWarnigDialog(Bundle.AddPluignsPanel_invalid_plugin_url());
            }
        }
    }//GEN-LAST:event_availablePluginsListMouseClicked

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        reloadCount++;
        if (reloadCount > MAX_RELOAD_COUNT) {
            reloadButton.setEnabled(false);
            return;
        }
        setPluginsTable();
    }//GEN-LAST:event_reloadButtonActionPerformed

    private void availablePluginsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_availablePluginsListValueChanged
        changePlugin();
    }//GEN-LAST:event_availablePluginsListValueChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel availablePluginsLabel;
    private javax.swing.JList<String> availablePluginsList;
    private javax.swing.JScrollPane availablePluginsScrollPane;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JLabel pluginLabel;
    private javax.swing.JTextField pluginTextField;
    private javax.swing.JButton reloadButton;
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
