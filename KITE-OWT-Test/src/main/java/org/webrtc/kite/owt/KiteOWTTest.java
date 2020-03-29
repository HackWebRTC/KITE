package org.webrtc.kite.owt;

import io.cosmosoftware.kite.steps.StayInMeetingStep;
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonArray;
import org.webrtc.kite.owt.checks.AllVideoHDCheck;
import org.webrtc.kite.owt.checks.BitrateCheck;
import org.webrtc.kite.owt.checks.IceStateCheck;
import org.webrtc.kite.owt.checks.RemoteVideoDisplayCheck;
import org.webrtc.kite.owt.pages.MainPage;
import org.webrtc.kite.owt.steps.OpenUrlStep;
import org.webrtc.kite.tests.KiteBaseTest;
import org.webrtc.kite.tests.TestRunner;

public class KiteOWTTest extends KiteBaseTest {

  private List<String> rooms = new ArrayList<>();
  private int roomSize = 2;

  @Override
  protected void payloadHandling() {
    super.payloadHandling();
    roomSize = payload.getInt("room_size", 2);
    if (roomSize <= 0) {
      roomSize = 2;
    }
    JsonArray array = payload.getJsonArray("rooms");
    if (array != null && !array.isEmpty()) {
      for (int i = 0; i < array.size(); i++) {
        rooms.add(array.getString(i));
      }
    }
  }

  @Override
  public void populateTestSteps(TestRunner runner) {
    String owtUrl = url;
    if (!rooms.isEmpty()) {
      int index = (runner.getId() / roomSize) % rooms.size();
      owtUrl += "&room=" + rooms.get(index);
    }

    MainPage mainPage = new MainPage(runner);
    runner.addStep(new OpenUrlStep(runner, mainPage, owtUrl, roomSize));
    runner.addStep(new IceStateCheck(runner, mainPage, roomSize));
    runner.addStep(new RemoteVideoDisplayCheck(runner, mainPage, roomSize));
    runner.addStep(new AllVideoHDCheck(runner, mainPage, roomSize));

    BitrateCheck bitrateCheck = new BitrateCheck(runner, mainPage, 2500);
    runner.addStep(bitrateCheck);

    if (this.meetingDuration > 0) {
      runner.addStep(new StayInMeetingStep(runner, meetingDuration));
    }
  }
}
