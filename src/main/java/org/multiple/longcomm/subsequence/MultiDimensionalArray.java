package org.multiple.longcomm.subsequence;

import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.lang.Iterable;
import java.util.NoSuchElementException;

import javax.naming.SizeLimitExceededException;

public class MultiDimensionalArray<T> {
    int dimensions = 0;
    List<Integer> dimensionSizes = new ArrayList<>();
    T t;
    int maxAllowedSize = 20000;
    int maxAllowedDimensions = 6;

    // TODO : need to move this data store to Infinispan
    Map<List<Integer>, T> dimensionSpace = new HashMap<List<Integer>, T>();

    public IndexIterator<T> reverseIndexIterator = null;
    public IndexIterator<T> forwardIndexIterator = null;

    public class IndexIterator<T> implements Iterable<List<Integer>> {

        MultiDimensionalArray<T> mda = null;
        T t;
        Boolean reverse;

        public IndexIterator(T t, MultiDimensionalArray mda, Boolean reverse) {
            this.mda = mda;
            this.reverse = reverse;
        }

        @Override
        public Iterator<List<Integer>> iterator() {
            if (!reverse) {
                ForwardIterator<T> myIterator = new ForwardIterator<T>(this.t, this.mda);
                return myIterator;
            } else {
                BackwardsIterator<T> myIterator = new BackwardsIterator<T>(this.t, this.mda);
                return myIterator;
            }
        }

        public class BackwardsIterator<E> implements Iterator<List<Integer>> {

            List<Integer> idx = new ArrayList<>();
            List<Integer> min = new ArrayList<>();
            MultiDimensionalArray<E> mda = null;
            Boolean reachedZero = false;


            public BackwardsIterator(E e, MultiDimensionalArray<E> incommingmda) {
                this.mda = incommingmda;
                
                for(int i = 0; i < this.mda.dimensionSizes.size(); i++) {
                    idx.add(dimensionSizes.get(i));
                    min.add(0);
                }    
            }

            @Override
            public boolean hasNext() {
                return !reachedZero;
            }

            @Override
            public List<Integer> next() throws NoSuchElementException {
                if (reachedZero) {
                    throw new NoSuchElementException();
                }

                List<Integer> next = new ArrayList<>();
                for(int i: idx) {
                    next.add(i);
                }
                if (idx.equals(min)) {
                    reachedZero = true;
                }
        
                Boolean iteratedOneDigit = false;
                for (int digit = dimensionSizes.size() - 1; digit >= 0; digit-- ){
                    if (!iteratedOneDigit) {
                        if (idx.get(digit) > 0) {
                            idx.set(digit, idx.get(digit)-1);
                            for(int remainder = digit+1; remainder < dimensionSizes.size(); remainder++) {
                                idx.set(remainder, dimensionSizes.get(remainder));
                            }    
                            iteratedOneDigit = true;
                        }
                    }
                }

                return next;
            }
        }
        public class ForwardIterator<E> implements Iterator<List<Integer>> {

            List<Integer> idx = new ArrayList<>();
            List<Integer> max = new ArrayList<>();
            MultiDimensionalArray<E> mda = null;
            Boolean reachedMax = false;


            public ForwardIterator(E e, MultiDimensionalArray<E> incommingmda) {
                this.mda = incommingmda;
                
                for(int i = 0; i < this.mda.dimensionSizes.size(); i++) {
                    idx.add(0);
                    max.add(dimensionSizes.get(i));
                }    
            }

            @Override
            public boolean hasNext() {
                return !reachedMax;
            }

            @Override
            public List<Integer> next() throws NoSuchElementException {
                if (reachedMax) {
                    throw new NoSuchElementException();
                }

                List<Integer> next = new ArrayList<>();
                for(int i: idx) {
                    next.add(i);
                }
                if (idx.equals(max)) {
                    reachedMax = true;
                }
        
                Boolean iteratedOneDigit = false;
                for (int digit = dimensionSizes.size() - 1; digit >= 0; digit-- ){
                    if (!iteratedOneDigit) {
                        if (idx.get(digit) < dimensionSizes.get(digit)) {
                            idx.set(digit, idx.get(digit)+1);
                            for(int remainder = digit+1; remainder < dimensionSizes.size(); remainder++) {
                                idx.set(remainder, 0);
                            }    
                            iteratedOneDigit = true;
                        }
                    }
                }

                return next;
            }
        }


    }

    MultiDimensionalArray(T t, int[] sizes) throws SizeLimitExceededException {
        this.dimensions = sizes.length;
        this.dimensionSizes = new ArrayList<Integer>();
        int maxSizeCheck = 0;
        for (int val : sizes) {
            dimensionSizes.add(val);
            maxSizeCheck *= val;
        }
        if (dimensionSizes.size() > maxAllowedDimensions) {
            throw new SizeLimitExceededException("Required set dimensions is " + dimensionSizes.size() + ". Cannot handle sets of size larger than " + maxAllowedDimensions + ".");
        }
        if (maxSizeCheck > maxAllowedSize) {
            throw new SizeLimitExceededException("Required memory set is " + maxSizeCheck + ". Cannot handle sets of size larger than " + maxAllowedSize + ".");
        }
        this.t = t;

        List<List<Integer>> workingIdxSet = new ArrayList<List<Integer>>();
        for(int i = sizes.length-1; i >= 0; i--){

            List<List<Integer>> newWorkingSet = new ArrayList<List<Integer>>();
            for(int j = 0; j < sizes[i] + 1; j++) {
                if (workingIdxSet.size() > 0) {
                    for(List<Integer> oneWorkingIdx : workingIdxSet) {
                        List<Integer> alist = new ArrayList<Integer>();
                        alist.add(Integer.valueOf(j));
                        alist.addAll(oneWorkingIdx);
                        newWorkingSet.add(alist);
                    }
                } else {
                    List<Integer> aList = new ArrayList<Integer>();
                    aList.add(Integer.valueOf(j));
                    newWorkingSet.add(aList);
                }
            };
            workingIdxSet = newWorkingSet;                
        }

        for(List<Integer> idx : workingIdxSet) {
            dimensionSpace.put(idx, t);
        }
    }

