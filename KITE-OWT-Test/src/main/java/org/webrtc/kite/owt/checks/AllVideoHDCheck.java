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
import java.util.Arrays;
import javax.json.JsonObject;
import org.webrtc.kite.owt.pages.MainPage;

public class AllVideoHDCheck extends TestCheck {
  private final MainPage mainPage;
  private final int roomSize;

  public AllVideoHDCheck(Runner runner, MainPage mainPage, int roomSize) {
    super(runner);
    this.mainPage = mainPage;
    this.roomSize = roomSize;
  }

  @Override
  public String stepDescription() {
    return "Verify that the all video resolution are HD (1280x720)";
  }

  @Override
  protected void step() throws KiteTestException {
    boolean[] hd = new boolean[roomSize];
    String[] res = new String[roomSize];
    for (int elapsedTime = 0; elapsedTime < this.checkTimeout; elapsedTime += this.checkInterval) {
      boolean allHD = true;
      for (int i = 0; i < roomSize; i++) {
        if (!hd[i]) {
          JsonObject resolution = mainPage.getResolution(i);
          int width = resolution.getInt("width");
          int height = resolution.getInt("height");
          res[i] = width + "x" + height;
          logger.info("video[" + i + "]: " + res[i]);
          hd[i] = width == 1280 && height == 720;
          allHD = allHD && hd[i];
        }
      }
      if (allHD) {
        return;
      }
    }
    reporter.textAttachment(report, "Videos res", Arrays.toString(res), "plain");
    throw new KiteTestException("Not all HD, hd: " + Arrays.toString(hd), Status.FAILED);
  }
}
