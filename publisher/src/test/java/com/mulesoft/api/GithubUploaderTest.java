package com.mulesoft.api;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple GithubUploader.
 */
public class GithubUploaderTest

{
    @org.junit.Test
    public void test() throws Exception
    {
        GithubUploader uploader = new GithubUploader("USER", "PASSWORD",
                                                      new File("FILE_PATH"),
                                                      "GIT PROJECT URL");

        uploader.execute();
    }

}
