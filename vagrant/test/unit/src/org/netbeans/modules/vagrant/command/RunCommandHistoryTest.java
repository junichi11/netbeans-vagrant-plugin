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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;

/**
 *
 * @author junichi11
 */
public class RunCommandHistoryTest extends NbTestCase {

    public RunCommandHistoryTest(String name) {
        super(name);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    @Override
    public void setUp() {
    }

    @After
    @Override
    public void tearDown() {
    }

    /**
     * Test of add method, of class RunCommandHistory.
     */
    @Test
    public void testAddForMaxCommandSize() {
        RunCommandHistory history = new RunCommandHistory();
        int maxCommandSize = history.getMaxCommandSize();

        // add commands until max size
        for (int i = 0; i < maxCommandSize; i++) {
            history.add(new CommandHistory.Command("command" + i, Collections.<String>emptyList()));
        }
        List<CommandHistory.Command> commands = history.getCommands();
        assertEquals(maxCommandSize, commands.size());

        // add commands until max size - 1
        history = new RunCommandHistory();
        for (int i = 0; i < maxCommandSize - 1; i++) {
            history.add(new CommandHistory.Command("command" + i, Collections.<String>emptyList()));
        }
        commands = history.getCommands();
        assertEquals(maxCommandSize - 1, commands.size());

        // add commands until max size + 5
        history = new RunCommandHistory();
        for (int i = 0; i < maxCommandSize + 5; i++) {
            history.add(new CommandHistory.Command("command" + i, Collections.<String>emptyList()));
        }
        commands = history.getCommands();
        assertEquals(maxCommandSize, commands.size());
    }

    /**
     * Test of add method, of class RunCommandHistory.
     */
    @Test
    public void testAddForSameCommand() {
        RunCommandHistory history = new RunCommandHistory();
        history.add(new CommandHistory.Command("up", Collections.<String>emptyList()));
        history.add(new CommandHistory.Command("up", Collections.<String>emptyList()));
        history.add(new CommandHistory.Command("up", Arrays.asList("default")));

        assertEquals(2, history.getCommands().size());
    }

    /**
     * Test of add method, of class RunCommandHistory.
     */
    @Test
    public void testAddForOrdering() {
        RunCommandHistory history = new RunCommandHistory();
        history.add(new CommandHistory.Command("halt", Collections.<String>emptyList()));
        history.add(new CommandHistory.Command("up", Arrays.asList("default")));
        history.add(new CommandHistory.Command("resume", Collections.<String>emptyList()));
        history.add(new CommandHistory.Command("up", Arrays.asList("default2")));
        history.add(new CommandHistory.Command("up", Arrays.asList("default")));

        assertEquals(4, history.getCommands().size());
        assertEquals(new CommandHistory.Command("up", Arrays.asList("default")), history.getCommands().get(0));
    }

}
