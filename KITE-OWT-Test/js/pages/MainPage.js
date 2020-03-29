const {By, Key} = require('selenium-webdriver');
const {TestUtils, KiteTestError, Status} = require('kite-common'); 

class MainPage {
  constructor(driver) {
    this.driver = driver;
  }

  async open(url) {
    await TestUtils.open(url);
  }
}

module.exports = MainPage;