const {TestStep, KiteTestError, Status} = require('kite-common');

const checkPass = true;


class MyFirstCheck extends TestStep {
  constructor(kiteBaseTest) {
    super();
    this.driver = kiteBaseTest.driver;
    this.timeout = kiteBaseTest.timeout;
    this.page = kiteBaseTest.page;

    // Test reporter if you want to add attachment(s)
    this.testReporter = kiteBaseTest.reporter;
  }

  stepDescription() {
    return "MyFirstCheck checks that checkPass is true";
  }

  async step() {
    try {
      this.testReporter.textAttachment(this.report, "MyFirstCheck", "" + checkPass, "plain");
      if (!checkPass) {
        throw new KiteTestError(Status.FAILED, "MyFirstCheck failed");
      }
    } catch (error) {
      console.log(error);
      if (error instanceof KiteTestError) {
        throw error;
      } else {
        throw new KiteTestError(Status.BROKEN, "MyFirstCheck error : " + error);
      }
    }
  }
}

module.exports = MyFirstCheck;