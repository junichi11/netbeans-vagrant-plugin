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

import java.io.IOException;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author junichi11
 */
public class VagrantUtilsTest extends NbTestCase {

    public VagrantUtilsTest(String name) {
        super(name);
    }

    /**
     * Test of getScriptExt method, of class VagrantUtils.
     */
    @Test
    public void testGetScriptExt() {
    }

    /**
     * Test of getIcon method, of class VagrantUtils.
     */
    @Test
    public void testGetIcon() {
    }

    /**
     * Test of boxNameSplit method, of class VagrantUtils.
     */
    @Test
    public void testBoxNameSplit() {
        String boxName = "test (virtualbox)";
        String[] expect = {"test", "virtualbox"};
        String[] result = VagrantUtils.boxNameSplit(boxName);
        assertArrayEquals(expect, result);

        boxName = "test  (virtualbox)";
        result = VagrantUtils.boxNameSplit(boxName);
        assertArrayEquals(expect, result);

        boxName = "test   (virtualbox)";
        result = VagrantUtils.boxNameSplit(boxName);
        assertArrayEquals(expect, result);

        boxName = "test";
        expect = new String[]{"test"};
        result = VagrantUtils.boxNameSplit(boxName);
        assertArrayEquals(expect, result);

        boxName = "test   (virtualbox) some";
        expect = null;
        result = VagrantUtils.boxNameSplit(boxName);
        assertArrayEquals(expect, result);

    }

    /**
     * Test of getBoxName method, of class VagrantUtils.
     */
    @Test
    public void testGetBoxName() {
        String boxName = "box-name (virtualbox)";
        String expect = "box-name";
        String result = VagrantUtils.getBoxName(boxName);
        assertEquals(expect, result);

        boxName = "box-name  (virtualbox)";
        result = VagrantUtils.getBoxName(boxName);
        assertEquals(expect, result);

        boxName = "box-name   (virtualbox)";
        result = VagrantUtils.getBoxName(boxName);
        assertEquals(expect, result);

        boxName = "box-name";
        result = VagrantUtils.getBoxName(boxName);
        assertEquals(expect, result);

        boxName = "box-name   (virtualbox) some";
        expect = null;
        result = VagrantUtils.getBoxName(boxName);
        assertEquals(expect, result);
    }

    /**
     * Test of getBoxName method, of class VagrantUtils.
     */
    @Test
    public void testGetProvider() {
        String boxName = "box-name (virtualbox)";
        String expect = "virtualbox";
        String result = VagrantUtils.getProvider(boxName);
        assertEquals(expect, result);

        boxName = "box-name  (virtualbox)";
        result = VagrantUtils.getProvider(boxName);
        assertEquals(expect, result);

        boxName = "box-name   (virtualbox)";
        result = VagrantUtils.getProvider(boxName);
        assertEquals(expect, result);

        boxName = "box-name";
        expect = null;
        result = VagrantUtils.getProvider(boxName);
        assertEquals(expect, result);

        boxName = "box-name   (virtualbox) some";
        expect = null;
        result = VagrantUtils.getProvider(boxName);
        assertEquals(expect, result);
    }

    /**
     * Test of hasVagrantfile method, of class VagrantUtils.
     */
    @Test
    public void testHasVagrantfile() throws IOException {
        FileSystem fileSystem = FileUtil.createMemoryFileSystem();
        FileObject root = fileSystem.getRoot();
        FileObject hasParent = root.createFolder("hasVagrantfile");
        FileObject vagrantfile = hasParent.createData("Vagrantfile");
        FileObject hasNotParent = root.createFolder("hasNotVagrantfile");
        FileObject hasFolderParent = root.createFolder("hasFolderVagrantfile");
        hasFolderParent.createFolder("Vagrantfile");

        assertTrue(VagrantUtils.hasVagrantfile(hasParent));
        assertFalse(VagrantUtils.hasVagrantfile(hasNotParent));
        assertFalse(VagrantUtils.hasVagrantfile(hasFolderParent));
        assertFalse(VagrantUtils.hasVagrantfile(vagrantfile));

        hasParent.delete();
        hasNotParent.delete();
        hasFolderParent.delete();
    }
}
