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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.vagrant.utils.StringUtils;

/**
 *
 * @author junichi11
 */
public class StatusLine {

    private final String name;
    private final String status;
    private final String provider;
    private static final Pattern STATUS_LINE_PATTERN = Pattern.compile("\\A(?<name>.+\\s{2,})(?<status>[^ ]+.+\\s)\\((?<provider>.+)\\)\\z"); // NOI18N

    public static StatusLine create(String statusLine) {
        // format: name status (provider)
        String name = ""; // NOI18N
        String status = ""; // NOI18N
        String provider = ""; // NOI18N
        if (StringUtils.isEmpty(statusLine)) {
            return new StatusLine(name, status, provider);
        }

        Matcher matcher = STATUS_LINE_PATTERN.matcher(statusLine);
        if (matcher.find()) {
            name = matcher.group("name"); // NOI18N
            status = matcher.group("status"); // NOI18N
            provider = matcher.group("provider"); // NOI18N
            name = name == null ? "" : name.trim();
            status = status == null ? "" : status.trim();
            provider = provider == null ? "" : provider.trim();
        }
        return new StatusLine(name, status, provider);
    }

    private StatusLine(String name, String status, String provider) {
        this.name = name;
        this.status = status;
        this.provider = provider;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getProvider() {
        return provider;
    }

    @Override
    public String toString() {
        if (name.isEmpty() || status.isEmpty() || provider.isEmpty()) {
            return ""; // NOI18N
        }
        return String.format("%s %s (%s)", name, status, provider); // NOI18N
    }

    public static boolean isStatusLine(String line) {
        if (StringUtils.isEmpty(line)) {
            return false;
        }
        Matcher matcher = STATUS_LINE_PATTERN.matcher(line);
        return matcher.find();
    }

}
