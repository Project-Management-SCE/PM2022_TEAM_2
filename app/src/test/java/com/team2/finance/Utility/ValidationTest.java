package com.team2.finance.Utility;

import junit.framework.TestCase;

public class ValidationTest extends TestCase {

    public void testIsAlpha() {
        assertTrue(Validation.isAlpha("alon"));
        assertFalse(Validation.isAlpha("al0n"));
        assertFalse(Validation.isAlpha("123"));
    }

    public void testIsEmailValid() {
        assertFalse(Validation.isEmailValid("alon"));
        assertFalse(Validation.isEmailValid("alon@alon"));
        assertTrue(Validation.isEmailValid("alon@alon.com"));
    }

    public void testIsValidPassword() {
        assertFalse(Validation.isValidPassword("12345"));
        assertFalse(Validation.isValidPassword("123abc"));
        assertFalse(Validation.isValidPassword("123456789"));
        assertFalse(Validation.isValidPassword("12345a789"));
        assertFalse(Validation.isValidPassword("12345A789"));
        assertTrue(Validation.isValidPassword("ABC123a$$$"));
    }

    public void testIsValidPhoneNumber() {
        assertFalse(Validation.isValidPhoneNumber("123456789"));
        assertTrue(Validation.isValidPhoneNumber("0523567122"));
    }

    public void testIsEmpty() {
        assertFalse(Validation.isEmpty(""));
        assertTrue(Validation.isEmpty("text"));
    }
}