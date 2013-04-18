package com.mulesoft.api;


import com.google.gson.JsonObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;

public class RestClient
{
    public static final String NAME = "name";
    public static final String DOCS_URL = "docsUrl";
    public static final String DESCRIPTION = "description";

    public void post(ApiMetadata metadata)
    {
        try
        {
            Client client = Client.create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(DESCRIPTION,metadata.getDescription());
            jsonObject.addProperty(NAME, metadata.getName());
            jsonObject.addProperty(DOCS_URL,metadata.getDocsUrl());


            MavenProject mavenProject = metadata.getMavenProject();
            WebResource webResource = client
                    .resource("http://172.16.20.128:9090/packages").path(mavenProject.getGroupId())
                                      .path(mavenProject.getArtifactId())
                                      .path(mavenProject.getVersion());


            webResource.type(MediaType.APPLICATION_JSON_TYPE)
                    .post(ClientResponse.class, jsonObject.toString());



        }
        catch (Exception e)
        {

            e.printStackTrace();

        }
    }


}
