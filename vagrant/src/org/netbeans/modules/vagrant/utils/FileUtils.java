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
package org.netbeans.modules.vagrant.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.Parameters;
import org.openide.util.Utilities;

/**
 * Copy from org.netbeans.modules.php.api.util.FileUtils
 *
 * @author junichi11
 */
public final class FileUtils {

    private static final Logger LOGGER = Logger.getLogger(FileUtils.class.getName());

    private FileUtils() {
    }

    /**
     * Find all the files (absolute path) with the given "filename" on user's
     * PATH.
     * <p>
     * This method is suitable for *nix as well as windows.
     *
     * @param filename the name of a file to find.
     * @return list of absolute paths of found files.
     * @see #findFileOnUsersPath(String[])
     */
    public static List<String> findFileOnUsersPath(String filename) {
        return findFileOnUsersPath(new String[]{filename});
    }

    /**
     * Find all the files (absolute path) with the given "filename" on user's
     * PATH.
     * <p>
     * This method is suitable for *nix as well as windows.
     *
     * @param filename the name of a file to find, more names can be provided.
     * @return list of absolute paths of found files (order preserved according
     * to input names).
     * @see #findFileOnUsersPath(String)
     */
    public static List<String> findFileOnUsersPath(String... filename) {
        Parameters.notNull("filename", filename); // NOI18N

        String path = System.getenv("PATH"); // NOI18N
        LOGGER.log(Level.FINE, "PATH: [{0}]", path);
        if (path == null) {
            return Collections.<String>emptyList();
        }
        // on linux there are usually duplicities in PATH
        Set<String> dirs = new LinkedHashSet<>(Arrays.asList(path.split(File.pathSeparator)));
        LOGGER.log(Level.FINE, "PATH dirs: {0}", dirs);
        List<String> found = new ArrayList<>(dirs.size() * filename.length);
        for (String f : filename) {
            for (String d : dirs) {
                File file = new File(d, f);
                if (file.isFile()) {
                    String absolutePath = FileUtil.normalizeFile(file).getAbsolutePath();
                    LOGGER.log(Level.FINE, "File ''{0}'' found", absolutePath);
                    // not optimal but should be ok
                    if (!found.contains(absolutePath)) {
                        LOGGER.log(Level.FINE, "File ''{0}'' added to found files", absolutePath);
                        found.add(absolutePath);
                    }
                }
            }
        }
        LOGGER.log(Level.FINE, "Found files: {0}", found);
        return found;
    }

    /**
     * Get Project.
     *
     * @return current Project if exists, otherwise null
     */
    public static Project getProject() {
        Lookup context = Utilities.actionsGlobalContext();
        Project project = context.lookup(Project.class);
        if (project == null) {
            Node node = context.lookup(Node.class);
            if (node != null) {
                DataObject dataObject = node.getLookup().lookup(DataObject.class);
                if (dataObject == null) {
                    return null;
                }
                FileObject fileObject = dataObject.getPrimaryFile();
                project = FileOwnerQuery.getOwner(fileObject);
            }
        }

        return project;
    }
}
