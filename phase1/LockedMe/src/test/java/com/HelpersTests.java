package com;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class HelpersTests {

	@Test
	@Parameters({"mismatch, match, mismatch, mismatch",
				"match,match,match,mismatch",
				"match,match,match,match"})
	public void testEqualsAnyOneMatch(String frst,String scnd,String thrd,String frth) {
		var one = frst;
		var two = scnd;
		var three = thrd;
		var four = frth;
		var strToMatch = "match";
		
		var matchFound = Helpers.equalsAny(strToMatch, one,two,three,four);
		
		assertTrue(matchFound);
	}
	
	@Test
	public void testEqualsAnyNoMatchesFound() {
		var mismatch = "mismatch";
		var strToMatch = "string";
		
		var matchFound = Helpers.equalsAny(mismatch, strToMatch);
		
		assertFalse(matchFound);
	}
	
	@Test
	@Parameters({"  ",""})
	public void testIsEmpty(String emptyString) {
		var result = Helpers.isNullOrEmpty(emptyString);
		
		assertTrue(result);
	}
	
	@Test
	public void testIsNull() {
		String string = null;
		
		var result = Helpers.isNullOrEmpty(string);
		
		assertTrue(result);	
	}
	
	@Test
	public void testNotNullOrEmpty() {
		String string = "NonEmptyString";
		
		var result = Helpers.isNullOrEmpty(string);
		
		assertFalse(result);	
	}

}
