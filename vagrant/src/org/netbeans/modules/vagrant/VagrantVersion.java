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

import org.netbeans.api.annotations.common.NonNull;

public class VagrantVersion implements Versionable {

    private final String versionNumber;
    private final int major;
    private final int minor;
    private final int revision;
    private final String unstable;

    public VagrantVersion(@NonNull String versionNumber) {
        versionNumber = versionNumber.replace("Vagrant", "").trim(); // NOI18N
        this.versionNumber = versionNumber;
        String[] splitVersion = splitVersion(versionNumber);
        String unstableVersion = null;
        int revisionNumber = -1;
        int minorNumber = -1;
        int majorNumber = -1;
        if (splitVersion.length > 3) {
            unstableVersion = splitVersion[3];
        }
        if (splitVersion.length > 2) {
            revisionNumber = Integer.parseInt(splitVersion[2]);
        }
        if (splitVersion.length > 1) {
            minorNumber = Integer.parseInt(splitVersion[1]);
        }
        if (splitVersion.length > 0) {
            majorNumber = Integer.parseInt(splitVersion[0]);
        }
        this.major = majorNumber;
        this.minor = minorNumber;
        this.revision = revisionNumber;
        this.unstable = unstableVersion;
    }

    public VagrantVersion(String versionNumber, int major, int minor, int revision, String unstable) {
        this.versionNumber = versionNumber;
        this.major = major;
        this.minor = minor;
        this.revision = revision;
        this.unstable = unstable;
    }

    @Override
    public String getVersion() {
        return versionNumber;
    }

    @Override
    public int getMajor() {
        return major;
    }

    @Override
    public int getMinor() {
        return minor;
    }

    @Override
    public int getRevision() {
        return revision;
    }

    @Override
    public String getUnstable() {
        return unstable;
    }

    private String[] splitVersion(String versionNumber) {
        return versionNumber.split("[\\. -]"); // NOI18N
    }

}
