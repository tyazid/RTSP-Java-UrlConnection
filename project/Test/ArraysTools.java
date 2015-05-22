 


  class ArraysTools {
	    // Like public version, but without range checks.
	      static int binarySearch(String[] a, String key) {
	    	
	    int 	fromIndex=0,toIndex= a.length;
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
		    int mid = (low + high) >>> 1; 
		    int cmp = compareTo(a[mid],key);

		    if (cmp < 0)
			low = mid + 1;
		    else if (cmp > 0)
			high = mid - 1;
		    else
			return mid; // key found
		}
		return -(low + 1);  // key not found.
	    }
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
	  			int high, int off) {
	  		int length = high - low;

	  		// Insertion sort on smallest arrays
	  		if (length < INSERTIONSORT_THRESHOLD) {
	  			for (int i = low; i < high; i++)
	  				for (int j = i; j > low
	  						&& compareTo( dest[j - 1],dest[j]) > 0; j--)
	  					swap(dest, j, j - 1);
	  			return;
	  		}

	  		// Recursively sort halves of dest into src
	  		int destLow = low;
	  		int destHigh = high;
	  		low += off;
	  		high += off;
	  		int mid = (low + high) >>> 1;
	  		mergeSort(dest, src, low, mid, -off);
	  		mergeSort(dest, src, mid, high, -off);

	  		// If list is already sorted, just copy from src to dest. This is an
	  		// optimization that results in faster sorts for nearly ordered lists.
	  		if ( compareTo(src[mid - 1],src[mid]) <= 0) {
	  			System.arraycopy(src, low, dest, destLow, length);
	  			return;
	  		}

	  		// Merge sorted halves (now in src) into dest
	  		for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
	  			if (q >= high || p < mid
	  					&&   compareTo( src[p],src[q])<= 0)
	  				dest[i] = src[p++];
	  			else
	  				dest[i] = src[q++];
	  		}
	  	}

	  	static void sort(String[] a) {
	  		String[] aux = (String[]) a.clone();
	  		mergeSort(aux, a, 0, a.length, 0);
	  	}

 

}
