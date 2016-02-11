import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClassPathResourceFolderListerTest {
  private String folder = "/resource-directory/spark/";

  @Test
  public void verifyNestedSubfolderContentsAreListedProperly() {
    final Collection<String> list = ClassPathResourceFolderLister.getFolderListing(folder);
    verifyListCorrectness(list);
  }

  @Test
  public void verifyFolderEntriesAreListableForFolderInJar() {
    String jar = "jar-with-resource-directory-at-top-level.jar";
    URL url = Thread.currentThread().getContextClassLoader().getResource(jar);
    assert url != null;
    String jarFile = url.getFile();
    List<String> classPathElements = new ArrayList<String>();
    classPathElements .add(jarFile);
    final Collection<String> list = ClassPathResourceFolderLister
        .getFolderListingForFirstMatchInClassPath(folder, classPathElements);
    verifyListCorrectness(list);
  }

  private void verifyListCorrectness(Collection<String> list) {
    assert list.contains("bar");
    assert list.contains("junk1");
    assert list.contains("junko");
    assert list.size() == 3;
  }
}

