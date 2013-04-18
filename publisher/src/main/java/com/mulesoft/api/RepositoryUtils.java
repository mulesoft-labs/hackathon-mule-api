package com.mulesoft.api;

import static org.eclipse.egit.github.core.client.IGitHubConstants.HOST_DEFAULT;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SUFFIX_GIT;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.egit.github.core.RepositoryId;

/**
 * Created with IntelliJ IDEA.
 * User: fernandofederico
 * Date: 4/17/13
 * Time: 11:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class RepositoryUtils
{
    /**
     * Extra repository id from given SCM URL
     *
     * @param url
     * @return repository id or null if extraction fails
     */
    public static RepositoryId extractRepositoryFromScmUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        int ghIndex = url.indexOf(HOST_DEFAULT);
        if (ghIndex == -1 || ghIndex + 1 >= url.length()) {
            return null;
        }
        if (!url.endsWith(SUFFIX_GIT)) {
            return null;
        }
        url = url.substring(ghIndex + HOST_DEFAULT.length() + 1, url.length()
                                                                 - SUFFIX_GIT.length());
        return RepositoryId.createFromId(url);
    }

    /**
     * Get repository
     *
     * @param owner
     * @param name
     * @return repository id or null if none configured
     */
    public static RepositoryId getRepository(final String url,
                                             final String owner, final String name) {
        // Use owner and name if specified
        if (!StringUtils.isEmpty(owner) && !StringUtils.isEmpty(name)) {
            return RepositoryId.create(owner, name);
        }

        return RepositoryId.createFromUrl(url);
    }
}
