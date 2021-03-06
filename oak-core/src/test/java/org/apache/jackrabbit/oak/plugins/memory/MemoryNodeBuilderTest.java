/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.oak.plugins.memory;

import org.apache.jackrabbit.oak.spi.state.NodeBuilder;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.apache.jackrabbit.oak.api.Type.STRING;
import static org.apache.jackrabbit.oak.plugins.memory.EmptyNodeState.EMPTY_NODE;

public class MemoryNodeBuilderTest {

    private NodeState base;

    @Before
    public void setUp() {
        NodeBuilder builder = EMPTY_NODE.builder();
        builder.setProperty("a", 1L);
        builder.setProperty("b", 2L);
        builder.setProperty("c", 3L);
        builder.child("x").child("q");
        builder.child("y");
        builder.child("z");
        base = builder.getNodeState();
    }

    @Test
    public void testConnectOnAddProperty() {
        NodeBuilder root = base.builder();
        NodeBuilder childA = root.child("x");
        NodeBuilder childB = root.child("x");

        assertNull(childA.getProperty("test"));
        childB.setProperty("test", "foo");
        assertNotNull(childA.getProperty("test"));
    }

    @Test
    public void testConnectOnUpdateProperty() {
        NodeBuilder root = base.builder();
        NodeBuilder childA = root.child("x");
        NodeBuilder childB = root.child("x");

        childB.setProperty("test", "foo");

        childA.setProperty("test", "bar");
        assertEquals("bar", childA.getProperty("test").getValue(STRING));
        assertEquals("bar", childB.getProperty("test").getValue(STRING));
    }

    @Test
    public void testConnectOnRemoveProperty() {
        NodeBuilder root = base.builder();
        NodeBuilder childA = root.child("x");
        NodeBuilder childB = root.child("x");

        childB.setProperty("test", "foo");

        childA.removeProperty("test");
        assertNull(childA.getProperty("test"));
        assertNull(childB.getProperty("test"));

        childA.setProperty("test", "bar");
        assertEquals("bar", childA.getProperty("test").getValue(STRING));
        assertEquals("bar", childB.getProperty("test").getValue(STRING));
    }

    @Test
    public void testConnectOnAddNode() {
        NodeBuilder root = base.builder();
        NodeBuilder childA = root.child("x");
        NodeBuilder childB = root.child("x");

        assertFalse(childA.hasChildNode("test"));
        assertFalse(childB.hasChildNode("test"));

        childB.child("test");
        assertTrue(childA.hasChildNode("test"));
        assertTrue(childB.hasChildNode("test"));
    }

    @Test
    public void testReadOnRemoveNode() {
        for (String name : new String[] {"x", "new"}) {
            NodeBuilder root = base.builder();
            NodeBuilder child = root.child(name);

            root.removeNode(name);
            try {
                child.getChildNodeCount();
                fail();
            } catch (IllegalStateException e) {
                // expected
            }

            root.child(name);
            assertEquals(0, child.getChildNodeCount()); // reconnect!
        }
    }

    @Test
    public void testWriteOnRemoveNode() {
        for (String name : new String[] {"x", "new"}) {
            NodeBuilder root = base.builder();
            NodeBuilder child = root.child(name);

            root.removeNode(name);
            try {
                child.setProperty("q", "w");
                fail();
            } catch (IllegalStateException e) {
                // expected
            }

            root.child(name);
            assertEquals(0, child.getChildNodeCount()); // reconnect!
        }
    }

    @Test
    public void testAddRemovedNodeAgain() {
        NodeBuilder root = base.builder();

        root.removeNode("x");
        NodeBuilder x = root.child("x");

        x.child("q");
        assertTrue(x.hasChildNode("q"));
    }

    @Test
    public void testReset() {
        NodeBuilder root = base.builder();
        NodeBuilder child = root.child("x");
        child.child("new");

        assertTrue(child.hasChildNode("new"));
        assertTrue(root.child("x").hasChildNode("new"));

        root.reset(base);
        assertFalse(child.hasChildNode("new"));
        assertFalse(root.child("x").hasChildNode("new"));
    }

    @Test
    public void testReset2() {
        NodeBuilder root = base.builder();
        NodeBuilder x = root.child("x");
        x.child("y");

        root.reset(base);
        assertTrue(root.hasChildNode("x"));
        assertFalse(x.hasChildNode("y"));
    }

    @Test
    public void testUnmodifiedEqualsBase() {
        NodeBuilder root = base.builder();
        NodeBuilder x = root.child("x");
        assertEquals(x.getBaseState(), x.getNodeState());
    }

    @Test(expected = IllegalStateException.class)
    public void testReadOnRemovedNode() {
        NodeBuilder root = base.builder();
        NodeBuilder m = root.child("m");
        NodeBuilder n = m.child("n");

        root.removeNode("m");
        n.hasChildNode("any");
    }

}
