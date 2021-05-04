package app;

class Helpers {

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
