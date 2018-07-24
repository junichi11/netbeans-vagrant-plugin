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
package org.netbeans.modules.vagrant.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.modules.vagrant.preferences.VagrantPreferences;

public class RunCommandHistory implements CommandHistory {

    private final List<Command> commands = new LinkedList<>();
    // TODO add settings?
    private static final int DEFAULT_MAX_SIZE = 20;

    @Override
    public void add(Command command) {
        if (commands.contains(command)) {
            commands.remove(command);
        }
        int size = commands.size();
        int maxSize = getMaxCommandSize();
        if (size >= maxSize) {
            for (int i = maxSize - 1; i < size; i++) {
                commands.remove(i);
            }
        }
        commands.add(0, command);
    }

    public int getMaxCommandSize() {
        return DEFAULT_MAX_SIZE;
    }

    @Override
    public List<Command> getCommands() {
        return new ArrayList<>(commands);
    }

    public static class Factory {

        private static final Map<Project, RunCommandHistory> map = new HashMap<Project, RunCommandHistory>();

        public static RunCommandHistory create(Project project) {
            RunCommandHistory history = map.get(project);
            if (history == null) {
                history = VagrantPreferences.getRunCommandHistory(project);
                if (history == null) {
                    history = new RunCommandHistory();
                }
                map.put(project, history);
            }
            return history;
        }

        public static void remove(Project project) {
            if (VagrantPreferences.isSaveRunCommandHistoriesOnClose(project)) {
                save(project);
            }
            map.remove(project);
        }

        public static void save(Project project) {
            if (project == null) {
                return;
            }
            RunCommandHistory history = map.get(project);
            if (history == null) {
                return;
            }
            VagrantPreferences.setRunCommandHistory(project, history, false);
        }

        public static void saveAll() {
            for (Map.Entry<Project, RunCommandHistory> entry : map.entrySet()) {
                Project project = entry.getKey();
                RunCommandHistory history = entry.getValue();
                VagrantPreferences.setRunCommandHistory(project, history, false);
            }
        }

    }
}
