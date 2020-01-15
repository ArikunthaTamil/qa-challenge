package com.qa.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.GherkinKeyword;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.ExtentXReporter;
import com.aventstack.extentreports.reporter.KlovReporter;
import com.mongodb.MongoClientURI;
import com.qa.driverFactory.DriverManager;
import com.qa.driverFactory.LocalWebDriverListener;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import static com.qa.driverFactory.DriverManager.getBrowserInfo;



/**
 * A cucumber based reporting listener which generates the Extent Report
 */
public class ExtentCucumberFormatter implements Reporter, Formatter {
    public static ExtentReports extentReports;
    private static ExtentHtmlReporter htmlReporter;
    private static KlovReporter klovReporter;

    public static ThreadLocal<ExtentTest> featureTestThreadLocal = new InheritableThreadLocal<>();
    public static ThreadLocal<ExtentTest> scenarioOutlineThreadLocal = new InheritableThreadLocal<>();
    static ThreadLocal<ExtentTest> scenarioThreadLocal = new InheritableThreadLocal<>();
    private static ThreadLocal<LinkedList<Step>> stepListThreadLocal = new InheritableThreadLocal<>();
    static ThreadLocal<ExtentTest> stepTestThreadLocal = new InheritableThreadLocal<>();
    private boolean scenarioOutlineFlag;
    private static final ThreadLocal myThreadLocal = new InheritableThreadLocal();
    public volatile static String device;
    public static ExtentTest scenarioNode;
    static LocalWebDriverListener localWebDriverListener= new LocalWebDriverListener();
    DriverManager driverManager=new DriverManager();


    public ExtentCucumberFormatter(File file) {
        setExtentHtmlReport(file);
        setExtentReport();
        setKlovReport();
        stepListThreadLocal.set(new LinkedList<Step>());
        scenarioOutlineFlag = false;
    }

    private static void setExtentHtmlReport(File file) {
        if (htmlReporter != null) {
            return;
        }
        if (file == null || file.getPath().isEmpty()) {
            file = new File(ExtentProperties.INSTANCE.getReportPath());
        }
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        htmlReporter = new ExtentHtmlReporter(file);
    }

    static ExtentHtmlReporter getExtentHtmlReport() {
        return htmlReporter;
    }



    private static void setExtentReport() {
        ExtentTest test = null;
        if (extentReports != null) {
            return;
        }
        extentReports = new ExtentReports();
        ExtentProperties extentProperties = ExtentProperties.INSTANCE;

        // Remove this block in the next release
        if (extentProperties.getExtentXServerUrl() != null) {
            String extentXServerUrl = extentProperties.getExtentXServerUrl();
            try {
                URL url = new URL(extentXServerUrl);
                ExtentXReporter xReporter = new ExtentXReporter(url.getHost());
                xReporter.config().setServerUrl(extentXServerUrl);
                xReporter.config().setProjectName(extentProperties.getProjectName());
                //test.assignCategory(browserName);
                //test.info(MarkupHelper.createLabel(getBrowserInfo(),ExtentColor.RED));
                extentReports.attachReporter(htmlReporter, xReporter);
                return;
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid ExtentX Server URL", e);
            }
        }
        extentReports.attachReporter(htmlReporter);
    }

    static ExtentReports getExtentReport() {
        return extentReports;
    }

    /**
     * When running cucumber tests in parallel Klov reporter should be attached only once, in order to avoid duplicate builds on klov server.
     */
    private static synchronized void setKlovReport() {
        if (extentReports == null) {
            //Extent reports object not found. call setExtentReport() first
            return;
        }

        ExtentProperties extentProperties = ExtentProperties.INSTANCE;

        //if reporter is not null that means it is already attached
        if (klovReporter != null) {
            //Already attached, attaching it again will create a new build/klov report
            return;
        }


        if (extentProperties.getKlovServerUrl() != null) {
            String hostname = extentProperties.getMongodbHost();
            int port = extentProperties.getMongodbPort();

            String database = extentProperties.getMongodbDatabase();

            String username = extentProperties.getMongodbUsername();
            String password = extentProperties.getMongodbPassword();

            try {
                //Create a new KlovReporter object
                klovReporter = new KlovReporter();

                if (username != null && password != null) {
                    MongoClientURI uri = new MongoClientURI("mongodb://" + username + ":" + password + "@" + hostname + ":" + port + "/?authSource=" + database);
                    klovReporter.initMongoDbConnection(uri);
                } else {
                    klovReporter.initMongoDbConnection(hostname, port);
                }

                klovReporter.setProjectName(extentProperties.getKlovProjectName());
                klovReporter.setReportName(extentProperties.getKlovReportName());
                klovReporter.setKlovUrl(extentProperties.getKlovServerUrl());

                extentReports.attachReporter(klovReporter);

            } catch (Exception ex) {
                klovReporter = null;
                throw new IllegalArgumentException("Error setting up Klov Reporter", ex);
            }
        }
    }

    static KlovReporter getKlovReport() {
        return klovReporter;
    }

    public void syntaxError(String state, String event, List<String> legalEvents, String uri,
                            Integer line) {

    }

    public void uri(String uri) {

    }

