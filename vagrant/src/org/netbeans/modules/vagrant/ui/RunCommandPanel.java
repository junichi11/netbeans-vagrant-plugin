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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.project.Project;
import org.netbeans.modules.vagrant.command.CommandHistory;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.RunCommandHistory;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.utils.StringUtils;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class RunCommandPanel extends JPanel {

    private static final long serialVersionUID = -4564497771171566386L;
    private List<String> commands;
    private final Map<String, String> helpMap = new HashMap<>();
    private static final RunCommandPanel INSTANCE = new RunCommandPanel();
    private Project project;
    private static final Logger LOGGER = Logger.getLogger(RunCommandPanel.class.getName());

    /**
     * Creates new form RunCommandPanel
     */
    private RunCommandPanel() {
        initComponents();
        setDocumentListener();
        init();
    }

    public static RunCommandPanel getDefault() {
        return INSTANCE;
    }

    private void setDocumentListener() {
        Document comboboxDocument = getParameterComboBoxDocument();
        comboboxDocument.addDocumentListener(new DefaultDocumentListener());
    }

    private void init() {
        commandList.setCellRenderer(new StripeListCellRenderer());
        update();
    }

    private void update() {
        final DefaultListModel<String> model = getListModel();
        model.clear();
        SwingUtilities.invokeLater(() -> {
            try {
                // get command list
                Vagrant vagrant = Vagrant.getDefault();
                commands = vagrant.getCommandListLines();
                int i = 0;

                // set command list
                for (String command : commands) {
                    // #8 command may have description
                    String description = ""; // NOI18N
                    int indexOf = command.indexOf(" "); // NOI18N
                    if (indexOf != -1) {
                        description = command.substring(indexOf);
                        command = command.substring(0, indexOf).trim();
                        commands.set(i, command);
                    }
                    model.add(i, String.format("<html><b>%s</b>%s</html>", command, description)); // NOI18N
                    i++;
                }
            } catch (InvalidVagrantExecutableException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage());
            }
        });
    }

    /**
     * Show dialog.
     *
     * @return DialogDescriptor
     */
    @NbBundle.Messages({
        "# {0} - project name",
        "RunCommandPanel.dialog.title=Run Command - {0}"})
    public DialogDescriptor showDialog(@NonNull Project project) {
        this.project = project;
        FileObject projectDirectory = project.getProjectDirectory();
        String projectName = projectDirectory.getName();

        DialogDescriptor dialogDescriptor = new DialogDescriptor(this, Bundle.RunCommandPanel_dialog_title(projectName));
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        dialog.pack();
        dialog.setVisible(true);
        return dialogDescriptor;
    }

    @NbBundle.Messages({
        "# {0} - command",
        "RunCommandPanel.run.command=Vagrant ({0})"
    })
    public void runCommand() {
        if (project == null) {
            return;
        }
        RunCommandHistory history = RunCommandHistory.Factory.create(project);
        String command = getCommand();
        if (StringUtils.isEmpty(command)) {
            return;
        }
        List<String> params = StringUtils.explode(getParameters(), " ");

        boolean hasError = false;
        try {
            // run command
            Vagrant vagrant = Vagrant.getDefault();
            vagrant.runCommand(project, command, Bundle.RunCommandPanel_run_command(getCommand()), params);
        } catch (InvalidVagrantExecutableException ex) {
            hasError = true;
            LOGGER.log(Level.WARNING, ex.getMessage());
            VagrantUtils.showWarnigDialog(ex.getMessage());
        }

        if (!hasError) {
            history.add(new CommandHistory.Command(command, params));
            updateParameterComboBox(command);
        }
    }

    public String getCommand() {
        int selectedIndex = commandList.getSelectedIndex();
        if (selectedIndex == -1) {
            return ""; // NOI18N
        }
        return commands.get(selectedIndex);
    }

    public String getParameters() {
        Document document = getParameterComboBoxDocument();
        try {
            String params = document.getText(0, document.getLength());
            return params.replaceAll(" +", " "); // NOI18N
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ""; // NOI18N
    }

    private DefaultListModel<String> getListModel() {
        return (DefaultListModel<String>) commandList.getModel();
    }

    private Document getParameterComboBoxDocument() {
        JTextComponent editorComponent = (JTextComponent) parameterComboBox.getEditor().getEditorComponent();
        return editorComponent.getDocument();
    }

    private void updateCommandTextField(String command) {
        commandTextField.setText(String.format("%s %s", command, getParameters())); // NOI18N
    }

    private void updateParameterComboBox(String command) {
        parameterComboBox.removeAllItems();
        RunCommandHistory history = RunCommandHistory.Factory.create(project);
        for (CommandHistory.Command cmd : history.getCommands()) {
            if (!command.equals(cmd.getCommand())) {
                continue;
            }
            parameterComboBox.addItem(StringUtils.implode(cmd.getParameters(), " ")); // NOI18N
        }
    }

    private void addSubcommands(String command, int selectedIndex) throws InvalidVagrantExecutableException {
        String[] split = command.split(" "); // NOI18N
        ArrayList<String> subcommands = new ArrayList<>();
        subcommands.addAll(Arrays.asList(split));
        Vagrant vagrant = Vagrant.getDefault();
        List<String> subcommandList = vagrant.getSubcommandListLines(subcommands);
        if (!subcommandList.isEmpty()) {
            DefaultListModel<String> model = getListModel();
            for (String subcommand : subcommandList) {
                subcommand = command + " " + subcommand; // NOI18N
                model.add(++selectedIndex, String.format("<html><b>%s</b></html>", subcommand)); // NOI18N
                commands.add(selectedIndex, subcommand);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        commandTextField = new javax.swing.JTextField();
        parameterLabel = new javax.swing.JLabel();
        commandScrollPane = new javax.swing.JScrollPane();
        commandList = new javax.swing.JList<String>();
        helpScrollPane = new javax.swing.JScrollPane();
        helpTextArea = new javax.swing.JTextArea();
        reloadButton = new javax.swing.JButton();
        parameterComboBox = new javax.swing.JComboBox<String>();

        commandTextField.setEditable(false);
        commandTextField.setText(org.openide.util.NbBundle.getMessage(RunCommandPanel.class, "RunCommandPanel.commandTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(parameterLabel, org.openide.util.NbBundle.getMessage(RunCommandPanel.class, "RunCommandPanel.parameterLabel.text")); // NOI18N

        commandList.setModel(new DefaultListModel<String>());
        commandList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        commandList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                commandListValueChanged(evt);
            }
        });
        commandScrollPane.setViewportView(commandList);

        helpTextArea.setEditable(false);
        helpTextArea.setColumns(20);
        helpTextArea.setRows(5);
        helpScrollPane.setViewportView(helpTextArea);

        org.openide.awt.Mnemonics.setLocalizedText(reloadButton, org.openide.util.NbBundle.getMessage(RunCommandPanel.class, "RunCommandPanel.reloadButton.text")); // NOI18N
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });

        parameterComboBox.setEditable(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(helpScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                    .addComponent(commandScrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(parameterLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(commandTextField)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(reloadButton))
                    .addComponent(parameterComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(commandTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parameterLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parameterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(commandScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(helpScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reloadButton)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void commandListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_commandListValueChanged
        String command = getCommand();
        int selectedIndex = commandList.getSelectedIndex();
        // update command text field
        updateCommandTextField(command);

        // update comand history
        updateParameterComboBox(command);

        // update help
        String help = helpMap.get(command);
        if (help == null && !StringUtils.isEmpty(command)) {
            try {
                // get help
                Vagrant vagrant = Vagrant.getDefault();
                help = vagrant.getHelp(command);

                // add subcommand
                if (help.contains("Available subcommands") && !command.equals(Vagrant.HELP_COMMAND)) {
                    addSubcommands(command, selectedIndex);
                }

                helpMap.put(command, help);
            } catch (InvalidVagrantExecutableException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage());
            }
        }
        if (help != null) {
            helpTextArea.setText(help);
        } else {
            helpTextArea.setText(""); // NOI18N
        }
    }//GEN-LAST:event_commandListValueChanged

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        update();
        helpMap.clear();
    }//GEN-LAST:event_reloadButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> commandList;
    private javax.swing.JScrollPane commandScrollPane;
    private javax.swing.JTextField commandTextField;
    private javax.swing.JScrollPane helpScrollPane;
    private javax.swing.JTextArea helpTextArea;
    private javax.swing.JComboBox<String> parameterComboBox;
    private javax.swing.JLabel parameterLabel;
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
            updateCommandTextField(getCommand());
        }
    }
}
