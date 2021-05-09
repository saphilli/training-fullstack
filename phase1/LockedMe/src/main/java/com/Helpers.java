package com;

class Helpers {

	private Helpers() {
		throw new IllegalStateException("Helpers type cannot be instantiated");
	}
	public static boolean equalsAny(String str, String ... strings) {
		for(String s:strings) {
			if(str.equals(s)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isNullOrEmpty(String s) {
		return(s == null || s.isBlank());
	}
}
