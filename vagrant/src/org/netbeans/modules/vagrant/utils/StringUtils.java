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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.annotations.common.NonNull;

/**
 *
 * @author junichi11
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Check whether string is null or empty.
     *
     * @param string
     * @return true if null or empty, false otherwise.
     */
    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * Implode list.
     *
     * @param list list
     * @param delimiter delimiter
     * @return imploded string with delimiter
     */
    public static String implode(@NonNull List<String> list, @NonNull String delimiter) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String string : list) {
            if (!isFirst) {
                sb.append(delimiter);
            }
            sb.append(string);
            isFirst = false;
        }
        return sb.toString();
    }

    /**
     * Explode string with delimiter.
     *
     * @param target string
     * @param delimiter delimiter
     * @return exploded list with delimiter
     */
    public static List<String> explode(@NonNull String target, String delimiter) {
        if (target.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(Arrays.asList(target.split(delimiter)));
    }

    /**
     * Check whether string contains all conditions.
     *
     * @param name target string
     * @param filters strings for filltering
     * @return true if target string pass all filters, false otherwise.
     */
    public static boolean containsAll(String name, String[] filters) {
        if (filters == null) {
            return false;
        }
        for (String filter : filters) {
            if (!name.contains(filter)) {
                return false;
            }
        }
        return true;
    }
}
