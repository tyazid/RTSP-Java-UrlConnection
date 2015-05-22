package com.net.rtsp;

import java.util.Arrays;

import com.sun.org.apache.xml.internal.utils.StringComparable;

//PJava compliance.
  class ArraysTools {
	    // Like public version, but without range checks.
	      static int compareTo(String aString,String anotherString) {
	    	 
	    	int len1 = aString.length();
	    	int len2 = anotherString.length();
	    	int n = Math.min(len1, len2);
	    	char v1[] = aString.toCharArray();
	    	char v2[] = anotherString.toCharArray();
	    	int i = 0;
	    	int j = 0;

	    	if (i == j) {
	    	    int k = i;
	    	    int lim = n + i;
	    	    while (k < lim) {
	    		char c1 = v1[k];
	    		char c2 = v2[k];
	    		if (c1 != c2) {
	    		    return c1 - c2;
	    		}
	    		k++;
	    	    }
	    	} else {
	    	    while (n-- != 0) {
	    		char c1 = v1[i++];
	    		char c2 = v2[j++];
	    		if (c1 != c2) {
	    		    return c1 - c2;
	    		}
	    	    }
	    	}
	    	return len1 - len2;
	        }
	  	/**
	  	 * Tuning parameter: list size at or below which insertion sort will be used
	  	 * in preference to mergesort or quicksort.
	  	 */
	  	private static final int INSERTIONSORT_THRESHOLD = 7;

	  	/**
	  	 * Swaps x[a] with x[b].
	  	 */
	  	private static void swap(Object[] x, int a, int b) {
	  		Object t = x[a];
	  		x[a] = x[b];
	  		x[b] = t;
	  	}

	  	private static void mergeSort(String[] src, String[] dest, int low,
	  			int high, int off) { }


 

}
