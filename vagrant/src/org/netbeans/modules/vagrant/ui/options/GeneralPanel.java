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
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.options.VagrantOptions;
import org.netbeans.modules.vagrant.ui.VagrantStatusLineElement;
import org.netbeans.modules.vagrant.utils.FileUtils;
import org.netbeans.modules.vagrant.utils.StringUtils;
import org.netbeans.modules.vagrant.utils.UiUtils;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public final class GeneralPanel extends VagrantCategoryPanel {

    private static final String VAGRANT_LAST_FOLDER_SUFFIX = ".vagrant"; // NOI18N
    private static final String CATEGORY_NAME = "General"; // NOI18N
    private static final long serialVersionUID = -3566086017181566981L;
    private final ChangeSupport cs = new ChangeSupport(this);

    public GeneralPanel() {
        initComponents();
        init();
    }

    private void init() {
        setVersion();
    }

    public String getVagrantPath() {
        return vagrantPathTextField.getText();
    }

    private void setVagrantPath(String vagrantPath) {
        vagrantPathTextField.setText(vagrantPath);
    }

    public boolean isShowStatus() {
        return showStatusCheckBox.isSelected();
    }

    private void setShowStatus(boolean show) {
        showStatusCheckBox.setSelected(show);
    }

    private void setVersion() {
        setVersion(""); // NOI18N
        versionLabel.setVisible(false);
        String vagrantPath = getOptions().getVagrantPath();
        if (StringUtils.isEmpty(vagrantPath)) {
            return;
        }
        try {
            Vagrant vagrant = Vagrant.getDefault();
            String version = vagrant.getVersion();
            if (!StringUtils.isEmpty(version)) {
                setVersion(version);
                versionLabel.setVisible(true);
            }
        } catch (InvalidVagrantExecutableException ex) {
            setVersion(""); // NOI18N
        }
    }

    public void setVersion(String version) {
        versionLabel.setText(version);
    }

    public void addChangeListener(ChangeListener listener) {
        cs.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        cs.removeChangeListener(listener);
    }

    private void fireChange() {
        cs.fireChange();
    }

    @Override
    void store() {
        String vagrantPath = getVagrantPath();
        if (StringUtils.isEmpty(vagrantPath) || !isVagrantScript(vagrantPath)) {
            vagrantPath = ""; // NOI18N
            setVagrantPath(vagrantPath);
        }
        getOptions().setVagrantPath(vagrantPath);
        getOptions().setShowStatus(isShowStatus());
        VagrantStatusLineElement statusLineElement = Lookup.getDefault().lookup(VagrantStatusLineElement.class);
        if (statusLineElement != null) {
            statusLineElement.setShowStatus(isShowStatus());
        }
        setVersion();
    }

    @Override
    void load() {
        setVagrantPath(getOptions().getVagrantPath());
        setShowStatus(getOptions().isShowStatus());
    }

    @Override
    void reload() {
        // noop
    }

    @Override
    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    @Override
    public String getCategoryName() {
        return CATEGORY_NAME;
    }

    private VagrantOptions getOptions() {
        return VagrantOptions.getInstance();
    }

    private boolean isVagrantScript(String path) {
        return Vagrant.isVagrantScript(path, true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vagrantPathLabel = new javax.swing.JLabel();
        vagrantPathTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();
        applyButton = new javax.swing.JButton();
        noteLabel = new javax.swing.JLabel();
        learnMoreVagranLabel = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        showStatusCheckBox = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(vagrantPathLabel, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.vagrantPathLabel.text")); // NOI18N

        vagrantPathTextField.setText(org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.vagrantPathTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(searchButton, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.searchButton.text")); // NOI18N
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(applyButton, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.applyButton.text")); // NOI18N
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(noteLabel, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.noteLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(learnMoreVagranLabel, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.learnMoreVagranLabel.text")); // NOI18N
        learnMoreVagranLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                learnMoreVagranLabelMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                learnMoreVagranLabelMousePressed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(versionLabel, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.versionLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(showStatusCheckBox, org.openide.util.NbBundle.getMessage(GeneralPanel.class, "GeneralPanel.showStatusCheckBox.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(learnMoreVagranLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(vagrantPathLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(vagrantPathTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(browseButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(noteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(applyButton)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(versionLabel)
                            .addComponent(showStatusCheckBox))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vagrantPathLabel)
                    .addComponent(vagrantPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton)
                    .addComponent(searchButton)
                    .addComponent(applyButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(versionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showStatusCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(learnMoreVagranLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void learnMoreVagranLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_learnMoreVagranLabelMouseEntered
        evt.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_learnMoreVagranLabelMouseEntered

    private void learnMoreVagranLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_learnMoreVagranLabelMousePressed
        try {
            HtmlBrowser.URLDisplayer.getDefault().showURL(new URL("http://www.vagrantup.com/")); // NOI18N
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_learnMoreVagranLabelMousePressed

    @NbBundle.Messages({
        "GeneralPanel.search.scripts.title=Vagrant scripts",
        "GeneralPanel.search.scripts=&Vagrant scripts:",
        "GeneralPanel.search.scripts.pleaseWaitPart=Vagrant scripts",
        "GeneralPanel.search.scripts.notFound=No Vagrant scripts found."
    })
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String script = UiUtils.SearchWindow.search(new UiUtils.SearchWindow.SearchWindowSupport() {
            @Override
            public List<String> detect() {
                return FileUtils.findFileOnUsersPath(Vagrant.NAME, Vagrant.LONG_NAME);
            }

            @Override
            public String getWindowTitle() {
                return Bundle.GeneralPanel_search_scripts_title();
            }

            @Override
            public String getListTitle() {
                return Bundle.GeneralPanel_search_scripts();
            }

            @Override
            public String getPleaseWaitPart() {
                return Bundle.GeneralPanel_search_scripts_pleaseWaitPart();
            }

            @Override
            public String getNoItemsFound() {
                return Bundle.GeneralPanel_search_scripts_notFound();
            }
        });
        if (script != null) {
            vagrantPathTextField.setText(script);
            store();
            fireChange();
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    @NbBundle.Messages("GeneralPanel.browse.title=Select Vagrant script")
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        File vagrant = new FileChooserBuilder(GeneralPanel.class.getName() + VAGRANT_LAST_FOLDER_SUFFIX)
                .setTitle(Bundle.GeneralPanel_browse_title())
                .setFilesOnly(true)
                .showOpenDialog();
        if (vagrant != null) {
            vagrant = FileUtil.normalizeFile(vagrant);
            String vagrantPath = vagrant.getAbsolutePath();
            vagrantPathTextField.setText(vagrantPath);
            store();
            fireChange();
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        store();
        fireChange();
    }//GEN-LAST:event_applyButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyButton;
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel learnMoreVagranLabel;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JButton searchButton;
    private javax.swing.JCheckBox showStatusCheckBox;
    private javax.swing.JLabel vagrantPathLabel;
    private javax.swing.JTextField vagrantPathTextField;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables
}
