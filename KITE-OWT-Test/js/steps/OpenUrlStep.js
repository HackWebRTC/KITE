const {TestStep} = require('kite-common');

class OpenUrlStep extends TestStep {
  constructor(kiteBaseTest) {
    super();
    this.driver = kiteBaseTest.driver;
    this.timeout = kiteBaseTest.timeout;
    this.url = kiteBaseTest.url;
    this.uuid = kiteBaseTest.uuid;
    this.page = kiteBaseTest.page;
  }

  stepDescription() {
    return 'Open ' + this.url + '';
  }

  async step() {
    await this.page.open(this);
  }
}

module.exports = OpenUrlStep;