package com.team2.finance.Pages;

import com.google.firebase.Timestamp;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.Date;

public class CheckoutActivityTest extends TestCase {

    public void testAddDay() {

        Date date = new Date(Timestamp.now().toDate().getTime());
        date = CheckoutActivity.addDay(date, 1);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        assertEquals(date.getDay(), tomorrow.getDay());

    }

    public void testAddMonth() {

        Date date = new Date(Timestamp.now().toDate().getTime());
        date = CheckoutActivity.addMonth(date, 1);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        Date tomorrow = calendar.getTime();

        assertEquals(date.getMonth(), tomorrow.getMonth());
    }

    public void testAddYear() {

        Date date = new Date(Timestamp.now().toDate().getTime());
        date = CheckoutActivity.addYear(date, 1);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        Date tomorrow = calendar.getTime();

        assertEquals(date.getYear(), tomorrow.getYear());
    }
}