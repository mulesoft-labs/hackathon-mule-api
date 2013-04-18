package com.mulesoft.api;


import org.codehaus.jackson.annotate.JsonProperty;

public class ApiMetadata
{

    private String description;
    private String docsUrl;
    private String name;
    private MavenProject mavenProject;

    public ApiMetadata(String description, String docsUrl, String name, MavenProject mavenProject)
    {
        this.description = description;
        this.docsUrl = docsUrl;
        this.name = name;
        this.mavenProject = mavenProject;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDocsUrl()
    {
        return docsUrl;
    }

    public void setDocsUrl(String docsUrl)
    {
        this.docsUrl = docsUrl;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public MavenProject getMavenProject()
    {
        return mavenProject;
    }
}
