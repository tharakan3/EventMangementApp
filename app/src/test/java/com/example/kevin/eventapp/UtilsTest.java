package com.example.kevin.eventapp;

import junit.framework.Assert;

import org.junit.Test;

public class UtilsTest {

    @Test
    public void validateEmail() {
        Assert.assertEquals(Utils.validateEmail("kaushik@gmail.com"), true);
        Assert.assertEquals(Utils.validateEmail("123h0fuwrail.com"), false);
    }

    @Test
    public void validatePasswordTest(){
        Assert.assertEquals(Utils.validatePassword("wf"), false);
        Assert.assertEquals(Utils.validatePassword("abcdE124"), true);
    }

    @Test
    public void isStringNullorEmptyTest(){
        Assert.assertEquals(Utils.isStringNullorEmpty(""), true);
        Assert.assertEquals(Utils.isStringNullorEmpty(null), true);
        Assert.assertEquals(Utils.isStringNullorEmpty("randomString"), false);
    }



}