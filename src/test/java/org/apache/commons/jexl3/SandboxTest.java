/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jexl3;

import org.apache.commons.jexl3.annotations.NoJexl;
import org.apache.commons.jexl3.introspection.JexlSandbox;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 * Tests sandbox features.
 */
public class SandboxTest extends JexlTestCase {
    static final Logger LOGGER = LogManager.getLogger(SandboxTest.class.getName());

    public SandboxTest() {
        super("SandboxTest");
        JEXL.setClassLoader(getClass().getClassLoader());
    }

    @NoJexl
    public interface CantCallMe {
        void tryMe();
    }

    public interface TryCallMe {
        @NoJexl
        void tryMeARiver();
    }

    public static abstract class CallMeNot {
        public @NoJexl
        String NONO = "should not be accessible!";

        @NoJexl
        public void callMeNot() {
            throw new RuntimeException("should not be callable!");
        }
    }

    public static class Foo extends CallMeNot implements CantCallMe, TryCallMe {
        String name;
        public String alias;

        public @NoJexl Foo(String name, String notcallable) {
            throw new RuntimeException("should not be callable!");
        }

        public Foo(String name) {
            this.name = name;
            this.alias = name + "-alias";
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String Quux() {
            return name + "-quux";
        }

        @NoJexl
        public String cantCallMe() {
            throw new RuntimeException("should not be callable!");
        }

        @Override
        public void tryMe() {
            throw new RuntimeException("should not be callable!");
        }

        @Override
        public void tryMeARiver() {
            throw new RuntimeException("should not be callable!");
        }
    }

    public void testCtorBlack() throws Exception {
        String expr = "new('" + Foo.class.getName() + "', '42')";
        JexlScript script = JEXL.createScript(expr);
        Object result;
        result = script.execute(null);
        assertEquals("42", ((Foo) result).getName());

        JexlSandbox sandbox = new JexlSandbox();
        sandbox.black(Foo.class.getName()).execute("");
        JexlEngine sjexl = new JexlBuilder().sandbox(sandbox).strict(true).create();

        script = sjexl.createScript(expr);
        try {
            result = script.execute(null);
            fail("ctor should not be accessible");
        } catch (JexlException.Method xmethod) {
            // ok, ctor should not have been accessible
            LOGGER.info(xmethod.toString());
        }
    }

    public void testMethodBlack() throws Exception {
        String expr = "foo.Quux()";
        JexlScript script = JEXL.createScript(expr, "foo");
        Foo foo = new Foo("42");
        Object result;
        result = script.execute(null, foo);
        assertEquals(foo.Quux(), result);

        JexlSandbox sandbox = new JexlSandbox();
        sandbox.black(Foo.class.getName()).execute("Quux");
        JexlEngine sjexl = new JexlBuilder().sandbox(sandbox).strict(true).create();

        script = sjexl.createScript(expr, "foo");
        try {
            result = script.execute(null, foo);
            fail("Quux should not be accessible");
        } catch (JexlException.Method xmethod) {
            // ok, Quux should not have been accessible
            LOGGER.info(xmethod.toString());
        }
    }

    public void testGetBlack() throws Exception {
        String expr = "foo.alias";
        JexlScript script = JEXL.createScript(expr, "foo");
        Foo foo = new Foo("42");
        Object result;
        result = script.execute(null, foo);
        assertEquals(foo.alias, result);

        JexlSandbox sandbox = new JexlSandbox();
        sandbox.black(Foo.class.getName()).read("alias");
        JexlEngine sjexl = new JexlBuilder().sandbox(sandbox).strict(true).create();

        script = sjexl.createScript(expr, "foo");
        try {
            result = script.execute(null, foo);
            fail("alias should not be accessible");
        } catch (JexlException.Property xvar) {
            // ok, alias should not have been accessible
            LOGGER.info(xvar.toString());
        }
    }

    public void testSetBlack() throws Exception {
        String expr = "foo.alias = $0";
        JexlScript script = JEXL.createScript(expr, "foo", "$0");
        Foo foo = new Foo("42");
        Object result;
        result = script.execute(null, foo, "43");
        assertEquals("43", result);

        JexlSandbox sandbox = new JexlSandbox();
        sandbox.black(Foo.class.getName()).write("alias");
        JexlEngine sjexl = new JexlBuilder().sandbox(sandbox).strict(true).create();

        script = sjexl.createScript(expr, "foo", "$0");
        try {
            result = script.execute(null, foo, "43");
            fail("alias should not be accessible");
        } catch (JexlException.Property xvar) {
            // ok, alias should not have been accessible
            LOGGER.info(xvar.toString());
        }
    }

