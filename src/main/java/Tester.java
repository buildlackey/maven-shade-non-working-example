import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * this class does tests.
 */
public class Tester {

  /**
   * this method does tests.
   */
  public void test(String resourceName) throws IOException {
    ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    // The path to the resource from the root of the JAR file
    System.out.println("SPRING way");
    Resource[] resources = resourceResolver.getResources("resource-directory/*");
    for (Resource resource : resources) {

      System.out.println("resource: " + resource.getDescription());
      InputStream in = resource.getInputStream();
      System.out.println("input stream: " + in);
      Object result = IOUtils.toByteArray(in);
      String resultAsString = result.toString();
      System.out.println("resultAsString:" + resultAsString);

      Object again = IOUtils.readLines(in);
      System.out.println("readlines gives: " + again);
    }
  }

  /**
   * this method does stuff.
   */
  public static void main(String[] args) throws IOException {
    //new Tester().test("resourcedir");
    final Collection<String> list = ClassPathResourceFolderLister.getFolderListing("/resource-directory/spark/");
    for(final String name : list){
      System.out.println("FINAL>" + name);
    }
  }
}

