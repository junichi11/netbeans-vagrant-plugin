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
package org.netbeans.modules.vagrant.boxes;

import java.io.IOException;
import java.nio.charset.IllegalCharsetNameException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.netbeans.modules.vagrant.options.VagrantOptions;

/**
 *
 * @author junichi11
 */
public final class VagrantBoxesSupport {

    public static final String OFFICIAL_BOXES_URL = "https://github.com/mitchellh/vagrant/wiki/Available-Vagrant-Boxes"; // NOI18N
    public static final String COMMUNITY_BOXES_URL = "http://vagrantbox.es"; // NOI18N
    private static final Logger LOGGER = Logger.getLogger(VagrantBoxesSupport.class.getName());

    private VagrantBoxesSupport() {
    }

    /**
     * Get all boxes. (official and community)
     *
     * @return boxes.
     */
    public static List<VagrantBoxItem> getBoxes() {
        List<VagrantBoxItem> boxes = getOfficialBoxes();
        boxes.addAll(getCommunityBoxes());
        return boxes;
    }

    /**
     * Get community boxes.
     *
     * @return boxes
     */
    public static List<VagrantBoxItem> getCommunityBoxes() {
        LinkedList<VagrantBoxItem> boxes = new LinkedList<VagrantBoxItem>();
        String boxesUrl = VagrantOptions.getInstance().getBoxesUrl();
        try {
            // parse HTML
            Document doc = Jsoup.connect(boxesUrl).get();

            Elements ths = doc.select("thead th"); // NOI18N
            if (!isBoxesTable(ths)) {
                return boxes;
            }

            Elements trs = doc.select("tbody tr"); // NOI18N
            for (Element tr : trs) {
                // #22
                Elements tds = tr.select("td"); // NOI18N
                int childSize = tds.size();
                String name = childSize >= 1 ? tr.child(0).text().trim() : ""; // NOI18N
                if (name.isEmpty()) {
                    continue;
                }
                String provider = childSize >= 2 ? tr.child(1).text().trim() : ""; // NOI18N
                String url = childSize >= 3 ? tr.child(2).text().trim() : ""; // NOI18N
                String size = childSize >= 4 ? tr.child(3).text().trim() : ""; // NOI18N
                boxes.add(new VagrantBoxItem(name, provider, url, size));
            }
        } catch (IllegalCharsetNameException ex) {
            // TODO report an issue
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "connect failed:{0}", boxesUrl);
        }

        return boxes;
    }

    /**
     * Get official boxes.
     *
     * @return boxes
     */
    public static List<VagrantBoxItem> getOfficialBoxes() {
        LinkedList<VagrantBoxItem> boxes = new LinkedList<VagrantBoxItem>();
        try {
            // parse HTML
            Document doc = Jsoup.connect(OFFICIAL_BOXES_URL).get();
            Elements links = doc.select(".markdown-body a"); // NOI18N

            for (Element link : links) {
                String url = link.attr("href"); // NOI18N
                if (!url.endsWith(".box")) { // NOI18N
                    continue;
                }
                String name = "[Official] " + link.text().trim(); // NOI18N
                String provider = url.contains("vmware_fusion") ? "VMware Fusion" : "VirtualBox"; // NOI18N
                boxes.add(new VagrantBoxItem(name, provider, url, "")); // NOI18N
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "connect failed:" + OFFICIAL_BOXES_URL);
        }
        return boxes;
    }

    private static boolean isBoxesTable(Elements ths) {
        int thSize = ths.size();
        if (thSize != 4) {
            return false;
        }
        return ths.get(0).text().trim().toLowerCase().equals("name") // NOI18N
                && ths.get(1).text().trim().toLowerCase().equals("provider") // NOI18N
                && ths.get(2).text().trim().toLowerCase().equals("url") // NOI18N
                && ths.get(3).text().trim().toLowerCase().equals("size"); // NOI18N
    }
}
