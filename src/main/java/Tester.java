import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * this class does tests.
 */
public class Tester {

  /**
   * this method does tests.
   */
  public void test(String resourceName) throws IOException {
    InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourceName);
    System.out.println("input stream: " + in);

    Object result = IOUtils.readLines(in);
    System.out.println("result of directory contents as  classpath resource:" + result);
  }

  /**
   * this method does stuff.
   */
  public static void main(String[] args) throws IOException {
    new Tester().test("resource-directory");
  }
}
