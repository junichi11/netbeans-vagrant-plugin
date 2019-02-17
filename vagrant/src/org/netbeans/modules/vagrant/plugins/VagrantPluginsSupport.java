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
package org.netbeans.modules.vagrant.plugins;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

/**
 *
 * @author junichi11
 */
public final class VagrantPluginsSupport {

    public static final String VAGRANT_PLUGINS_URL = "https://github.com/mitchellh/vagrant/wiki/Available-Vagrant-Plugins"; // NOI18N
    private static final Logger LOGGER = Logger.getLogger(VagrantPluginsSupport.class.getName());

    private VagrantPluginsSupport() {
    }

    /**
     * Get plugins from github wiki. (see:
     * https://github.com/mitchellh/vagrant/wiki/Available-Vagrant-Plugins)
     *
     * @return plugins
     */
    public static List<VagrantPluginItem> getPlugins() {
        LinkedList<VagrantPluginItem> plugins = new LinkedList<>();
        try {
            // parse HTML
            Document doc = Jsoup.connect(VAGRANT_PLUGINS_URL).get();
            Elements body = doc.select(".markdown-body"); // NOI18N
            String category = "";
            for (Element element : body) {
                for (Element child : element.children()) {
                    if (child.tag() == Tag.valueOf("h2")) { // NOI18N
                        category = child.text();
                        continue;
                    }

                    if (child.tag() == Tag.valueOf("ul")) { // NOI18N
                        Elements lists = child.select("li"); // NOI18N
                        for (Element list : lists) {
                            Elements links = list.select("a"); // NOI18N
                            for (Element link : links) {
                                // get only first link
                                String name = link.text();
                                String url = link.attr("href"); // NOI18N
                                plugins.add(new VagrantPluginItem(name, url, category));
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "connect failed:" + VAGRANT_PLUGINS_URL);
        }
        return plugins;
    }
}
