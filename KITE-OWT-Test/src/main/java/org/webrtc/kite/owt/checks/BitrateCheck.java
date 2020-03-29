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

import static io.cosmosoftware.kite.util.TestUtils.waitAround;

public class BitrateCheck extends TestCheck {
  private final MainPage mainPage;

  private int expectedBitrate = -1;

  public BitrateCheck(Runner runner, MainPage mainPage, int expectedBitrate) {
    super(runner);
    this.mainPage = mainPage;
    this.expectedBitrate = expectedBitrate;
  }

  @Override
  public String stepDescription() {
    return "Verify the bitrate of a media track";
  }

  @Override
  protected void step() throws KiteTestException {
    int duration = 20_000;
    double minBitrate = 0.7 * expectedBitrate;
    double maxBitrate = 1.3 * expectedBitrate;

    logger.info("BitrateCheck get start bitrate");
    double startingTotalByteCount = mainPage.getPublishTotalSentBytes();
    logger.info("BitrateCheck got start bitrate");
    waitAround(duration);
    logger.info("BitrateCheck get end bitrate");
    double endingTotalByteCount = mainPage.getPublishTotalSentBytes();
    logger.info("BitrateCheck got end bitrate");

    double avgBitrate = (endingTotalByteCount - startingTotalByteCount) * 8 / duration;
    // Assuming that there's a 10% tolerance to the test result:
    reporter.textAttachment(report, "Bitrate check",
        "Expected : [" + minBitrate + ", " + maxBitrate + "], found " + avgBitrate, "plain");
    if (avgBitrate < minBitrate || maxBitrate < avgBitrate) {
      throw new KiteTestException(
          "Expected bitrate to be in [" + minBitrate + "," + maxBitrate + "], found " + avgBitrate,
          Status.FAILED);
    }
  }
}
