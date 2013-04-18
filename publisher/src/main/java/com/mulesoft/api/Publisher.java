package com.mulesoft.api;


import java.io.File;

public class Publisher
{
   public static void publish(GithubConfiguration githubConfiguration, ApiMetadata apiMetadata) throws Exception
   {
       GithubUploader uploader = new GithubUploader(githubConfiguration.getUsername(),
                                                          githubConfiguration.getPassword(),
                                                          new File(githubConfiguration.getDocPath()),
                                                          githubConfiguration.getUrl());

       RestClient restClient = new RestClient();

       uploader.execute();
       restClient.post(apiMetadata);
   }


    public static void main(String[] args) throws Exception
    {
        publish(new GithubConfiguration("https://github.com/fernandofederico1984/doc-test",
                                        "fernandofederico1984",
                                        "",
                                        ""),
                new ApiMetadata("This is a test app", "https://fernandofederico1984.github.io/doc-test","the App name",
                                new MavenProject("org.mule.api", "my-test-api", "1.0-SNAPSHOT")));
    }

}
