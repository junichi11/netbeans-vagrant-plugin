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

import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.project.Project;
import org.netbeans.modules.vagrant.VagrantInstaller;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
@NbBundle.Messages({
    "# {0} - project name",
    "ProjectClosedAction.closed.message=Vagrant is running({0}). Do you want to run halt command?"
})
public enum ProjectClosedAction {

    NONE("none") { // NOI18N

                @Override
                void runAction(Project project, Vagrant vagrant) {
                    // noop
                }
            },
    HALT("halt") { // NOI18N

                @Override
                void runAction(Project project, Vagrant vagrant) {
                    vagrant.halt(project);
                }
            },
    HALT_ASK("halt-ask") { // NOI18N

                @Override
                void runAction(final Project project, final Vagrant vagrant) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            NotifyDescriptor.Confirmation confirmation = new NotifyDescriptor.Confirmation(
                                    Bundle.ProjectClosedAction_closed_message(project.getProjectDirectory().getName()),
                                    NotifyDescriptor.YES_NO_OPTION,
                                    NotifyDescriptor.QUESTION_MESSAGE
                            );
                            // run halt command
                            if (DialogDisplayer.getDefault().notify(confirmation) == NotifyDescriptor.YES_OPTION) {
                                vagrant.halt(project);
                            }
                        }
                    });
                }
            };

    private final String name;
    private static final Map<String, ProjectClosedAction> ENUMS = new HashMap<String, ProjectClosedAction>();

    static {
        for (ProjectClosedAction action : ProjectClosedAction.values()) {
            ENUMS.put(action.toString(), action);
        }
    }

    private ProjectClosedAction(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public void run(Project project, Vagrant vagrant) {
        if (VagrantInstaller.Status.getInstance().isShutdown()) {
            return;
        }
        runAction(project, vagrant);
    }

    abstract void runAction(Project project, Vagrant vagrant);

    @CheckForNull
    public static ProjectClosedAction toEnum(String name) {
        return ENUMS.get(name);
    }

}
