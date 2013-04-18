package com.mulesoft.api;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.util.DirectoryScanner;
import org.eclipse.egit.github.core.Blob;
import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.Reference;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.Tree;
import org.eclipse.egit.github.core.TreeEntry;
import org.eclipse.egit.github.core.TypedResource;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.util.EncodingUtils;

/**
 * Hello world!
 *
 */
public class GithubUploader
{
    private static final String BRANCH_DEFAULT = "refs/heads/gh-pages";
    private static final int BUFFER_LENGTH = 8192;

    private final String branch = BRANCH_DEFAULT;
    private final boolean force = true;
    private final boolean merge = true;
    private final int retryCount = 3;
    private final int sleepTime = 10000;
    private final boolean failBuild =false;
    private final String message = "Updating documentation";
    private final String host ="api.github.com";
    private final boolean dryRun = false;


    // TODO: ???
    private String path;

    // TODO: add
    private String repositoryName;

    // TODO: add
    private String repositoryOwner;

    // TODO: add
    private String userName;

    // TODO: add
    private String password;

    // TODO: add
    private String oauth2Token;

    // TODO: Add
    private File outputDirectory;

    private String urlProject;


    public GithubUploader(String userName, String password, File outputDirectory, String urlProject)
    {
        this.userName = userName;
        this.password = password;
        this.outputDirectory = outputDirectory;
        this.urlProject = urlProject;
    }

