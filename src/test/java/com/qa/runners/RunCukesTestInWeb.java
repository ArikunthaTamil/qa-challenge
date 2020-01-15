package com.qa.runners;
import com.qa.listeners.ExtentProperties;
import com.qa.listeners.Reporter;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import cucumber.api.testng.AbstractTestNGCucumberTests;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.TestNGCucumberRunner;
import org.junit.runner.RunWith;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.io.File;
import static com.qa.driverFactory.DriverManager.getBrowserInfo;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/website",
        glue = {"com.qa/stepDefinitions/web"},
        plugin = {"com.qa.listeners.ExtentCucumberFormatter:"})
public class RunCukesTestInWeb extends AbstractTestNGCucumberTests {
    private TestNGCucumberRunner testNGCucumberRunner;

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
        ExtentProperties extentProperties = ExtentProperties.INSTANCE;
        extentProperties.setReportPath(extentProperties.getReportPath());
    }

    @Test(groups = "cucumber", description = "Runs Cucumber Feature", dataProvider = "features")
    public void feature(CucumberFeatureWrapper cucumberFeature) {
        testNGCucumberRunner.runCucumber(cucumberFeature.getCucumberFeature());
    }

    @DataProvider
    public Object[][]features() {
        return testNGCucumberRunner.provideFeatures();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws Exception {
        Reporter.loadXMLConfig(new File("src/test/resources/extent-config.xml"));
        Reporter.setSystemInfo("Browser Name",getBrowserInfo());
        Reporter.setSystemInfo("user", System.getProperty("user.name"));
        Reporter.setSystemInfo("os", "Mac OSX");
        Reporter.setTestRunnerOutput("*End of Test Execution*");
        testNGCucumberRunner.finish();

    }

}