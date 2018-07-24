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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.vagrant.StatusLine;
import org.netbeans.modules.vagrant.VagrantStatus;
import org.netbeans.modules.vagrant.command.InvalidVagrantExecutableException;
import org.netbeans.modules.vagrant.command.RunCommandHistory;
import org.netbeans.modules.vagrant.command.Vagrant;
import org.netbeans.modules.vagrant.options.VagrantOptions;
import org.netbeans.modules.vagrant.preferences.VagrantPreferences;
import org.netbeans.modules.vagrant.utils.StringUtils;
import org.netbeans.spi.project.LookupProvider;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 * VagrantLookupProvider
 *
 * @author junichi11
 */
@LookupProvider.Registration(projectType = {
    "org-netbeans-modules-j2ee-clientproject",
    "org-netbeans-modules-j2ee-earproject",
    "org-netbeans-modules-j2ee-ejbjarproject",
    "org-netbeans-modules-java-j2seproject",
    "org-netbeans-modules-php-project",
    "org-netbeans-modules-web-project",
    "org.netbeans.modules.web.clientproject",
    "org-netbeans-modules-web-clientproject",
    "org-netbeans-modules-ruby-rubyproject",
    "org-netbeans-modules-ruby-railsprojects",
    "com-tropyx-nb_puppet"
})
public class VagrantLookupProvider implements LookupProvider {

    private static final Logger LOGGER = Logger.getLogger(VagrantLookupProvider.class.getName());
    private static final RequestProcessor RP = new RequestProcessor(VagrantLookupProvider.class);

    @Override
    public Lookup createAdditionalLookup(Lookup lookup) {
        final Project project = lookup.lookup(Project.class);
        return Lookups.fixed(new ProjectOpenedHook() {

            @Override
            protected void projectOpened() {
                final VagrantStatus vagrantStatus = getVagrantStatus();
                if (vagrantStatus != null) {
                    RP.execute(() -> {
                        vagrantStatus.update(project);
                    });
                }
            }

            @Override
            protected void projectClosed() {
                VagrantOptions options = VagrantOptions.getInstance();
                if (project == null || StringUtils.isEmpty(options.getVagrantPath())) {
                    return;
                }
                VagrantStatus vagrantStatus = getVagrantStatus();
                try {
                    if (vagrantStatus != null) {
                        // use cache
                        // Vagrant environment may be broken if anoter command is run
                        // when Vagrant.getStatusLines() is running
                        List<StatusLine> statusLines = vagrantStatus.get(project);
                        Vagrant vagrant = Vagrant.getDefault();
                        // status confirmation
                        for (StatusLine statusLine : statusLines) {
                            if (statusLine.getStatus().contains("running")) { // NOI18N
                                ProjectClosedAction closedAction = VagrantPreferences.getProjectClosedAction(project);
                                closedAction.run(project, vagrant);
                                break;
                            }
                        }
                    }
                } catch (InvalidVagrantExecutableException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage());
                }

                // remove status
                if (vagrantStatus != null) {
                    vagrantStatus.remove(project);
                }

                // remove command history
                RunCommandHistory.Factory.remove(project);
            }

            private VagrantStatus getVagrantStatus() {
                Lookup lookup = Lookup.getDefault();
                return lookup.lookup(VagrantStatus.class);
            }
        });
    }

}
