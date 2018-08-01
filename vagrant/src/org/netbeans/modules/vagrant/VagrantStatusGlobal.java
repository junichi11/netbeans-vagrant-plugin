/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
 */
package org.netbeans.modules.vagrant;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.vagrant.api.VagrantProjectGlobal;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.preferences.VagrantPreferences;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import static org.netbeans.modules.vagrant.utils.VagrantUtils.isVagrantAvailable;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.Pair;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = VagrantStatus.class)
public class VagrantStatusGlobal implements VagrantStatus<VagrantProjectGlobal> {

    private static final Map<VagrantProjectGlobal, List<Pair<VagrantProjectGlobal, StatusLine>>> VAGRANT_STATUS
            = new TreeMap<>((VagrantProjectGlobal p1, VagrantProjectGlobal p2) -> {
                return p1.getDisplayName().compareTo(p2.getDisplayName());
            });
    private static final RequestProcessor RP = new RequestProcessor(VagrantStatusGlobal.class);
    private static final Logger LOGGER = Logger.getLogger(VagrantStatusGlobal.class.getName());
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private final Object lock = new Object();

    @Override
    public List<Pair<VagrantProjectGlobal, StatusLine>> getAll() {
        ArrayList<Pair<VagrantProjectGlobal, StatusLine>> allList = new ArrayList<>();
        synchronized (lock) {
            VAGRANT_STATUS.values().forEach(list -> allList.addAll(list));
        }
        return allList;
    }

    @Override
    public List<StatusLine> get(VagrantProjectGlobal project) {
        ArrayList<StatusLine> allStatus = new ArrayList<>();
        synchronized (lock) {
            List<Pair<VagrantProjectGlobal, StatusLine>> statusList = VAGRANT_STATUS.get(project);
            if (statusList == null) {
                return allStatus;
            }
            statusList.forEach(status -> allStatus.add(status.second()));
        }
        return allStatus;
    }

    @Override
    public void remove(VagrantProjectGlobal project) {
        synchronized (lock) {
            VAGRANT_STATUS.remove(project);
        }
        fireChange();
    }

    @Override
    public void update(VagrantProjectGlobal project) {
        String vagrantFilePath = project.getVagrantRootPath();
        File file = new File(vagrantFilePath);
        if (!isVagrantAvailable() || !VagrantUtils.hasVagrantfile(FileUtil.toFileObject(file))) {
            return;
        }
        RP.post(() -> {
            synchronized (lock) {
                VAGRANT_STATUS.remove(project);
            }
            add(project);
            fireChange();
        });
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    @Override
    public void refresh() {
        synchronized (lock) {
            VAGRANT_STATUS.clear();
        }
        if (!isVagrantAvailable()) {
            return;
        }
        // XXX may return FeatureProjectFactory$FeatureNonProject
        // It's occurred if some projects is already opened when plugin is installed
        // Workaround: reboot NetBeans or reopen projects
        RP.post(() -> {
            Collection<? extends VagrantProjectGlobal> allPorjects = VagrantPreferences.getAllProjects();
            allPorjects.forEach(project -> add(project));
            fireChange();
        });
    }

    @Override
    public void clear() {
        synchronized (lock) {
            VAGRANT_STATUS.clear();
        }
        fireChange();
    }

    private void add(VagrantProjectGlobal project) {
        if (Vagrant.isRunning()) {
            LOGGER.log(Level.FINE, "Vagrant command is running...");
        }
        try {
            Vagrant vagrant = Vagrant.getDefault();
            Vagrant.setRunning(true);
            List<StatusLine> statusLines = vagrant.getStatusLines(project);
            Vagrant.setRunning(false);
            ArrayList<Pair<VagrantProjectGlobal, StatusLine>> list = new ArrayList<>(statusLines.size());
            statusLines.forEach(statusLine -> list.add(Pair.of(project, statusLine)));
            synchronized (lock) {
                VAGRANT_STATUS.put(project, list);
            }
        } catch (InvalidVagrantExecutableException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    private void fireChange() {
        changeSupport.fireChange();
    }

}
