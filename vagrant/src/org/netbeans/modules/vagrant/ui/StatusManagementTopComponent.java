/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.vagrant.ui;

import java.awt.Component;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.modules.vagrant.VagrantStatus;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.options.VagrantOptions;
import org.netbeans.modules.vagrant.utils.StringUtils;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Pair;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.netbeans.modules.vagrant.ui//StatusManagement//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "StatusManagementTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "navigator", openAtStartup = true)
@ActionID(category = "Window", id = "org.netbeans.modules.vagrant.ui.StatusManagementTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_StatusManagementAction",
        preferredID = "StatusManagementTopComponent"
)
@Messages({
    "CTL_StatusManagementAction=Vagrant Status",
    "CTL_StatusManagementTopComponent=Vagrant Status",
    "HINT_StatusManagementTopComponent=This is a management window for Vagrant status"
})
public final class StatusManagementTopComponent extends TopComponent implements ChangeListener {

    private static final long serialVersionUID = -5325086456659205908L;
    private static final RequestProcessor RP = new RequestProcessor(StatusManagementTopComponent.class);
    private final DefaultListModel<Pair<Project, String>> model = new DefaultListModel<Pair<Project, String>>();
    private final ProjectListCellRenderer cellRenderer = new ProjectListCellRenderer();

    public StatusManagementTopComponent() {
        initComponents();
        setName(Bundle.CTL_StatusManagementTopComponent());
        setToolTipText(Bundle.HINT_StatusManagementTopComponent());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        projectList = new javax.swing.JList<Pair<Project, String>>();
        commandToolBar = new javax.swing.JToolBar();
        upButton = new javax.swing.JButton();
        reloadButton = new javax.swing.JButton();
        suspendButton = new javax.swing.JButton();
        resumeButton = new javax.swing.JButton();
        haltButton = new javax.swing.JButton();
        destroyButton = new javax.swing.JButton();
        statusButton = new javax.swing.JButton();
        shareButton = new javax.swing.JButton();
        provisionButton = new javax.swing.JButton();
        reloadListButton = new javax.swing.JButton();
        reloadListAllButton = new javax.swing.JButton();

        jScrollPane1.setMinimumSize(new java.awt.Dimension(0, 23));

        jScrollPane1.setViewportView(projectList);

        commandToolBar.setFloatable(false);
        commandToolBar.setRollover(true);
        commandToolBar.setMinimumSize(new java.awt.Dimension(0, 30));

        upButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vagrant/resources/up.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(upButton, org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.upButton.text")); // NOI18N
        upButton.setToolTipText(org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.upButton.toolTipText")); // NOI18N
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });
        commandToolBar.add(upButton);

        reloadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vagrant/resources/reload.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(reloadButton, org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.reloadButton.text")); // NOI18N
        reloadButton.setToolTipText(org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.reloadButton.toolTipText")); // NOI18N
        reloadButton.setFocusable(false);
        reloadButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        reloadButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });
        commandToolBar.add(reloadButton);

        suspendButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vagrant/resources/suspend.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(suspendButton, org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.suspendButton.text")); // NOI18N
        suspendButton.setToolTipText(org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.suspendButton.toolTipText")); // NOI18N
        suspendButton.setFocusable(false);
        suspendButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        suspendButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        suspendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                suspendButtonActionPerformed(evt);
            }
        });
        commandToolBar.add(suspendButton);

        resumeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vagrant/resources/resume.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(resumeButton, org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.resumeButton.text")); // NOI18N
        resumeButton.setToolTipText(org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.resumeButton.toolTipText")); // NOI18N
        resumeButton.setFocusable(false);
        resumeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resumeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        resumeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resumeButtonActionPerformed(evt);
            }
        });
        commandToolBar.add(resumeButton);

        haltButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vagrant/resources/halt.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(haltButton, org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.haltButton.text")); // NOI18N
        haltButton.setToolTipText(org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.haltButton.toolTipText")); // NOI18N
        haltButton.setFocusable(false);
        haltButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        haltButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        haltButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                haltButtonActionPerformed(evt);
            }
        });
        commandToolBar.add(haltButton);

        destroyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vagrant/resources/destroy.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(destroyButton, org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.destroyButton.text")); // NOI18N
        destroyButton.setToolTipText(org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.destroyButton.toolTipText")); // NOI18N
        destroyButton.setFocusable(false);
        destroyButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        destroyButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        destroyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                destroyButtonActionPerformed(evt);
            }
        });
        commandToolBar.add(destroyButton);

        statusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vagrant/resources/status.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(statusButton, org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.statusButton.text")); // NOI18N
        statusButton.setToolTipText(org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.statusButton.toolTipText")); // NOI18N
        statusButton.setFocusable(false);
        statusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        statusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        statusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusButtonActionPerformed(evt);
            }
        });
        commandToolBar.add(statusButton);

        shareButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vagrant/resources/share.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(shareButton, org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.shareButton.text")); // NOI18N
        shareButton.setToolTipText(org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.shareButton.toolTipText")); // NOI18N
        shareButton.setFocusable(false);
        shareButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        shareButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        shareButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shareButtonActionPerformed(evt);
            }
        });
        commandToolBar.add(shareButton);

        provisionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/vagrant/resources/provision.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(provisionButton, org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.provisionButton.text")); // NOI18N
        provisionButton.setToolTipText(org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.provisionButton.toolTipText")); // NOI18N
        provisionButton.setFocusable(false);
        provisionButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        provisionButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        provisionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                provisionButtonActionPerformed(evt);
            }
        });
        commandToolBar.add(provisionButton);

        org.openide.awt.Mnemonics.setLocalizedText(reloadListButton, org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.reloadListButton.text")); // NOI18N
        reloadListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadListButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(reloadListAllButton, org.openide.util.NbBundle.getMessage(StatusManagementTopComponent.class, "StatusManagementTopComponent.reloadListAllButton.text")); // NOI18N
        reloadListAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadListAllButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(reloadListAllButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reloadListButton))
            .addComponent(commandToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(commandToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reloadListButton)
                    .addComponent(reloadListAllButton)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void reloadListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadListButtonActionPerformed
        reloadStatus(getSelectedStatus(), true);
    }//GEN-LAST:event_reloadListButtonActionPerformed

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        runCommand(Vagrant.UP_COMMAND, true);
    }//GEN-LAST:event_upButtonActionPerformed

    private void haltButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_haltButtonActionPerformed
        runCommand(Vagrant.HALT_COMMAND, true);
    }//GEN-LAST:event_haltButtonActionPerformed

    private void reloadListAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadListAllButtonActionPerformed
        reload(true);
    }//GEN-LAST:event_reloadListAllButtonActionPerformed

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        runCommand(Vagrant.RELOAD_COMMAND, true);
    }//GEN-LAST:event_reloadButtonActionPerformed

    private void suspendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_suspendButtonActionPerformed
        runCommand(Vagrant.SUSPEND_COMMAND, true);
    }//GEN-LAST:event_suspendButtonActionPerformed

    private void resumeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resumeButtonActionPerformed
        runCommand(Vagrant.RESUME_COMMAND, true);
    }//GEN-LAST:event_resumeButtonActionPerformed

    private void destroyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_destroyButtonActionPerformed
        runCommand(Vagrant.DESTROY_COMMAND, true);
    }//GEN-LAST:event_destroyButtonActionPerformed

    private void statusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusButtonActionPerformed
        runCommand(Vagrant.STATUS_COMMAND, true);
    }//GEN-LAST:event_statusButtonActionPerformed

    private void shareButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shareButtonActionPerformed
        runCommand(Vagrant.SHARE_COMMAND, true);
    }//GEN-LAST:event_shareButtonActionPerformed

    private void provisionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_provisionButtonActionPerformed
        runCommand(Vagrant.PROVISION_COMMAND, true);
    }//GEN-LAST:event_provisionButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar commandToolBar;
    private javax.swing.JButton destroyButton;
    private javax.swing.JButton haltButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<Pair<Project, String>> projectList;
    private javax.swing.JButton provisionButton;
    private javax.swing.JButton reloadButton;
    private javax.swing.JButton reloadListAllButton;
    private javax.swing.JButton reloadListButton;
    private javax.swing.JButton resumeButton;
    private javax.swing.JButton shareButton;
    private javax.swing.JButton statusButton;
    private javax.swing.JButton suspendButton;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        VagrantStatus statuses = Lookup.getDefault().lookup(VagrantStatus.class);
        if (statuses != null) {
            statuses.addChangeListener(this);
        }
    }

    @Override
    public void componentClosed() {
        model.clear();
        VagrantStatus status = Lookup.getDefault().lookup(VagrantStatus.class);
        if (status != null) {
            status.removeChangeListener(this);
        }
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    /**
     * Reload project status list.
     *
     * @param runOnBackground {@code true} if reload on background
     */
    private void reload(boolean runOnBackground) {
        if (!runOnBackground) {
            reload();
            return;
        }
        RP.execute(new Runnable() {

            @Override
            public void run() {
                reload();
            }
        });
    }

    /**
     * Reload project status list.
     */
    private synchronized void reload() {
        setAllButtonsEnabled(false);
        String vagrantPath = VagrantOptions.getInstance().getVagrantPath();
        if (StringUtils.isEmpty(vagrantPath)) {
            return;
        }

        model.clear();
        VagrantStatus status = Lookup.getDefault().lookup(VagrantStatus.class);
        if (status == null) {
            return;
        }
        status.refresh();
        setModel();
        setCellRenderer();
    }

    /**
     * Reload a project status.
     *
     * @param status
     * @param runOnBackground {@code true} if reload status on background
     */
    private void reloadStatus(final Pair<Project, String> status, boolean runOnBackground) {
        if (!runOnBackground) {
            reloadStatus(status);
            return;
        }
        RP.execute(new Runnable() {

            @Override
            public void run() {
                reloadStatus(status);
            }
        });
    }

    /**
     * Reload a project status.
     *
     * @param status
     */
    private synchronized void reloadStatus(Pair<Project, String> status) {
        if (status == null) {
            return;
        }
        setAllButtonsEnabled(false);
        Project project = status.first();
        if (project == null) {
            return;
        }
        VagrantStatus vagrantStatus = Lookup.getDefault().lookup(VagrantStatus.class);
        if (vagrantStatus == null) {
            return;
        }
        vagrantStatus.update(project);
    }

    /**
     * Set all buttons enabled.
     *
     * @param isEnabled
     */
    private void setAllButtonsEnabled(boolean isEnabled) {
        Component[] commands = commandToolBar.getComponents();
        for (Component command : commands) {
            if (command instanceof JButton) {
                command.setEnabled(isEnabled);
            }
        }
        reloadListButton.setEnabled(isEnabled);
        reloadListAllButton.setEnabled(isEnabled);
    }

    /**
     * Get selected value on project list.
     *
     * @return selected value
     */
    private Pair<Project, String> getSelectedStatus() {
        return projectList.getSelectedValue();
    }

    /**
     * Run command.
     *
     * @param command command name (e.g. up, halt, ...)
     * @param runOnBackground {@code true} if run command on background
     */
    private void runCommand(final String command, boolean runOnBackground) {
        if (!runOnBackground) {
            runCommand(command);
            return;
        }
        RP.execute(new Runnable() {

            @Override
            public void run() {
                runCommand(command);
            }
        });
    }

    /**
     * Run command.
     *
     * @param command command name (e.g. up, halt, ...)
     */
    private void runCommand(String command) {
        setAllButtonsEnabled(false);
        try {
            Pair<Project, String> selectedStatus = getSelectedStatus();
            if (selectedStatus == null) {
                return;
            }
            Project selectedProject = selectedStatus.first();
            if (selectedProject == null || StringUtils.isEmpty(command)) {
                return;
            }
            try {
                Vagrant vagrant = Vagrant.getDefault();
                Future<Integer> result = null;
                if (command.equals(Vagrant.UP_COMMAND)) {
                    result = vagrant.up(selectedProject);
                } else if (command.equals(Vagrant.RELOAD_COMMAND)) {
                    result = vagrant.reload(selectedProject);
                } else if (command.equals(Vagrant.SUSPEND_COMMAND)) {
                    result = vagrant.suspend(selectedProject);
                } else if (command.equals(Vagrant.RESUME_COMMAND)) {
                    result = vagrant.resume(selectedProject);
                } else if (command.equals(Vagrant.HALT_COMMAND)) {
                    result = vagrant.halt(selectedProject);
                } else if (command.equals(Vagrant.DESTROY_COMMAND)) {
                    result = vagrant.destroy(selectedProject);
                } else if (command.equals(Vagrant.STATUS_COMMAND)) {
                    result = vagrant.status(selectedProject);
                } else if (command.equals(Vagrant.SHARE_COMMAND)) {
                    result = vagrant.share(selectedProject);
                } else if (command.equals(Vagrant.PROVISION_COMMAND)) {
                    result = vagrant.provisiton(selectedProject);
                }
                if (result != null) {
                    result.get();
                }
            } catch (InvalidVagrantExecutableException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        } finally {
            setAllButtonsEnabled(true);
        }
    }

    @Override
    public synchronized void stateChanged(ChangeEvent e) {
        final Object source = e.getSource();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (source instanceof VagrantStatus) {
                    VagrantStatus vagrantStatus = (VagrantStatus) source;
                    Pair<Project, String> selectedValue = getSelectedStatus();
                    Pair<Project, String> newSelectedValue = null;
                    model.clear();
                    for (Pair<Project, String> status : vagrantStatus.getAll()) {
                        Project project = status.first();
                        if (selectedValue != null) {
                            Project selectedProject = selectedValue.first();
                            if (selectedProject == project) {
                                newSelectedValue = status;
                            }
                        }
                        model.addElement(status);
                    }

                    // add model when project is open
                    setModel();
                    setCellRenderer();
                    if (newSelectedValue != null) {
                        projectList.setSelectedValue(newSelectedValue, true);
                    }
                    setAllButtonsEnabled(true);
                }
            }
        });
    }

    private void setModel() {
        ListModel<Pair<Project, String>> m = projectList.getModel();
        if (model != m) {
            projectList.setModel(model);
        }
    }

    private void setCellRenderer() {
        ListCellRenderer<? super Pair<Project, String>> cr = projectList.getCellRenderer();
        if (cellRenderer != cr) {
            projectList.setCellRenderer(cellRenderer);
        }
    }

    //~ Inner class
    private static class ProjectListCellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 6862574717250523258L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Pair<Project, String> projectStatus = (Pair<Project, String>) value;
            ProjectInformation information = ProjectUtils.getInformation(projectStatus.first());
            String status = String.format("%s : %s", information.getDisplayName(), projectStatus.second()); // NOI18N
            setText(status);
            setIcon(information.getIcon());
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }

    }
}
