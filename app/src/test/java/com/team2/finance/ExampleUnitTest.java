package com.team2.finance;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExampleUnitTest {

    @Test
    public void isValidPassword_test() {
        assertFalse(Validation.isValidPassword("12345"));
        assertFalse(Validation.isValidPassword("123abc"));
        assertFalse(Validation.isValidPassword("123456789"));
        assertFalse(Validation.isValidPassword("12345a789"));
        assertFalse(Validation.isValidPassword("12345A789"));
        assertTrue(Validation.isValidPassword("ABC123a$$$"));
    }

    @Test
    public void isEmailValid_test() {
        assertFalse(Validation.isEmailValid("alon"));
        assertFalse(Validation.isEmailValid("alon@alon"));
        assertTrue(Validation.isEmailValid("alon@alon.com"));
    }

    @Test
    public void isAlpha_test() {
        assertTrue(Validation.isAlpha("alon"));
        assertFalse(Validation.isAlpha("al0n"));
        assertFalse(Validation.isAlpha("123"));
    }

    @Test
    public void isValidPhoneNumber_test() {
        assertFalse(Validation.isValidPhoneNumber("123456789"));
        assertTrue(Validation.isValidPhoneNumber("0523567122"));
    }

    @Test
    public void isValidEmpty() {
        assertFalse(Validation.isEmpty(""));
        assertTrue(Validation.isEmpty("text"));

    }

}