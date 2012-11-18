package com.ercot.java.ghost.utils;

public class GhostHash {

	public static int hashFunction(int[] k, int offset, int length, int initval){
		return Hash.lookup3(k, offset, length, initval);
	}
	
	public static java.lang.Number hashCharFunction(CharSequence s, int start, int end, long initval){
		//Changing long value to int to use 32 bit character hash function. Later on a 64-bit platform
		//can remove conversion and use 64 bit version without code change elsewhere
		return Hash.lookup3ycs(s, start, end, Integer.valueOf(Long.toString(initval)));		
	}
}
