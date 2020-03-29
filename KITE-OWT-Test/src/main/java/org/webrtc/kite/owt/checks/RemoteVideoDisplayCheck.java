/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.webrtc.kite.owt.checks;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.steps.TestCheck;
import org.webrtc.kite.owt.pages.MainPage;

import static io.cosmosoftware.kite.util.TestUtils.videoCheck;
import static io.cosmosoftware.kite.util.TestUtils.waitAround;

public class RemoteVideoDisplayCheck extends TestCheck {
  private final MainPage mainPage;
  private final int roomSize;

  public RemoteVideoDisplayCheck(Runner runner, MainPage mainPage, int roomSize) {
    super(runner);
    this.mainPage = mainPage;
    this.roomSize = roomSize;
  }

  @Override
  public String stepDescription() {
    return "Verify that the remote video is actually playing";
  }

  @Override
  protected void step() throws KiteTestException {
    logger.info("Looking for video object");

    boolean allVisible = false;
    for (int elapsedTime = 0; elapsedTime < checkTimeout; elapsedTime += checkInterval) {
      mainPage.waitAllVideos();
      if (mainPage.videoNum() == roomSize) {
        allVisible = true;
        break;
      }
      waitAround(checkInterval);
    }
    if (!allVisible) {
      throw new KiteTestException("Unable to find "
          + roomSize
          + " <video> element on the page. Only find "
          + mainPage.videoNum(),
          Status.FAILED);
    }

    StringBuilder videoCheck = new StringBuilder();
    boolean error = false;
    for (int i = 1; i < roomSize; i++) {
      String v = videoCheck(webDriver, i);
      videoCheck.append(v);
      if (i < roomSize - 1) {
        videoCheck.append("|");
      }
      if (!"video".equalsIgnoreCase(v)) {
        error = true;
      }
    }
    reporter.textAttachment(report, "Received Videos", videoCheck.toString(), "plain");
    if (error) {
      throw new KiteTestException("Some videos are still or blank: " + videoCheck, Status.FAILED);
    }
  }
}
