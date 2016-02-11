import org.junit.Test;

import java.util.Collection;

public class ClassPathResourceFolderListerTest {

    private Collection collection;

    @Test
    public void verifyNestedSubfolderContentsAreListedProperly() {
        String folder = "/resource-directory/spark/";
        final Collection<String> list = ClassPathResourceFolderLister.getFolderListing(folder);

        assert list.contains("bar");
        assert list.contains("junk1");
        assert list.contains("junko");
        assert list.size() == 3;
    }
}