    public void testCtorWhite() throws Exception {
        String expr = "new('" + Foo.class.getName() + "', '42')";
        JexlScript script;
        Object result;

        JexlSandbox sandbox = new JexlSandbox();
        sandbox.white(Foo.class.getName()).execute("");
        JexlEngine sjexl = new JexlBuilder().sandbox(sandbox).strict(true).create();

        script = sjexl.createScript(expr);
        result = script.execute(null);
        assertEquals("42", ((Foo) result).getName());
    }

    public void testMethodWhite() throws Exception {
        Foo foo = new Foo("42");
        String expr = "foo.Quux()";
        JexlScript script;
        Object result;

        JexlSandbox sandbox = new JexlSandbox();
        sandbox.white(Foo.class.getName()).execute("Quux");
        JexlEngine sjexl = new JexlBuilder().sandbox(sandbox).strict(true).create();

        script = sjexl.createScript(expr, "foo");
        result = script.execute(null, foo);
        assertEquals(foo.Quux(), result);
    }

    public void testMethodNoJexl() throws Exception {
        Foo foo = new Foo("42");
        String[] exprs = {
            "foo.cantCallMe()",
            "foo.tryMe()",
            "foo.tryMeARiver()",
            "foo.callMeNot()",
            "foo.NONO",
            "new('org.apache.commons.jexl3.SandboxTest$Foo', 'one', 'two')"
        };
        JexlScript script;
        Object result;

        JexlEngine sjexl = new JexlBuilder().strict(true).create();
        for (String expr : exprs) {
            script = sjexl.createScript(expr, "foo");
            try {
                result = script.execute(null, foo);
                fail("should have not been possible");
            } catch (JexlException.Method xjm) {
                // ok
                LOGGER.info(xjm.toString());
            } catch (JexlException.Property xjm) {
                // ok
                LOGGER.info(xjm.toString());
            }
        }
    }

    public void testGetWhite() throws Exception {
        Foo foo = new Foo("42");
        String expr = "foo.alias";
        JexlScript script;
        Object result;

        JexlSandbox sandbox = new JexlSandbox();
        sandbox.white(Foo.class.getName()).read("alias");
        sandbox.get(Foo.class.getName()).read().alias("alias", "ALIAS");
        JexlEngine sjexl = new JexlBuilder().sandbox(sandbox).strict(true).create();

        script = sjexl.createScript(expr, "foo");
        result = script.execute(null, foo);
        assertEquals(foo.alias, result);

        script = sjexl.createScript("foo.ALIAS", "foo");
        result = script.execute(null, foo);
        assertEquals(foo.alias, result);
    }

    public void testSetWhite() throws Exception {
        Foo foo = new Foo("42");
        String expr = "foo.alias = $0";
        JexlScript script;
        Object result;

        JexlSandbox sandbox = new JexlSandbox();
        sandbox.white(Foo.class.getName()).write("alias");
        JexlEngine sjexl = new JexlBuilder().sandbox(sandbox).strict(true).create();

        script = sjexl.createScript(expr, "foo", "$0");
        result = script.execute(null, foo, "43");
        assertEquals("43", result);
        assertEquals("43", foo.alias);
    }

    public void testRestrict() throws Exception {
        JexlContext context = new MapContext();
        context.set("System", System.class);
        JexlSandbox sandbox = new JexlSandbox();
        // only allow call to currentTimeMillis (avoid exit, gc, loadLibrary, etc)
        sandbox.white(System.class.getName()).execute("currentTimeMillis");
        // can not create a new file
        sandbox.black(java.io.File.class.getName()).execute("");

        JexlEngine sjexl = new JexlBuilder().sandbox(sandbox).strict(true).create();

        String expr;
        JexlScript script;
        Object result;

        script = sjexl.createScript("System.exit()");
        try {
            result = script.execute(context);
            fail("should not allow calling exit!");
        } catch (JexlException xjexl) {
            LOGGER.info(xjexl.toString());
        }

        script = sjexl.createScript("new('java.io.File', '/tmp/should-not-be-created')");
        try {
            result = script.execute(context);
            fail("should not allow creating a file");
        } catch (JexlException xjexl) {
            LOGGER.info(xjexl.toString());
        }

        expr = "System.currentTimeMillis()";
        script = sjexl.createScript("System.currentTimeMillis()");
        result = script.execute(context);
        assertNotNull(result);
    }
}
