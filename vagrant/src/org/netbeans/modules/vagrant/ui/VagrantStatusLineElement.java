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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.vagrant.StatusLine;
import org.netbeans.modules.vagrant.VagrantStatus;
import org.netbeans.modules.vagrant.options.VagrantOptions;
import org.netbeans.modules.vagrant.ui.actions.VagrantAction;
import org.netbeans.modules.vagrant.ui.actions.VagrantActionMenu;
import org.netbeans.modules.vagrant.utils.FileUtils;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import org.openide.awt.StatusLineElementProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Pair;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author junichi11
 */
@ServiceProvider(service = StatusLineElementProvider.class)
public class VagrantStatusLineElement implements StatusLineElementProvider, LookupListener, ChangeListener {

    private Lookup.Result<DataObject> result = null;
    private final JLabel statusLabel = new JLabel(""); // NOI18N
    private Project project;
    private final Map<Project, String> statusCache = new HashMap<>();
    private boolean isShowStatus;
    private static final RequestProcessor RP = new RequestProcessor(VagrantStatusLineElement.class);

    public VagrantStatusLineElement() {
        // add listeners
        result = Utilities.actionsGlobalContext().lookupResult(DataObject.class);
        result.addLookupListener(this);
        statusLabel.addMouseListener(new DefaultMouseAdapter());
        isShowStatus = VagrantOptions.getInstance().isShowStatus();
    }

    @Override
    public Component getStatusLineElement() {
        return panelWithSeparator(statusLabel);
    }

    public void setShowStatus(boolean isShowStatus) {
        this.isShowStatus = isShowStatus;
    }

