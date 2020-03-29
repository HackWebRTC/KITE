package org.webrtc.kite.owt.pages;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.pages.BasePage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static io.cosmosoftware.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static io.cosmosoftware.kite.util.TestUtils.executeJsScript;
import static io.cosmosoftware.kite.util.TestUtils.waitAround;

public class MainPage extends BasePage {

  private List<WebElement> videos = new ArrayList<>();

  public MainPage(Runner runner) {
    super(runner);
  }

  public void open(String url) {
    webDriver.get(url);
  }

  public void waitAllVideos() throws KiteTestException {
    videos = webDriver.findElements(By.tagName("video"));
    for (WebElement video : videos) {
      waitUntilVisibilityOf(video, 20);
    }
  }

  public int videoNum() {
    return videos.size();
  }

  @SuppressWarnings("unchecked")
  public List<String> getICEConnectionStates() throws KiteTestException {
    Object res = executeJsScript(webDriver, getIceConnectionStateScript());
    if (res instanceof ArrayList) {
      return (ArrayList<String>) res;
    }
    return new ArrayList<>();
  }

  private String getIceConnectionStateScript() {
    return "var retValue;"
        + "try {"
        + "retValue = conference.channels.map(function (c) {return c._pc.iceConnectionState});"
        + "} catch (exception) {} "
        + "if (retValue) {return retValue;} else {return [];}";
  }

  public JsonObject getResolution(int index) throws KiteTestException {
    executeJsScript(webDriver, stashResolutionScript(index));
    waitAround(ONE_SECOND_INTERVAL);
    String resolution = (String) executeJsScript(webDriver, getStashedResolutionScript());
    InputStream stream = new ByteArrayInputStream(resolution.getBytes(StandardCharsets.UTF_8));
    JsonReader reader = Json.createReader(stream);
    return reader.readObject();
  }

  private String stashResolutionScript(int index) {
    return "window.resolution = {width: -1, height: -1};"
        + "conference.channels[" + index + "]._pc.getStats().then(data => {"
        + "   [...data.values()].forEach(function(e){"
        + "       if (e.type.startsWith('track')){"
        + "           if (e.kind == 'video') { "
        + "               window.resolution.width = e.frameWidth;"
        + "               window.resolution.height = e.frameHeight;"
        + "           }"
        + "       }"
        + "   });"
        + "});";
  }

  private String getStashedResolutionScript() {
    return "return JSON.stringify(window.resolution);";
  }

  public long getPublishTotalSentBytes() throws KiteTestException {
    executeJsScript(webDriver, stashPublishTotalSentBytesScript());
    waitAround(ONE_SECOND_INTERVAL);
    return (Long) executeJsScript(webDriver, getStashedPublishTotalSentBytesScript());
  }

  private String stashPublishTotalSentBytesScript() {
    return "window.pubTotalSentBytes = 0;"
        + "conference.channels.filter(c => c._publication != null)[0]._pc.getStats().then(data => {"
        + "   [...data.values()].forEach(function(e) {"
        + "       if (e.type.startsWith('outbound-rtp')) {"
        + "           if (e.kind == 'video') {"
        + "               window.pubTotalSentBytes = e.bytesSent;"
        + "           }"
        + "       }"
        + "   });"
        + "});";
  }

  private String getStashedPublishTotalSentBytesScript() {
    return "return window.pubTotalSentBytes;";
  }
}