    MultiDimensionalArray(T t, List<Integer> sizes) throws SizeLimitExceededException {
        this.dimensions = sizes.size();
        this.dimensionSizes = new ArrayList<Integer>();
        int maxSizeCheck = 0;
        this.reverseIndexIterator = new IndexIterator(t, this, true);
        this.forwardIndexIterator = new IndexIterator(t, this, false);

        for (int val : sizes) {
            dimensionSizes.add(val);
            maxSizeCheck *= val;
        }
        if (dimensionSizes.size() > maxAllowedDimensions) {
            throw new SizeLimitExceededException("Required set dimensions is " + dimensionSizes.size() + ". Cannot handle sets of size larger than " + maxAllowedDimensions + ".");
        }
        if (maxSizeCheck > maxAllowedSize) {
            throw new SizeLimitExceededException("Required memory set is " + maxSizeCheck + ". Cannot handle sets of size larger than " + maxAllowedSize + ".");
        }
        // System.out.println(dimensionSizes.toString());
        this.t = t;

        List<List<Integer>> workingIdxSet = new ArrayList<List<Integer>>();
        for(int i = sizes.size()-1; i >= 0; i--){

            List<List<Integer>> newWorkingSet = new ArrayList<List<Integer>>();
            for(int j = 0; j < sizes.get(i) + 1; j++) {
                if (workingIdxSet.size() > 0) {
                    for(List<Integer> oneWorkingIdx : workingIdxSet) {
                        List<Integer> alist = new ArrayList<Integer>();
                        alist.add(Integer.valueOf(j));
                        alist.addAll(oneWorkingIdx);
                        newWorkingSet.add(alist);
                    }
                } else {
                    List<Integer> aList = new ArrayList<Integer>();
                    aList.add(Integer.valueOf(j));
                    newWorkingSet.add(aList);
                }
            };
            workingIdxSet = newWorkingSet;                
        }

        for(List<Integer> idx : workingIdxSet) {
            dimensionSpace.put(idx, t);
        }
        // for(Map.Entry<List<Integer>, T> kvp : dimensionSpace.entrySet()) {
        //     System.out.println(kvp.getKey().toString() + " : " + kvp.getValue().toString());
        // }
    }

    public T getData(List<Integer> idx) throws KeyException {
        if (idx.size() != dimensions) {
            throw new KeyException("Incorrect dimensionality. Expected: " + dimensions + " got: " + idx.size());
        }

        return this.t;
    }

    public T getData(int[] idx) throws KeyException {
        if (idx.length != dimensions) {
            throw new KeyException("Incorrect dimensionality. Expected: " + dimensions + " got: " + idx.length);
        }

        return this.t;
    }

    public List<Integer> firstIndex() {
        List<Integer> idx = new ArrayList<Integer>();
        for(Integer d : dimensionSizes) {
            idx.add(0);
        }
        return idx;
    }

    public List<Integer> lastIndex() {
        List<Integer> idx = new ArrayList<Integer>();
        for(Integer d : dimensionSizes) {
            idx.add(d);
        }
        return idx;
    }

    public String toString() {
        String output = "";
        String tabChar = " ";
        String gridDelimiter = " ";

        List<Integer> truncatedIdx = new ArrayList<Integer>();
        for(int i = 0; i < dimensionSizes.size()-2; i++) {
            truncatedIdx.add(-1);
        }

        Integer d2idx = -1;
        String gridRow = "";
        for(List<Integer> idx : forwardIndexIterator) {
            List<Integer> idxTrunk = new ArrayList<Integer>();
            if (idx.size() > 2) {
                for (int i = 0; i < idx.size()-2; i++) {
                    idxTrunk.add(idx.get(i));
                }
            }
            Boolean lastTwoDimensions = (idx.size() == 2 || idxTrunk.equals(truncatedIdx));

            if (!lastTwoDimensions) {
                Boolean advancedHigherDimensions = false;
                String hlevel = "";
                for(int tdi = 0; tdi < idxTrunk.size(); tdi++) {
                    if (!idxTrunk.get(tdi).equals(truncatedIdx.get(tdi))) {
                        hlevel += new String(new char[tdi]).replace("\0", tabChar) + idxTrunk.get(tdi) + "\n";
                        advancedHigherDimensions = true;
                    }
                }
                if (advancedHigherDimensions) {
                    truncatedIdx = idxTrunk;

                    output += gridRow + "\n";
                    gridRow = new String(new char[truncatedIdx.size()]).replace("\0", tabChar) + dimensionSpace.get(idx).toString();
                    d2idx = idx.get(idx.size()-2);
                    output += hlevel;
                    hlevel = "";
                }
            } else {
                if (d2idx != idx.get(idx.size()-2)) {
                    output += gridRow + "\n";
                    gridRow = new String(new char[truncatedIdx.size()]).replace("\0", tabChar) + dimensionSpace.get(idx).toString();
                    d2idx = idx.get(idx.size()-2);
                } else {
                    if (gridRow.length() > 0) {
                        gridRow += gridDelimiter;
                    }
                    gridRow += dimensionSpace.get(idx).toString();
                }
            }
        }
        output += gridRow + "\n";



        return output;
    }

    public T get(List<Integer> idx) {
        return dimensionSpace.get(idx);
    }

    public void put(List<Integer> idx, T val) {
        dimensionSpace.put(idx, val);
    }

}