    /**
     * Create Component(JPanel) and add separator and JLabel to it.
     *
     * @param cell JLabel
     * @return panel
     */
    private Component panelWithSeparator(JLabel cell) {
        // create separator
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL) {
            private static final long serialVersionUID = -6385848933295984637L;

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(3, 3);
            }
        };
        separator.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        // create panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(separator, BorderLayout.WEST);
        panel.add(cell, BorderLayout.EAST);
        return panel;
    }

    @Override
    public void resultChanged(final LookupEvent lookupEvent) {
        if (!isShowStatus) {
            clearStatusLabel();
            return;
        }
        if (VagrantOptions.getInstance().getVagrantPath().isEmpty()) {
            clearStatusLabel();
            return;
        }

        // get FileObject
        FileObject fileObject = getFileObject(lookupEvent);

        // get Project
        Project currentProject = null;
        if (fileObject != null) {
            currentProject = VagrantUtils.getProject(fileObject);
        }
        if (currentProject == null) {
            currentProject = FileUtils.getProject();
        }
        if (currentProject == null) {
            clearStatusLabel();
            return;
        }

        // keep current project
        if (project == currentProject) {
            return;
        }
        project = currentProject;

        // has Vagrantfile?
        if (!VagrantUtils.hasVagrantfile(project)) {
            setStatus("not created");
            return;
        }

        // try get from cache
        String status = statusCache.get(project);
        if (status != null) {
            setStatus(status);
            return;
        }

        RP.post(() -> {
            String status1 = getStatus(project, false);
            // update view
            setStatus(status1);
        });
    }

    /**
     * Set status. Add text and icon to label.
     *
     * @param status Vagrant status
     */
    private void setStatus(final String status) {
        if (SwingUtilities.isEventDispatchThread()) {
            if (status == null) {
                clearStatusLabel();
                return;
            }
            int indexOfComma = status.indexOf(","); // NOI18N
            String statusText = status;
            if (indexOfComma != -1) {
                statusText = String.format("%s...", status.substring(0, indexOfComma)); // NOI18N
            }
            statusLabel.setText(statusText);
            statusLabel.setToolTipText(status);
            statusLabel.setIcon(VagrantUtils.getIcon(VagrantUtils.VAGRANT_ICON_16));
            return;
        }

        SwingUtilities.invokeLater(() -> {
            if (status == null) {
                clearStatusLabel();
                return;
            }
            int indexOfComma = status.indexOf(","); // NOI18N
            String statusText = status;
            if (indexOfComma != -1) {
                statusText = status.substring(0, indexOfComma);
            }
            statusLabel.setText(statusText);
            statusLabel.setToolTipText(status);
            statusLabel.setIcon(VagrantUtils.getIcon(VagrantUtils.VAGRANT_ICON_16));
        });
    }

    /**
     * Clear status label.
     */
    private void clearStatusLabel() {
        if (SwingUtilities.isEventDispatchThread()) {
            project = null;
            statusLabel.setText(""); // NOI18N
            statusLabel.setToolTipText(""); // NOI18N
            statusLabel.setIcon(null);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            project = null;
            statusLabel.setText(""); // NOI18N
            statusLabel.setToolTipText(""); // NOI18N
            statusLabel.setIcon(null);
        });
    }

    /**
     * Get status.
     *
     * @param project Project
     * @param isForce true if rerun status command, false if get status from
     * cache.
     * @return Vagrant status
     */
    private String getStatus(final Project project, boolean isForce) {
        if (project == null) {
            return null;
        }

        if (isForce) {
            final VagrantStatus vagrantStatus = Lookup.getDefault().lookup(VagrantStatus.class);
            if (vagrantStatus != null) {
                RP.execute(() -> {
                    setStatus(Bundle.VagrantStatusLineElement_reload());
                    vagrantStatus.update(project);
                });
            }
            return ""; // NOI18N
        }

        return statusCache.get(project);
    }

    /**
     * Reload status.
     */
    @NbBundle.Messages("VagrantStatusLineElement.reload=Reloading...")
    private void reloadStatus() {
        if (project == null) {
            return;
        }

        if (!VagrantUtils.hasVagrantfile(project)) {
            return;
        }
        setStatus(Bundle.VagrantStatusLineElement_reload());
        statusLabel.paintImmediately(statusLabel.getBounds());
        setStatus(getStatus(project, true));
    }

    /**
     * Get FileObject
     *
     * @param lookupEvent
     * @return current FileObject if exists, {@code null} otherwise
     */
    private FileObject getFileObject(LookupEvent lookupEvent) {
        // get DataObject
        Lookup.Result<?> lookupResult = (Lookup.Result) lookupEvent.getSource();
        Collection<?> c = lookupResult.allInstances();
        DataObject dataObject = null;
        if (!c.isEmpty()) {
            dataObject = (DataObject) c.iterator().next();
        }

        // get FileObject
        FileObject fileObject = null;
        if (dataObject != null) {
            fileObject = dataObject.getPrimaryFile();
        }
        return fileObject;
    }

    @Override
    public synchronized void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if (source instanceof VagrantStatus) {
            VagrantStatus vagrantStatus = (VagrantStatus) source;
            statusCache.clear();
            for (Pair<Project, StatusLine> status : vagrantStatus.getAll()) {
                Project p = status.first();
                StatusLine statusLine = status.second();
                String existingStatus = statusCache.get(p);
                String allStatus = statusLine.toString();
                if (existingStatus != null) {
                    allStatus = String.format("%s, %s", existingStatus, statusLine.toString()); // NOI18N
                }
                statusCache.put(p, allStatus);
            }
            if (project != null) {
                setStatus(statusCache.get(project));
            } else {
                clearStatusLabel();
            }
        }
    }

    //~ inner class
    private class DefaultMouseAdapter extends MouseAdapter {

        private final JPopupMenu popup = new JPopupMenu();

        public DefaultMouseAdapter() {
            for (VagrantAction action : VagrantActionMenu.ALL_ACTIONS) {
                popup.add(action);
            }
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            if (mouseEvent.isPopupTrigger()) {
                popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            if (mouseEvent.isPopupTrigger()) {
                popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
            }
        }

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            int clickCount = mouseEvent.getClickCount();
            // reload status
            if (clickCount == 2) {
                reloadStatus();
            }
        }
    }
}
