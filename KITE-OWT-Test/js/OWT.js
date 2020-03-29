const {TestUtils, WebDriverFactory, KiteBaseTest} = require('./node_modules/kite-common'); 
const {OpenUrlStep} = require('./steps');
const {MyFirstCheck} = require('./checks');
const {MainPage} = require('./pages');

class OWT extends KiteBaseTest {
  constructor(name, kiteConfig) {
    super(name, kiteConfig);
  }
  
  async testScript() {
    try {
      this.driver = await WebDriverFactory.getDriver(this.capabilities, this.remoteUrl);
      this.page = new MainPage(this.driver);

      let openUrlStep = new OpenUrlStep(this);
      await openUrlStep.execute(this);

      let myFirstCheck = new MyFirstCheck(this);
      await myFirstCheck.execute(this);

    } catch (e) {
      console.log(e);
    } finally {
      await this.driver.quit();
    }
  }
}

module.exports= OWT;

(async () => {
  const kiteConfig = await TestUtils.getKiteConfig(__dirname);
  let test = new OWT('OWT test', kiteConfig);
  await test.run();
})();
