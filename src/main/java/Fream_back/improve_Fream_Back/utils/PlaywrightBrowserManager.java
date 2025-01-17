package Fream_back.improve_Fream_Back.utils;

import com.microsoft.playwright.*;

public class PlaywrightBrowserManager {
    private Playwright playwright;
    private Browser browser;

    public void openBrowser() {
        if (playwright == null) {
            this.playwright = Playwright.create();
        }
        if (browser == null) {
            this.browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            );
        }
    }

    public Page newPage() {
        if (browser == null) {
            throw new IllegalStateException("Browser is not opened. Call openBrowser() first.");
        }
        return browser.newPage();
    }

    public void closeBrowser() {
        if (browser != null) {
            browser.close(); // 모든 Page도 함께 닫힘
            browser = null;
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }
}
