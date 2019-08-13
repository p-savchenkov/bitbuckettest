package test.savchenkov.bitbuckettest;

import com.jayway.restassured.response.ResponseBody;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateIssueTest {

  private WebDriver driver;
  private final String TITLE = RandomStringUtils.randomAlphabetic(15);
  private BitbucketConfig cfg = ConfigFactory.create(BitbucketConfig.class);
  private Issue issue;

  @Before
  public void startWebDriver() {
    issue = createIssue(TITLE);
    WebDriverManager.chromedriver().setup();
    driver = new ChromeDriver();
  }

  @After
  public void stopWebDriver() {
    driver.quit();
    deleteIssue(issue);
  }

  private Issue createIssue(String title) {
    issue = new Issue(title);
    issue.setTitle(title);

    ResponseBody response = given()
        .auth().basic(cfg.userName(), cfg.password())
        .header("Content-Type", " application/json")
        .baseUri(cfg.apiHost())
        .body(issue)
        .when()
        .post(
            String.format(
                "2.0/repositories/%s/%s/issues",
                cfg.userName(),
                cfg.repo()
            )
        )
        .getBody();

    return response.as(Issue.class);
  }

  private void deleteIssue(Issue issue) {
    given()
        .auth().preemptive().basic(cfg.userName(), cfg.password())
        .baseUri(cfg.apiHost())
        .when()
        .delete(
            String.format(
                "2.0/repositories/%s/%s/issues/%s",
                cfg.userName(),
                cfg.repo(),
                issue.getId()
            )
        )
        .then()
        .statusCode(204);
  }

  @Test
  public void shouldSeeCreatedIssueTitle() {
    String url = String.format(
        "https://bitbucket.org/%s/%s/issues/%s",
        cfg.userName(),
        cfg.repo(),
        issue.getId()
    );

    driver.get(url);
    WebElement issueTitle = driver.findElement(By.xpath("//*[@id=\"issue-title\"]"));
    assertThat(issueTitle.getText(), equalTo(issue.getTitle()));
  }
}