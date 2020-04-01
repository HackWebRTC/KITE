package org.webrtc.kite.owt.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.steps.TestStep;
import org.webrtc.kite.owt.pages.MainPage;
import org.webrtc.kite.tests.TestRunner;

import static io.cosmosoftware.kite.util.TestUtils.waitAround;

public class OpenUrlStep extends TestStep {

  private final TestRunner runner;
  private final String url;
  private final MainPage mainPage;
  private final int roomSize;

  public OpenUrlStep(TestRunner runner, MainPage mainPage, String url, int roomSize) {
    super(runner);
    this.runner = runner;
    this.mainPage = mainPage;
    this.url = url;
    this.roomSize = roomSize;
  }

  @Override
  public String stepDescription() {
    return "Open " + url;
  }

  @Override
  protected void step() throws KiteTestException {
    // if all clients join at the same time, some client may not receive others
    //waitAround((runner.getId() % roomSize) * 2000);
    waitAround(runner.getId() * 1200);
    logger.info("OpenUrlStep open " + url);
    mainPage.open(url);
  }
}
