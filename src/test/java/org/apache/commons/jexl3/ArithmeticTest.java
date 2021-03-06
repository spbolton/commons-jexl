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

import org.apache.commons.jexl3.junit.Asserter;

import java.util.HashMap;
import java.util.Map;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ArithmeticTest extends JexlTestCase {
    private Asserter asserter;

    public ArithmeticTest() {
        super("ArithmeticTest");
        asserter = new Asserter(JEXL);
    }

    @Override
    public void setUp() {
    }

    public void testUndefinedVar() throws Exception {
        asserter.failExpression("objects[1].status", ".* undefined variable objects.*");
    }

    public void testLeftNullOperand() throws Exception {
        asserter.setVariable("left", null);
        asserter.setVariable("right", Integer.valueOf(8));
        asserter.setStrict(true);
        asserter.failExpression("left + right", ".*null.*");
        asserter.failExpression("left - right", ".*null.*");
        asserter.failExpression("left * right", ".*null.*");
        asserter.failExpression("left / right", ".*null.*");
        asserter.failExpression("left % right", ".*null.*");
        asserter.failExpression("left & right", ".*null.*");
        asserter.failExpression("left | right", ".*null.*");
        asserter.failExpression("left ^ right", ".*null.*");
    }

    public void testRightNullOperand() throws Exception {
        asserter.setVariable("left", Integer.valueOf(9));
        asserter.setVariable("right", null);
        asserter.failExpression("left + right", ".*null.*");
        asserter.failExpression("left - right", ".*null.*");
        asserter.failExpression("left * right", ".*null.*");
        asserter.failExpression("left / right", ".*null.*");
        asserter.failExpression("left % right", ".*null.*");
        asserter.failExpression("left & right", ".*null.*");
        asserter.failExpression("left | right", ".*null.*");
        asserter.failExpression("left ^ right", ".*null.*");
    }

    public void testNullOperands() throws Exception {
        asserter.setVariable("left", null);
        asserter.setVariable("right", null);
        asserter.failExpression("left + right", ".*null.*");
        asserter.failExpression("left - right", ".*null.*");
        asserter.failExpression("left * right", ".*null.*");
        asserter.failExpression("left / right", ".*null.*");
        asserter.failExpression("left % right", ".*null.*");
        asserter.failExpression("left & right", ".*null.*");
        asserter.failExpression("left | right", ".*null.*");
        asserter.failExpression("left ^ right", ".*null.*");
    }

    public void testNullOperand() throws Exception {
        asserter.setVariable("right", null);
        asserter.failExpression("~right", ".*null.*");
    }

    public void testBigDecimal() throws Exception {
        asserter.setVariable("left", new BigDecimal(2));
        asserter.setVariable("right", new BigDecimal(6));
        asserter.assertExpression("left + right", new BigDecimal(8));
        asserter.assertExpression("right - left", new BigDecimal(4));
        asserter.assertExpression("right * left", new BigDecimal(12));
        asserter.assertExpression("right / left", new BigDecimal(3));
        asserter.assertExpression("right % left", new BigDecimal(0));
    }

    public void testBigInteger() throws Exception {
        asserter.setVariable("left", new BigInteger("2"));
        asserter.setVariable("right", new BigInteger("6"));
        asserter.assertExpression("left + right", new BigInteger("8"));
        asserter.assertExpression("right - left", new BigInteger("4"));
        asserter.assertExpression("right * left", new BigInteger("12"));
        asserter.assertExpression("right / left", new BigInteger("3"));
        asserter.assertExpression("right % left", new BigInteger("0"));
    }

    /**
     * test some simple mathematical calculations
     */
    public void testUnaryMinus() throws Exception {
        asserter.setVariable("aByte", new Byte((byte) 1));
        asserter.setVariable("aShort", new Short((short) 2));
        asserter.setVariable("anInteger", new Integer(3));
        asserter.setVariable("aLong", new Long(4));
        asserter.setVariable("aFloat", new Float(5.5));
        asserter.setVariable("aDouble", new Double(6.6));
        asserter.setVariable("aBigInteger", new BigInteger("7"));
        asserter.setVariable("aBigDecimal", new BigDecimal("8.8"));

        asserter.assertExpression("-3", new Integer("-3"));
        asserter.assertExpression("-3.0", new Double("-3.0"));
        asserter.assertExpression("-aByte", new Byte((byte) -1));
        asserter.assertExpression("-aShort", new Short((short) -2));
        asserter.assertExpression("-anInteger", new Integer(-3));
        asserter.assertExpression("-aLong", new Long(-4));
        asserter.assertExpression("-aFloat", new Float(-5.5));
        asserter.assertExpression("-aDouble", new Double(-6.6));
        asserter.assertExpression("-aBigInteger", new BigInteger("-7"));
        asserter.assertExpression("-aBigDecimal", new BigDecimal("-8.8"));
    }

    /**
     * test some simple mathematical calculations
     */
    public void testCalculations() throws Exception {
        asserter.setStrict(true, false);
        /*
         * test new null coersion
         */
        asserter.setVariable("imanull", null);
        asserter.assertExpression("imanull + 2", new Integer(2));
        asserter.assertExpression("imanull + imanull", new Integer(0));
        asserter.setVariable("foo", new Integer(2));

        asserter.assertExpression("foo + 2", new Integer(4));
        asserter.assertExpression("3 + 3", new Integer(6));
        asserter.assertExpression("3 + 3 + foo", new Integer(8));
        asserter.assertExpression("3 * 3", new Integer(9));
        asserter.assertExpression("3 * 3 + foo", new Integer(11));
        asserter.assertExpression("3 * 3 - foo", new Integer(7));

        /*
         * test parenthesized exprs
         */
        asserter.assertExpression("(4 + 3) * 6", new Integer(42));
        asserter.assertExpression("(8 - 2) * 7", new Integer(42));

        /*
         * test some floaty stuff
         */
        asserter.assertExpression("3 * \"3.0\"", new Double(9));
        asserter.assertExpression("3 * 3.0", new Double(9));

        /*
         * test / and %
         */
        asserter.setStrict(false, false);
        asserter.assertExpression("6 / 3", new Integer(6 / 3));
        asserter.assertExpression("6.4 / 3", new Double(6.4 / 3));
        asserter.assertExpression("0 / 3", new Integer(0 / 3));
        asserter.assertExpression("3 / 0", new Double(0));
        asserter.assertExpression("4 % 3", new Integer(1));
        asserter.assertExpression("4.8 % 3", new Double(4.8 % 3));

    }

    public void testCoercions() throws Exception {
        asserter.assertExpression("1", new Integer(1)); // numerics default to Integer
        asserter.assertExpression("5L", new Long(5));

        asserter.setVariable("I2", new Integer(2));
        asserter.setVariable("L2", new Long(2));
        asserter.setVariable("L3", new Long(3));
        asserter.setVariable("B10", BigInteger.TEN);

        // Integer & Integer => Integer
        asserter.assertExpression("I2 + 2", new Integer(4));
        asserter.assertExpression("I2 * 2", new Integer(4));
        asserter.assertExpression("I2 - 2", new Integer(0));
        asserter.assertExpression("I2 / 2", new Integer(1));

        // Integer & Long => Long
        asserter.assertExpression("I2 * L2", new Long(4));
        asserter.assertExpression("I2 / L2", new Long(1));

        // Long & Long => Long
        asserter.assertExpression("L2 + 3", new Long(5));
        asserter.assertExpression("L2 + L3", new Long(5));
        asserter.assertExpression("L2 / L2", new Long(1));
        asserter.assertExpression("L2 / 2", new Long(1));

        // BigInteger
        asserter.assertExpression("B10 / 10", BigInteger.ONE);
        asserter.assertExpression("B10 / I2", new BigInteger("5"));
        asserter.assertExpression("B10 / L2", new BigInteger("5"));
    }

    // JEXL-24: long integers (and doubles)
    public void testLongLiterals() throws Exception {
        JexlEvalContext ctxt = new JexlEvalContext();
        ctxt.setStrictArithmetic(true);
        String stmt = "{a = 10L; b = 10l; c = 42.0D; d = 42.0d; e=56.3F; f=56.3f; g=63.5; h=0x10; i=010; j=0x10L; k=010l}";
        JexlScript expr = JEXL.createScript(stmt);
        /* Object value = */ expr.execute(ctxt);
        assertEquals(10L, ctxt.get("a"));
        assertEquals(10l, ctxt.get("b"));
        assertEquals(42.0D, ctxt.get("c"));
        assertEquals(42.0d, ctxt.get("d"));
        assertEquals(56.3f, ctxt.get("e"));
        assertEquals(56.3f, ctxt.get("f"));
        assertEquals(63.5d, ctxt.get("g"));
        assertEquals(0x10, ctxt.get("h"));
        assertEquals(010, ctxt.get("i"));
        assertEquals(0x10L, ctxt.get("j"));
        assertEquals(010l, ctxt.get("k"));
    }

    // JEXL-24: big integers and big decimals
    public void testBigLiterals() throws Exception {
        JexlEvalContext ctxt = new JexlEvalContext();
        ctxt.setStrictArithmetic(true);
        String stmt = "{a = 10H; b = 10h; c = 42.0B; d = 42.0b;}";
        JexlScript expr = JEXL.createScript(stmt);
        /* Object value = */ expr.execute(ctxt);
        assertEquals(new BigInteger("10"), ctxt.get("a"));
        assertEquals(new BigInteger("10"), ctxt.get("b"));
        assertEquals(new BigDecimal("42.0"), ctxt.get("c"));
        assertEquals(new BigDecimal("42.0"), ctxt.get("d"));
    }

    // JEXL-24: big decimals with exponent
    public void testBigExponentLiterals() throws Exception {
        JexlEvalContext ctxt = new JexlEvalContext();
        ctxt.setStrictArithmetic(true);
        String stmt = "{a = 42.0e1B; b = 42.0E+2B; c = 42.0e-1B; d = 42.0E-2b; e=4242.4242e1b}";
        JexlScript expr = JEXL.createScript(stmt);
        /* Object value = */ expr.execute(ctxt);
        assertEquals(new BigDecimal("42.0e+1"), ctxt.get("a"));
        assertEquals(new BigDecimal("42.0e+2"), ctxt.get("b"));
        assertEquals(new BigDecimal("42.0e-1"), ctxt.get("c"));
        assertEquals(new BigDecimal("42.0e-2"), ctxt.get("d"));
        assertEquals(new BigDecimal("4242.4242e1"), ctxt.get("e"));
    }

    // JEXL-24: doubles with exponent
    public void test2DoubleLiterals() throws Exception {
        JexlEvalContext ctxt = new JexlEvalContext();
        ctxt.setStrictArithmetic(true);
        String stmt = "{a = 42.0e1D; b = 42.0E+2D; c = 42.0e-1d; d = 42.0E-2d;}";
        JexlScript expr = JEXL.createScript(stmt);
        /* Object value = */ expr.execute(ctxt);
        assertEquals(Double.valueOf("42.0e+1"), ctxt.get("a"));
        assertEquals(Double.valueOf("42.0e+2"), ctxt.get("b"));
        assertEquals(Double.valueOf("42.0e-1"), ctxt.get("c"));
        assertEquals(Double.valueOf("42.0e-2"), ctxt.get("d"));
    }

    /**
     *
     * if silent, all arith exception return 0.0
     * if not silent, all arith exception throw
     * @throws Exception
     */
    public void testDivideByZero() throws Exception {
        Map<String, Object> vars = new HashMap<String, Object>();
        JexlEvalContext context = new JexlEvalContext(vars);
        vars.put("aByte", new Byte((byte) 1));
        vars.put("aShort", new Short((short) 2));
        vars.put("aInteger", new Integer(3));
        vars.put("aLong", new Long(4));
        vars.put("aFloat", new Float(5.5));
        vars.put("aDouble", new Double(6.6));
        vars.put("aBigInteger", new BigInteger("7"));
        vars.put("aBigDecimal", new BigDecimal("8.8"));


        vars.put("zByte", new Byte((byte) 0));
        vars.put("zShort", new Short((short) 0));
        vars.put("zInteger", new Integer(0));
        vars.put("zLong", new Long(0));
        vars.put("zFloat", new Float(0));
        vars.put("zDouble", new Double(0));
        vars.put("zBigInteger", new BigInteger("0"));
        vars.put("zBigDecimal", new BigDecimal("0"));

        String[] tnames = {
            "Byte", "Short", "Integer", "Long",
            "Float", "Double",
            "BigInteger", "BigDecimal"
        };
        // number of permutations this will generate
        final int PERMS = tnames.length * tnames.length;

        JexlEngine jexl = JEXL;
        // for non-silent, silent...
        for (int s = 0; s < 2; ++s) {
            boolean strict = Boolean.valueOf(s != 0);
            context.setStrict(true, strict);
            int zthrow = 0;
            int zeval = 0;
            // for vars of all types...
            for (String vname : tnames) {
                // for zeros of all types...
                for (String zname : tnames) {
                    // divide var by zero
                    String expr = "a" + vname + " / " + "z" + zname;
                    try {
                        JexlExpression zexpr = jexl.createExpression(expr);
                        Object nan = zexpr.evaluate(context);
                        // check we have a zero & incremement zero count
                        if (nan instanceof Number) {
                            double zero = ((Number) nan).doubleValue();
                            if (zero == 0.0) {
                                zeval += 1;
                            }
                        }
                    } catch (Exception any) {
                        // increment the exception count
                        zthrow += 1;
                    }
                }
            }
            if (strict) {
                assertTrue("All expressions should have thrown " + zthrow + "/" + PERMS,
                        zthrow == PERMS);
            } else {
                assertTrue("All expressions should have zeroed " + zeval + "/" + PERMS,
                        zeval == PERMS);
            }
        }
        debuggerCheck(jexl);
    }

    public void testNaN() throws Exception {
        Map<String, Object> ns = new HashMap<String, Object>();
        ns.put("double", Double.class);
        JexlEngine jexl = new JexlBuilder().namespaces(ns).create();
        JexlScript script;
        Object result;
        script = jexl.createScript("#NaN");
        result = script.execute(null);
        assertTrue(Double.isNaN((Double) result));
        script = jexl.createScript("NaN");
        result = script.execute(null);
        assertTrue(Double.isNaN((Double) result));
        script = jexl.createScript("double:isNaN(#NaN)");
        result = script.execute(null);
        assertTrue((Boolean) result);
        script = jexl.createScript("double:isNaN(NaN)");
        result = script.execute(null);
        assertTrue((Boolean) result);
    }

    public static class EmptyTestContext extends MapContext implements JexlContext.NamespaceResolver {
        public static int log(Object fmt, Object... arr) {
            //System.out.println(String.format(fmt.toString(), arr));
            return arr == null ? 0 : arr.length;
        }

        public static int log(Object fmt, int... arr) {
            //System.out.println(String.format(fmt.toString(), arr));
            return arr == null ? 0 : arr.length;
        }

        @Override
        public Object resolveNamespace(String name) {
            return this;
        }
    }

    public void testEmpty() throws Exception {
        Object[] SCRIPTS = {
            "var x = null; log('x = %s', x);", 0,
            "var x = 'abc'; log('x = %s', x);", 1,
            "var x = 333; log('x = %s', x);", 1,
            "var x = [1, 2]; log('x = %s', x);", 2,
            "var x = ['a', 'b']; log('x = %s', x);", 2,
            "var x = {1:'A', 2:'B'}; log('x = %s', x);", 1,
            "var x = null; return empty(x);", true,
            "var x = ''; return empty(x);", true,
            "var x = 'abc'; return empty(x);", false,
            "var x = 0; return empty(x);", true,
            "var x = 333; return empty(x);", false,
            "var x = []; return empty(x);", true,
            "var x = [1, 2]; return empty(x);", false,
            "var x = ['a', 'b']; return empty(x);", false,
            "var x = {:}; return empty(x);", true,
            "var x = {1:'A', 2:'B'}; return empty(x);", false
        };
        JexlEngine jexl = new JexlBuilder().create();
        JexlContext jc = new EmptyTestContext();
        JexlScript script;

        for (int e = 0; e < SCRIPTS.length; e += 2) {
            String stext = (String) SCRIPTS[e];
            Object expected = SCRIPTS[e + 1];
            script = jexl.createScript(stext);
            Object result = script.execute(jc);
            assertEquals("failed on " + stext, expected, result);
        }
    }

    public static class Var {
        final int value;
        Var(int v) {
            value = v;
        }
    }

    // an arithmetic that know how to subtract strings
    public static class ArithmeticPlus extends JexlArithmetic {
        public ArithmeticPlus(boolean strict) {
            super(strict);
        }
        public boolean equals(Var lhs, Var rhs) {
            return lhs.value == rhs.value;
        }
        public boolean lessThan(Var lhs, Var rhs) {
            return lhs.value < rhs.value;
        }
        public boolean lessThanOrEqual(Var lhs, Var rhs) {
            return lhs.value <= rhs.value;
        }
        public boolean greaterThan(Var lhs, Var rhs) {
            return lhs.value > rhs.value;
        }
        public boolean greaterThanOrEqual(Var lhs, Var rhs) {
            return lhs.value >= rhs.value;
        }
        public Var add(Var lhs, Var rhs) {
            return new Var(lhs.value + rhs.value);
        }
        public Var subtract(Var lhs, Var rhs) {
            return new Var(lhs.value - rhs.value);
        }
        public Var divide(Var lhs, Var rhs) {
            return new Var(lhs.value / rhs.value);
        }
        public Var multiply(Var lhs, Var rhs) {
            return new Var(lhs.value * rhs.value);
        }
        public Var mod(Var lhs, Var rhs) {
            return new Var(lhs.value / rhs.value);
        }
        public Var negate(Var arg) {
            return new Var(-arg.value);
        }
        public Var bitwiseAnd(Var lhs, Var rhs) {
            return new Var(lhs.value & rhs.value);
        }
        public Var bitwiseOr(Var lhs, Var rhs) {
            return new Var(lhs.value | rhs.value);
        }
        public Var bitwiseXor(Var lhs, Var rhs) {
            return new Var(lhs.value ^ rhs.value);
        }
        public Var bitwiseComplement(Var arg) {
            return new Var(~arg.value);
        }

        public Object subtract(String x, String y) {
            int ix = x.indexOf(y);
            if (ix < 0) {
                return x;
            }
            StringBuilder strb = new StringBuilder(x.substring(0, ix));
            strb.append(x.substring(ix + y.length()));
            return strb.toString();
        }

        public Object negate(final String str) {
            final int length = str.length();
            StringBuilder strb = new StringBuilder(str.length());
            for(int c = length - 1; c >= 0; --c) {
                strb.append(str.charAt(c));
            }
            return strb.toString();
        }
    }

    public void testArithmeticPlus() throws Exception {
        JexlEngine jexl = new JexlBuilder().cache(64).arithmetic(new ArithmeticPlus(false)).create();
        JexlContext jc = new EmptyTestContext();
        runOverload(jexl, jc);
    }

    public void testArithmeticPlusNoCache() throws Exception {
        JexlEngine jexl = new JexlBuilder().cache(0).arithmetic(new ArithmeticPlus(false)).create();
        JexlContext jc = new EmptyTestContext();
        runOverload(jexl, jc);
    }

    protected void runOverload(JexlEngine jexl,JexlContext jc) {
        JexlScript script;
        Object result;

        script = jexl.createScript("(x, y)->{ x < y }");
        result = script.execute(jc, 42, 43);
        assertEquals(true, result);
        result = script.execute(jc, new Var(42), new Var(43));
        assertEquals(true, result);
        result = script.execute(jc, new Var(42), new Var(43));
        assertEquals(true, result);
        result = script.execute(jc, 43, 42);
        assertEquals(false, result);
        result = script.execute(jc, new Var(43), new Var(42));
        assertEquals(false, result);
        result = script.execute(jc, new Var(43), new Var(42));
        assertEquals(false, result);

        script = jexl.createScript("(x, y)->{ x <= y }");
        result = script.execute(jc, 42, 43);
        assertEquals(true, result);
        result = script.execute(jc, new Var(42), new Var(43));
        assertEquals(true, result);
        result = script.execute(jc, new Var(41), new Var(44));
        assertEquals(true, result);
        result = script.execute(jc, 43, 42);
        assertEquals(false, result);
        result = script.execute(jc, new Var(45), new Var(40));
        assertEquals(false, result);
        result = script.execute(jc, new Var(46), new Var(39));
        assertEquals(false, result);

        script = jexl.createScript("(x, y)->{ x == y }");
        result = script.execute(jc, 42, 43);
        assertEquals(false, result);
        result = script.execute(jc, new Var(42), new Var(43));
        assertEquals(false, result);
        result = script.execute(jc, new Var(41), new Var(44));
        assertEquals(false, result);
        result = script.execute(jc, 43, 42);
        assertEquals(false, result);
        result = script.execute(jc, new Var(45), new Var(40));
        assertEquals(false, result);
        result = script.execute(jc, new Var(46), new Var(39));
        assertEquals(false, result);

        script = jexl.createScript("(x, y)->{ x % y }");
        result = script.execute(jc, 4242, 100);
        assertEquals(42, result);
        result = script.execute(jc, new Var(4242), new Var(100));
        assertEquals(42, ((Var) result).value);
        result = script.execute(jc, new Var(4242), new Var(100));
        assertEquals(42, ((Var) result).value);

        script = jexl.createScript("(x, y)->{ x * y }");
        result = script.execute(jc, 6, 7);
        assertEquals(42, result);
        result = script.execute(jc, new Var(6), new Var(7));
        assertEquals(42, ((Var) result).value);
        result = script.execute(jc, new Var(6), new Var(7));
        assertEquals(42, ((Var) result).value);

        script = jexl.createScript("(x, y)->{ x + y }");
        result = script.execute(jc, 35, 7);
        assertEquals(42, result);
        result = script.execute(jc, new Var(35), new Var(7));
        assertEquals(42, ((Var) result).value);
        result = script.execute(jc, new Var(35), new Var(7));
        assertEquals(42, ((Var) result).value);

        script = jexl.createScript("(x, y)->{ x - y }");
        result = script.execute(jc, 49, 7);
        assertEquals(42, result);
        result = script.execute(jc, "foobarquux", "bar");
        assertEquals("fooquux", result);
        result = script.execute(jc, 50, 8);
        assertEquals(42, result);
        result = script.execute(jc, new Var(50), new Var(8));
        assertEquals(42, ((Var) result).value);
        result = script.execute(jc, new Var(50), new Var(8));
        assertEquals(42, ((Var) result).value);

        script = jexl.createScript("(x)->{ -x }");
        result = script.execute(jc, -42);
        assertEquals(42, result);
        result = script.execute(jc, new Var(-42));
        assertEquals(42, ((Var) result).value);
        result = script.execute(jc, new Var(-42));
        assertEquals(42, ((Var) result).value);
        result = script.execute(jc, "pizza");
        assertEquals("azzip", result);
        result = script.execute(jc, -142);
        assertEquals(142, result);
    }
}