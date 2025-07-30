package com.novospir.libraries;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

    /*
    Todo: include instructions for modifying this so that you can use it in-situe (forgot what that use-case is)
        -ah, override a function on class creation such that you can call dispose() to release all resources that are either currently empty or entirely full

    Todo: Implement efficient storage; if a leaf is one color, then store that color.
        if on retrieval, the leaf is null &  color !null, create buffered image of that color
        note: this trades space for operation time - setting to flag on initialization
            exception is 0x0 tile ie fully transparent, then return null or rather, color will be null as well.
            the function above, dispose()/garbageCollect(), will call this function and iterate through tiles to check
            if any are able to be "optimized"
     */
}