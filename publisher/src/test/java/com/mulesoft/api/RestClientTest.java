package com.mulesoft.api;

import org.junit.Test;

public class RestClientTest
{

    @Test
    public void test(){
        new RestClient().post(new ApiMetadata("desc","url", "name", new MavenProject("groupId", "artifact","version")));
    }
}
