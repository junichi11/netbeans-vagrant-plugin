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
package org.netbeans.modules.vagrant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.vagrant.api.VagrantProjectImpl;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.utils.VagrantUtils;
import static org.netbeans.modules.vagrant.utils.VagrantUtils.isVagrantAvailable;
import org.openide.util.ChangeSupport;
import org.openide.util.Pair;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.ServiceProvider;

/**
 * Implementation of VagrantStatus
 *
 * @author junichi11
 */
@ServiceProvider(service = VagrantStatus.class)
public final class VagrantStatusImpl implements VagrantStatus<Project> {

    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private static final Map<Project, List<Pair<Project, StatusLine>>> VAGRANT_STATUS
            = new TreeMap<>((Project p1, Project p2) -> {
                ProjectInformation info1 = ProjectUtils.getInformation(p1);
                ProjectInformation info2 = ProjectUtils.getInformation(p2);
                return info1.getDisplayName().compareTo(info2.getDisplayName());
    });
    private static final RequestProcessor RP = new RequestProcessor(VagrantStatusImpl.class);
    private static final Logger LOGGER = Logger.getLogger(VagrantStatusImpl.class.getName());
    private final Object lock = new Object();

    @Override
    public List<Pair<Project, StatusLine>> getAll() {
        ArrayList<Pair<Project, StatusLine>> allList = new ArrayList<>();
        synchronized (lock) {
            for (List<Pair<Project, StatusLine>> list : VAGRANT_STATUS.values()) {
                allList.addAll(list);
            }
        }

        return allList;
    }

    @Override
    public List<StatusLine> get(Project project) {
        ArrayList<StatusLine> allStatus = new ArrayList<>();
        synchronized (lock) {
            List<Pair<Project, StatusLine>> statusList = VAGRANT_STATUS.get(project);
            if (statusList == null) {
                return allStatus;
            }
            for (Pair<Project, StatusLine> status : statusList) {
                allStatus.add(status.second());
            }
        }
        return allStatus;
    }

    private void add(Project project) {
        if (Vagrant.isRunning()) {
            LOGGER.log(Level.FINE, "Vagrant command is running...");
        }
        try {
            Vagrant vagrant = Vagrant.getDefault();
            Vagrant.setRunning(true);
            List<StatusLine> statusLines = vagrant.getStatusLines(VagrantProjectImpl.create(project));
            Vagrant.setRunning(false);
            ArrayList<Pair<Project, StatusLine>> list = new ArrayList<>(statusLines.size());
            for (StatusLine statusLine : statusLines) {
                Pair<Project, StatusLine> pair = Pair.of(project, statusLine);
                list.add(pair);
            }
            synchronized (lock) {
                VAGRANT_STATUS.put(project, list);
            }
        } catch (InvalidVagrantExecutableException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    @Override
    public void remove(Project project) {
        synchronized (lock) {
            VAGRANT_STATUS.remove(project);
        }
        fireChange();
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
            OpenProjects projects = OpenProjects.getDefault();
            for (Project project : projects.getOpenProjects()) {
                add(project);
            }
            fireChange();
        });
    }

    @Override
    public void update(final Project project) {
        if (!isVagrantAvailable() || !VagrantUtils.hasVagrantfile(project)) {
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
    public void clear() {
        synchronized (lock) {
            VAGRANT_STATUS.clear();
        }
        fireChange();
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    private void fireChange() {
        changeSupport.fireChange();
    }

}