    public void feature(Feature feature) {
        try {
            myThreadLocal.set(localWebDriverListener.deviceName);
            device = (String) myThreadLocal.get();
            System.out.println("Device Name in extent report is "+device);

            featureTestThreadLocal.set(getExtentReport().createTest(com.aventstack.extentreports.gherkin.model.Feature.class, feature.getName()+ " in browser "+getBrowserInfo()+ " Device name is "+device));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ExtentTest test = featureTestThreadLocal.get();
        for (Tag tag : feature.getTags()) {
            test.assignCategory(tag.getName());
            //test.assignCategory(browserName);
        }

    }

    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        scenarioOutlineFlag = true;
        ExtentTest node = featureTestThreadLocal.get()
                .createNode(com.aventstack.extentreports.gherkin.model.ScenarioOutline.class, scenarioOutline.getName());
        scenarioOutlineThreadLocal.set(node);

    }


    public void examples(Examples examples) {
        ExtentTest test = scenarioOutlineThreadLocal.get();

        String[][] data = null;
        List<ExamplesTableRow> rows = examples.getRows();
        int rowSize = rows.size();
        for (int i = 0; i < rowSize; i++) {
            ExamplesTableRow examplesTableRow = rows.get(i);
            List<String> cells = examplesTableRow.getCells();
            int cellSize = cells.size();
            if (data == null) {
                data = new String[rowSize][cellSize];
            }
            for (int j = 0; j < cellSize; j++) {
                data[i][j] = cells.get(j);
            }
        }
        test.info(MarkupHelper.createTable(data));

    }

    public void startOfScenarioLifeCycle(Scenario scenario) {
        if (scenarioOutlineFlag) {
            scenarioOutlineFlag = false;
        }

       // ExtentTest scenarioNode;
        if (scenarioOutlineThreadLocal.get() != null && scenario.getKeyword().trim()
                .equalsIgnoreCase("Scenario Outline")) {
            scenarioNode =
                    scenarioOutlineThreadLocal.get().createNode(com.aventstack.extentreports.gherkin.model.Scenario.class, scenario.getName());
            //for (int i = 0; i < Thread.currentThread().getId(); i++) {
                /*String[][] data = new String[0][];
                try {
                    data = new String[][]{
                            {"Browser", "Device Name", "BundleID"},
                            {getBrowserInfo(), device, ""}

                    };
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //scenarioNode.info(MarkupHelper.createTable(data));
            scenarioNode.log(Status.PASS,(MarkupHelper.createTable(data)));*/

            }
        else {
            scenarioNode =
                    featureTestThreadLocal.get().createNode(com.aventstack.extentreports.gherkin.model.Scenario.class, scenario.getName());
        }

        for (Tag tag : scenario.getTags()) {
            scenarioNode.assignCategory(tag.getName());
            //scenarioNode.assignCategory(browserName);
        }
        scenarioThreadLocal.set(scenarioNode);

    }

    public void background(Background background) {

    }

    public void scenario(Scenario scenario) {

    }


    public void step(Step step) {
        if (scenarioOutlineFlag) {
            return;
        }
        stepListThreadLocal.get().add(step);
    }

    public void endOfScenarioLifeCycle(Scenario scenario) {

    }

    public void done() {
        getExtentReport().flush();
    }

    @Override
    public void close() {

    }

    @Override
    public void eof() {

    }

    @Override
    public void before(Match match, Result result) {

    }

    public void result(Result result) {
        if (scenarioOutlineFlag) {
            return;
        }

        if (Result.PASSED.equals(result.getStatus())) {
            stepTestThreadLocal.get().pass(Result.PASSED);
        } else if (Result.FAILED.equals(result.getStatus())) {
            stepTestThreadLocal.get().fail(result.getError());
        } else if (Result.SKIPPED.equals(result)) {
            stepTestThreadLocal.get().skip(Result.SKIPPED.getStatus());
        } else if (Result.UNDEFINED.equals(result)) {
            stepTestThreadLocal.get().skip(Result.UNDEFINED.getStatus());
        }
    }

    public void after(Match match, Result result) {

    }

    public void match(Match match) {
        Step step = stepListThreadLocal.get().poll();
        String data[][] = null;
        if (step.getRows() != null) {
            List<DataTableRow> rows = step.getRows();
            int rowSize = rows.size();
            for (int i = 0; i < rowSize; i++) {
                DataTableRow dataTableRow = rows.get(i);
                List<String> cells = dataTableRow.getCells();
                int cellSize = cells.size();
                if (data == null) {
                    data = new String[rowSize][cellSize];
                }
                for (int j = 0; j < cellSize; j++) {
                    data[i][j] = cells.get(j);
                }
            }
        }

        ExtentTest scenarioTest = scenarioThreadLocal.get();
        ExtentTest stepTest = null;

        try {
            stepTest = scenarioTest.createNode(new GherkinKeyword(step.getKeyword()), step.getKeyword() + step.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (data != null) {
            Markup table = MarkupHelper.createTable(data);
            stepTest.info(table);
        }

        stepTestThreadLocal.set(stepTest);
    }

    public void embedding(String mimeType, byte[] data) {

    }

    public void write(String text) {

    }
}