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

import java.util.List;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.modules.vagrant.ui.SearchPanel;
import org.openide.util.Parameters;

/**
 * Copy from org.netbeans.modules.php.api.util.UiUtils
 *
 * @author junichi11
 */
public final class UiUtils {

    public static final String OPTIONS_PATH = "Advanced/Vagrant"; // NOI18N

    private UiUtils() {
    }

    public static void showOptions() {
        OptionsDisplayer.getDefault().open(OPTIONS_PATH);
    }

    /**
     * Utility class for searching which is done in a separate thread so the UI
     * is not blocked.
     */
    public static final class SearchWindow {

        private SearchWindow() {
        }

        /**
         * Open a serch window, start searching (in a separate thread) and
         * display the results.
         *
         * @param support {@link SearchWindowSupport search window support}
         * @return selected item (can be <code>null</code>) if user clicks OK
         * button, <code>null</code> otherwise
         */
        @CheckForNull
        public static String search(SearchWindowSupport support) {
            Parameters.notNull("support", support);

            SearchPanel panel = SearchPanel.create(support);
            if (panel.open()) {
                return panel.getSelectedItem();
            }
            return null;
        }

        public interface SearchWindowSupport {

            /**
             * Detector which runs in a separate thread and its results are
             * displayed to a user.
             *
             * @return list of search result
             */
            List<String> detect();

            /**
             * Get the title of the window.
             *
             * @return the title of the window
             */
            String getWindowTitle();

            /**
             * Get the title of the list of items.
             *
             * @return the title of the list of items
             */
            String getListTitle();

            /**
             * Get the "important" part (e.g. "PHPUnit script") of message that
             * is displayed during running of a {@link #detect() detect} method.
             *
             * @return the "important" part (e.g. "PHPUnit script") of message
             * that is displayed during running of a {@link #detect() detect}
             * method
             */
            String getPleaseWaitPart();

            /**
             * Get message that is displayed when no items are found.
             *
             * @return message that is displayed when no items are found
             */
            String getNoItemsFound();
        }
    }
}
