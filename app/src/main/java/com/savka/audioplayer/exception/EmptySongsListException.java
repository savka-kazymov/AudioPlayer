package com.savka.audioplayer.exception;

/**
 * Created by vlad-pc on 02.09.2014.
 */
public class EmptySongsListException extends Exception {
    //Parameterless Constructor
    public EmptySongsListException() {
    }

    //Constructor that accepts a message
    public EmptySongsListException(String message) {
        super(message);
    }
}
