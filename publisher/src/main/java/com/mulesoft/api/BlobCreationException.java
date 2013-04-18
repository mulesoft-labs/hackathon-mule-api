package com.mulesoft.api;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: fernandofederico
 * Date: 4/17/13
 * Time: 9:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlobCreationException extends Exception
{

    public BlobCreationException(String s, IOException e)
    {
        super(s,e);
    }
}
