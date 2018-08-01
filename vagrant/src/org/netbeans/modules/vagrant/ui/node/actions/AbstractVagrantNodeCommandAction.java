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
package org.netbeans.modules.vagrant.ui.node.actions;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import static java.util.logging.Level.WARNING;
import java.util.logging.Logger;
import org.netbeans.modules.vagrant.api.VagrantProjectGlobal;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.ui.node.VirtualMachine;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.RequestProcessor;

abstract class AbstractVagrantNodeCommandAction extends AbstractVagrantNodeAction {

    private static final Logger LOGGER = Logger.getLogger(AbstractVagrantNodeCommandAction.class.getName());
    private static final RequestProcessor RP = new RequestProcessor(AbstractVagrantNodeCommandAction.class);
    private static final long serialVersionUID = 9170461464012621192L;

    @Override
    protected void performAction(Node[] nodes) {
        for (Node node : nodes) {
            Vagrant.setRunning(true);
            VirtualMachine virtualMachine = node.getLookup().lookup(VirtualMachine.class);
            Node parentNode = node.getParentNode();
            VagrantProjectGlobal project = null;
            if (parentNode != null) {
                project = parentNode.getLookup().lookup(VagrantProjectGlobal.class);
            }
            if (virtualMachine != null && project != null) {
                runCommand(project, virtualMachine);
            }
            break;
        }
    }

    private void runCommand(VagrantProjectGlobal project, VirtualMachine virtualMachine) {
        RP.post(() -> {
            try {
                Future<Integer> future;
                switch (getCommand()) {
                    case Vagrant.UP_COMMAND:
                        future = Vagrant.getDefault().up(project, virtualMachine);
                        break;
                    case Vagrant.SUSPEND_COMMAND:
                        future = Vagrant.getDefault().suspend(project, virtualMachine.getName());
                        break;
                    case Vagrant.RELOAD_COMMAND:
                        future = Vagrant.getDefault().reload(project, virtualMachine.getName());
                        break;
                    case Vagrant.HALT_COMMAND:
                        future = Vagrant.getDefault().halt(project, virtualMachine.getName());
                        break;
                    case Vagrant.RESUME_COMMAND:
                        future = Vagrant.getDefault().resume(project, virtualMachine.getName());
                        break;
                    case Vagrant.SHARE_COMMAND:
                        future = Vagrant.getDefault().share(project);
                        break;
                    case Vagrant.PROVISION_COMMAND:
                        future = Vagrant.getDefault().provisiton(project, virtualMachine.getName());
                        break;
                    default:
                        throw new AssertionError();
                }
                if (future != null) {
                    Integer result = future.get();
                }
            } catch (InvalidVagrantExecutableException | ExecutionException ex) {
                LOGGER.log(WARNING, null, ex);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } finally {
                Vagrant.setRunning(false);
            }
        });
    }

    @Override
    protected boolean enable(Node[] nodes) {
        return !Vagrant.isRunning();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    abstract protected String getCommand();

}
