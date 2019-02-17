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
package org.netbeans.modules.vagrant.ui.node;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.vagrant.StatusLine;
import org.netbeans.modules.vagrant.VagrantStatusGlobal;
import org.netbeans.modules.vagrant.api.VagrantProjectGlobal;
import org.openide.nodes.DestroyableNodesFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;

/**
 *
 * @author junichi11
 */
public class VagrantProjectChildFactory extends DestroyableNodesFactory<VirtualMachine> implements ChangeListener {

    private final VagrantProjectGlobal project;
    // GuardebBy("this")
    private boolean loading;

    private VagrantProjectChildFactory(VagrantProjectGlobal project) {
        this.project = project;
    }

    private synchronized boolean isLoading() {
        return loading;
    }

    public static VagrantProjectChildFactory create(VagrantProjectGlobal project) {
        VagrantProjectChildFactory factory = new VagrantProjectChildFactory(project);
        VagrantStatusGlobal status = Lookup.getDefault().lookup(VagrantStatusGlobal.class);
        if (status != null) {
            status.addChangeListener(WeakListeners.create(ChangeListener.class, factory, status));
        }
        return factory;
    }

    @Override
    protected Node createNodeForKey(VirtualMachine key) {
        return new VirtualMachineNode(key);
    }

    @Override
    protected boolean createKeys(List<VirtualMachine> list) {
        List<StatusLine> statusLines = new ArrayList<>();
        VagrantStatusGlobal status = Lookup.getDefault().lookup(VagrantStatusGlobal.class);
        if (status != null) {
            statusLines.addAll(status.get(project));
            if (statusLines.isEmpty() && !isLoading()) {
                status.update(project);
                statusLines.addAll(status.get(project));
                list.add(new VirtualMachine(StatusLine.create("Loading..."), true));
                synchronized (this) {
                    loading = true;
                }
                return true;
            }
            statusLines.forEach((statusLine) -> {
                list.add(new VirtualMachine(statusLine));
            });
        }
        return true;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        refresh(false);
    }

}
