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

import javax.swing.ImageIcon;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;

/**
 *
 * @author junichi11
 */
public class VagrantUtils {

    private static final String RESOURCES_PATH = "org/netbeans/modules/vagrant/resources/"; // NOI18N
    public static final String VAGRANT_ICON_12 = RESOURCES_PATH + "logo_12.png"; // NOI18N
    public static final String VAGRANT_ICON_16 = RESOURCES_PATH + "logo.png"; // NOI18N
    public static final String VAGRANT_ICON_24 = RESOURCES_PATH + "logo_24.png"; // NOI18N
    public static final String VAGRANT_ICON_32 = RESOURCES_PATH + "logo_32.png"; // NOI18N
    public static final String UP_ICON_16 = RESOURCES_PATH + "up.png"; // NOI18N
    public static final String HALT_ICON_16 = RESOURCES_PATH + "halt.png"; // NOI18N
    public static final String RELOAD_ICON_16 = RESOURCES_PATH + "reload.png"; // NOI18N
    public static final String SUSPEND_ICON_16 = RESOURCES_PATH + "suspend.png"; // NOI18N
    public static final String RESUME_ICON_16 = RESOURCES_PATH + "resume.png"; // NOI18N
    public static final String DESTROY_ICON_16 = RESOURCES_PATH + "destroy.png"; // NOI18N
    public static final String STATUS_ICON_16 = RESOURCES_PATH + "status.png"; // NOI18N
    public static final String SHARE_ICON_16 = RESOURCES_PATH + "share.png"; // NOI18N
    public static final String SSH_ICON_16 = RESOURCES_PATH + "ssh.png"; // NOI18N
    public static final String INIT_ICON_16 = RESOURCES_PATH + "init.png"; // NOI18N
    public static final String PROVISION_ICON_16 = RESOURCES_PATH + "provision.png"; // NOI18N
    public static final String OPTIONS_ICON_16 = RESOURCES_PATH + "options.png"; // NOI18N
    public static final String RUN_COMMAND_ICON_16 = RESOURCES_PATH + "run_command.png"; // NOI18N
    public static final String VAGRANTFILE = "Vagrantfile"; // NOI18N

    private VagrantUtils() {
    }

    public static void showWarnigDialog(String message) {
        NotifyDescriptor.Message descriptor = new NotifyDescriptor.Message(message, NotifyDescriptor.WARNING_MESSAGE);
        DialogDisplayer.getDefault().notify(descriptor);
    }

    /**
     * Get script extension. Return .exe if OS is Windows, otherwise return .sh.
     *
     * @return extension
     */
    public static String getScriptExt() {
        if (Utilities.isWindows()) {
            return ".exe"; // NOI18N
        }
        return ".sh"; // NOI18N
    }

    /**
     * Get IconImage.
     *
     * @param path icon path.
     * @return icon
     */
    public static ImageIcon getIcon(String path) {
        return ImageUtilities.loadImageIcon(path, true);
    }

    /**
     * Get box data.
     *
     * @param boxName full box name
     * @return box data as array
     */
    public static String[] boxNameSplit(String boxName) {
        boxName = boxName.trim();
        boxName = boxName.replaceAll(" +", " "); // NOI18N
        String[] split = boxName.split(" "); // NOI18N
        if (split.length >= 3) {
            return null;
        }

        if (split.length == 2) {
            String provider = split[1];
            split[1] = provider.replaceAll("[()]", ""); // NOI18N
        }
        return split;
    }

    /**
     * Get box name from full box data.
     *
     * @param boxName full box data e.g. "boxname (virtualbox)"
     * @return box name
     */
    public static String getBoxName(String boxName) {
        String[] boxNameSplit = boxNameSplit(boxName);
        if (boxNameSplit == null) {
            return null;
        }
        return boxNameSplit[0];
    }

    /**
     * Get box provider from full box data.
     *
     * @param boxName full box data e.g. "boxname (virtualbox)"
     * @return box provider
     */
    public static String getProvider(String boxName) {
        String[] boxNameSplit = boxNameSplit(boxName);
        if (boxNameSplit == null || boxNameSplit.length != 2) {
            return null;
        }
        return boxNameSplit[1];
    }

    /**
     * Check whether folder has the Vagrantfile.
     *
     * @param fileObject
     * @return true if foler has the Vagrantfile, false otherwise.
     */
    public static boolean hasVagrantfile(FileObject fileObject) {
        if (fileObject == null || !fileObject.isFolder()) {
            return false;
        }
        FileObject vagrantfile = fileObject.getFileObject(VAGRANTFILE);
        // #7 casing doesn't matter
        if (vagrantfile == null) {
            FileObject[] children = fileObject.getChildren();
            for (FileObject child : children) {
                if (child.isFolder()) {
                    continue;
                }
                String nameExt = child.getNameExt();
                if (VAGRANTFILE.toLowerCase().equals(nameExt.toLowerCase())) {
                    return true;
                }
            }
            vagrantfile = fileObject.getFileObject(VAGRANTFILE.toLowerCase());
        }

        if (vagrantfile == null) {
            return false;
        }

        return !vagrantfile.isFolder();
    }

    /**
     * Get Project from FileObject.
     *
     * @param target FileObject
     * @return project
     */
    public static Project getProject(FileObject target) {
        if (target == null) {
            return null;
        }
        return FileOwnerQuery.getOwner(target);
    }
}
