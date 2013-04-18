package com.mulesoft.api;

public class GithubConfiguration
{

    String url;
    String username;
    String password;
    private String docPath;

    public GithubConfiguration(String url, String username, String password, String docPath)
    {
        this.url = url;
        this.username = username;
        this.password = password;
        this.docPath = docPath;
    }

    public String getUrl()
    {
        return url;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public String getDocPath()
    {
        return docPath;
    }
}
