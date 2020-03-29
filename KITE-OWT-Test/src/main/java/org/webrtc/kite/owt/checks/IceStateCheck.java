package org.webrtc.kite.owt.checks;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.steps.TestCheck;
import java.util.List;
import org.webrtc.kite.owt.pages.MainPage;

import static io.cosmosoftware.kite.util.TestUtils.waitAround;

public class IceStateCheck extends TestCheck {

  private final MainPage mainPage;
  private final int roomSize;

  public IceStateCheck(Runner runner, MainPage mainPage, int roomSize) {
    super(runner);
    this.mainPage = mainPage;
    this.roomSize = roomSize;
  }

  @Override
  public String stepDescription() {
    return "IceStateCheck check IceConnectionState of all PC";
  }

  @Override
  protected void step() throws KiteTestException {
    for (int elapsedTime = 0; elapsedTime < this.checkTimeout; elapsedTime += this.checkInterval) {
      List<String> states = mainPage.getICEConnectionStates();
      logger.info("ICE states: " + states);
      if (states.size() == roomSize) {
        boolean allConnected = true;
        for (String state : states) {
          if (state.equalsIgnoreCase("failed")) {
            throw new KiteTestException("The ICE connection's state has changed to failed",
                Status.FAILED);
          }
          if (!(state.equalsIgnoreCase("connected") || state.equalsIgnoreCase("completed"))) {
            allConnected = false;
          }
        }
        if (allConnected) {
          return;
        }
      }
      waitAround(this.checkInterval);
    }
    reporter.textAttachment(report, "ICE states", mainPage.getICEConnectionStates().toString(),
        "plain");
    throw new KiteTestException(
        "Could not verify the ICE connection's state after " + this.checkTimeout + "ms",
        Status.FAILED);
  }
}