    /**
     * Create client
     *
     * @param host
     * @param userName
     * @param password
     * @param oauth2Token
     * @return client
     * @throws CreateClientException
     */
    protected GitHubClient createClient(String host, String userName,
                                        String password, String oauth2Token) throws CreateClientException
    {
        GitHubClient client;
        if (!StringUtils.isEmpty(host)) {
            client = new GitHubClient(host);
        } else {
            client = new GitHubClient();
        }
        if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(password)) {
            client.setCredentials(userName, password);
        } else if (!StringUtils.isEmpty(oauth2Token)) {

            client.setOAuth2Token(oauth2Token);
        } else {
            throw new CreateClientException("No authentication credentials configured");
        }
        return client;
    }

    /**
     * Get repository and throw a {@link CreateClientException} on failures
     *
     * @param owner
     * @param name
     * @return non-null repository id
     * @throws CreateClientException
     */
    protected RepositoryId getRepository(String url, String owner,
                                         String name) throws CreateClientException
    {
        RepositoryId repository = RepositoryUtils.getRepository(url, owner, name);
        if (repository == null) {
            throw new CreateClientException("No GitHub repository (owner and name) configured");
        }

        return repository;
    }

    public void execute() throws Exception
    {

        RepositoryId repository = getRepository(urlProject, repositoryOwner, repositoryName);


        // Find files to include
        String baseDir = outputDirectory.getAbsolutePath();


        String[] paths = getMatchingPaths(baseDir);

        DataService service = new DataService(createClient(host, userName, password, oauth2Token));

        // Write blobs and build tree entries
        List<TreeEntry> entries = new ArrayList<TreeEntry>(paths.length);
        String prefix = path;
        if (prefix == null) {
            prefix = "";
        }
        if (prefix.length() > 0 && !prefix.endsWith("/")) {
            prefix += "/";
        }

        // Convert separator to forward slash '/'
        if ('\\' == File.separatorChar) {
            for (int i = 0; i < paths.length; i++) {
                paths[i] = paths[i].replace('\\', '/');
            }
        }

        for (String path : paths) {
            TreeEntry entry = new TreeEntry();
            entry.setPath(prefix + path);
            entry.setType(TreeEntry.TYPE_BLOB);
            entry.setMode(TreeEntry.MODE_BLOB);

            try {
                String blob = createBlob(service, repository, path);
                entry.setSha(blob);
            } catch (BlobCreationException e) {
                if (failBuild) {
                    throw e;
                } else {
                    return;
                }
            }

            entries.add(entry);
        }

        Reference ref = null;
        try {
            ref = service.getReference(repository, branch);
        } catch (RequestException e) {
            if (404 != e.getStatus()) {
                throw new Exception("Error getting reference: " + e.getMessage(), e);
            }
        } catch (IOException e) {
            throw new Exception("Error getting reference: " + e.getMessage(), e);
        }

        if (ref != null && !TypedResource.TYPE_COMMIT.equals(ref.getObject().getType())) {
            throw new Exception(
                    MessageFormat
                            .format("Existing ref {0} points to a {1} ({2}) instead of a commmit",
                                    ref.getRef(), ref.getObject().getType(),
                                    ref.getObject().getSha()));
        }

        // Write tree
        Tree tree;
        try {
            int size = entries.size();

            String baseTree = null;
            if (merge && ref != null) {
                Tree currentTree = service.getCommit(repository,
                                                     ref.getObject().getSha()).getTree();
                if (currentTree != null) {
                    baseTree = currentTree.getSha();
                }
            }
            if (!dryRun) {
                tree = service.createTree(repository, entries, baseTree);
            } else {
                tree = new Tree();
            }
        } catch (IOException e) {
            throw new Exception("Error creating tree: " + e.getMessage(), e);
        }

        // Build commit
        Commit commit = new Commit();
        commit.setMessage(message);
        commit.setTree(tree);

        // Set parent commit SHA-1 if reference exists
        if (ref != null) {
            commit.setParents(Collections.singletonList(new Commit().setSha(ref.getObject().getSha())));
        }

        Commit created;
        try {
            if (!dryRun) {
                created = service.createCommit(repository, commit);
            } else {
                created = new Commit();
            }
        } catch (IOException e) {
            throw new Exception("Error creating commit: " + e.getMessage(), e);
        }

        TypedResource object = new TypedResource();
        object.setType(TypedResource.TYPE_COMMIT).setSha(created.getSha());
        if (ref != null) {
            // Update existing reference
            ref.setObject(object);
            try {
                if (!dryRun) {
                    service.editReference(repository, ref, force);
                }
            } catch (IOException e) {
                throw new Exception("Error editing reference: " + e.getMessage(), e);
            }
        } else {
            // Create new reference
            ref = new Reference().setObject(object).setRef(branch);
            try {
                if (!dryRun) {
                    service.createReference(repository, ref);
                }
            } catch (IOException e) {
                throw new Exception("Error creating reference: " + e.getMessage(), e);
            }
        }
    }

    private String createBlob(DataService service, RepositoryId repository, String path) throws Exception
    {
        File file = new File(outputDirectory, path);
        long length = file.length();
        int size = length > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) length;
        ByteArrayOutputStream output = new ByteArrayOutputStream(size);
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            byte[] buffer = new byte[BUFFER_LENGTH];
            int read;
            while ((read = stream.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new BlobCreationException("Error reading file: " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(stream);
        }

        Blob blob = new Blob().setEncoding(Blob.ENCODING_BASE64);
        blob.setContent(EncodingUtils.toBase64(output.toByteArray()));

        try {
            if (!dryRun) {
                return uploadBlobRetryIfError(service, repository, blob);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new Exception("Error creating blob: " + e.getMessage(), e);
        }
    }

    private String uploadBlobRetryIfError(DataService service, RepositoryId repository, Blob blob) throws Exception {
        for (int i = 0; i < retryCount; i++) {
            try {
                return service.createBlob(repository, blob);
            } catch (IOException e) {
                sleep(sleepTime);
            }
        }

        throw new Exception("Cannot upload documentation to GitHub after retrying");
    }

    /**
     * Create an array with only the non-null and non-empty values
     *
     * @param values
     * @return non-null but possibly empty array of non-null/non-empty strings
     */
    public static String[] removeEmpties(String... values) {
        if (values == null || values.length == 0) {
            return new String[0];
        }
        List<String> validValues = new ArrayList<String>();
        for (String value : values) {
            if (value != null && value.length() > 0) {
                validValues.add(value);
            }
        }
        return validValues.toArray(new String[validValues.size()]);
    }

    /**
     * Get matching paths found in given base directory
     *
     * @param baseDir
     * @return non-null but possibly empty array of string paths relative to the
     *         base directory
     */
    public static String[] getMatchingPaths(String baseDir) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(baseDir);

        scanner.scan();
        return scanner.getIncludedFiles();
    }

    private void sleep(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
