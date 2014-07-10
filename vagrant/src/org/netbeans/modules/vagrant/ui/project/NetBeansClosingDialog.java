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
package org.netbeans.modules.vagrant.ui.project;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.KeyStroke;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Pair;
import org.openide.util.Utilities;

/**
 *
 * @author junichi11
 */
public class NetBeansClosingDialog extends JDialog {

    public static enum Status {

        NONE,
        CANCEL,
        SHUTDOWN,
    }

    private Status status = Status.NONE;
    private static final long serialVersionUID = -2989991020950300454L;
    private static final Logger LOGGER = Logger.getLogger(NetBeansClosingDialog.class.getName());

    /**
     * Creates new form NetBeansClosingDialog
     */
    @NbBundle.Messages({
        "NetBeansClosingDialog.title=Confirmation: Vagrant status is running"
    })
    public NetBeansClosingDialog(java.awt.Frame parent, boolean modal, List<Pair<Project, String>> status) {
        super(parent, modal);
        initComponents();
        setTitle(Bundle.NetBeansClosingDialog_title());

        // add projects to list
        DefaultListModel<Pair<Project, String>> model = new DefaultListModel<Pair<Project, String>>();
        for (Pair<Project, String> s : status) {
            model.addElement(s);
        }
        projectList.setModel(model);
        projectList.setCellRenderer(new ProjectListCellRenderer());

        // Close the dialog when Esc is pressed
        String cancelName = "cancel"; // NOI18N
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {
            private static final long serialVersionUID = -5644390861803492172L;

            @Override
            public void actionPerformed(ActionEvent e) {
                doClose(Status.CANCEL);
            }
        });

        // open the dialog on center of screen NetBeans exists on which NetBeans exists
        setBounds(Utilities.findCenterBounds(getPreferredSize()));
    }

    /**
     * @return {@link Status}
     */
    public Status getStatus() {
        return status;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        shutdownButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        projectList = new javax.swing.JList<Pair<Project, String>>();
        haltButton = new javax.swing.JButton();
        haltAllButton = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(shutdownButton, org.openide.util.NbBundle.getMessage(NetBeansClosingDialog.class, "NetBeansClosingDialog.shutdownButton.text")); // NOI18N
        shutdownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shutdownButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cancelButton, org.openide.util.NbBundle.getMessage(NetBeansClosingDialog.class, "NetBeansClosingDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(projectList);

        org.openide.awt.Mnemonics.setLocalizedText(haltButton, org.openide.util.NbBundle.getMessage(NetBeansClosingDialog.class, "NetBeansClosingDialog.haltButton.text")); // NOI18N
        haltButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                haltButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(haltAllButton, org.openide.util.NbBundle.getMessage(NetBeansClosingDialog.class, "NetBeansClosingDialog.haltAllButton.text")); // NOI18N
        haltAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                haltAllButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(haltButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(haltAllButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 121, Short.MAX_VALUE)
                        .addComponent(shutdownButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(shutdownButton)
                    .addComponent(haltButton)
                    .addComponent(haltAllButton))
                .addContainerGap())
        );

        getRootPane().setDefaultButton(shutdownButton);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void shutdownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shutdownButtonActionPerformed
        doClose(Status.SHUTDOWN);
    }//GEN-LAST:event_shutdownButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(Status.CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(Status.CANCEL);
    }//GEN-LAST:event_closeDialog

    private void haltButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_haltButtonActionPerformed
        List<Pair<Project, String>> selectedProjects = projectList.getSelectedValuesList();
        halt(selectedProjects);
    }//GEN-LAST:event_haltButtonActionPerformed

    private void haltAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_haltAllButtonActionPerformed
        DefaultListModel<Pair<Project, String>> model = (DefaultListModel<Pair<Project, String>>) projectList.getModel();
        Enumeration<Pair<Project, String>> elements = model.elements();
        ArrayList<Pair<Project, String>> projects = Collections.list(elements);
        halt(projects);
    }//GEN-LAST:event_haltAllButtonActionPerformed

    private void halt(List<Pair<Project, String>> status) {
        for (Pair<Project, String> s : status) {
            try {
                Vagrant vagrant = Vagrant.getDefault();
                String name = VagrantUtils.getNameFromStatus(s.second());
                vagrant.halt(s.first(), name);
                DefaultListModel<Pair<Project, String>> model = (DefaultListModel<Pair<Project, String>>) projectList.getModel();
                model.removeElement(s);
            } catch (InvalidVagrantExecutableException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void doClose(Status retStatus) {
        status = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton haltAllButton;
    private javax.swing.JButton haltButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<Pair<Project, String>> projectList;
    private javax.swing.JButton shutdownButton;
    // End of variables declaration//GEN-END:variables

    //~ Inner class
    private static class ProjectListCellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 6862574717250523258L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Pair) {
                Pair<?, ?> pair = (Pair<?, ?>) value;
                Object first = pair.first();
                Object second = pair.second();
                if (first instanceof Project && second instanceof String) {
                    Project project = (Project) first;
                    String status = (String) second;
                    ProjectInformation information = ProjectUtils.getInformation(project);
                    setText(String.format("%s : %s", information.getDisplayName(), status));
                    setIcon(information.getIcon());
                    if (isSelected) {
                        setBackground(list.getSelectionBackground());
                        setForeground(list.getSelectionForeground());
                    } else {
                        setBackground(list.getBackground());
                        setForeground(list.getForeground());
                    }
                }
            }
            return this;
        }

    }
}
