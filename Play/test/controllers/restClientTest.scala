package controllers

import Rest.restClient
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import org.junit.runner._
import org.scalatestplus.junit.JUnitRunner
import play.api.test._
import play.test.WithBrowser

@RunWith(classOf[JUnitRunner])
class restClientControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "HomeController GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new restClient(stubControllerComponents())
      val home = controller.logAPI().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
    }

    "render the index page from the application" in {
      val controller = inject[restClient]
      val home = controller.logAPI().apply(FakeRequest(GET, "/"))
      status(home) mustBe OK

    }

    "render the page from the router" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get
      status(home) mustBe NOT_FOUND
    }

//    "Application" should {
//
//      "Work from a browser" in new WithBrowser {
//
//        browser.goTo("http://localhost:9000/logAPI")
//        browser.pageSource must contain("\"{\\\"isPresent\\\": \\\"True\\\"}\"")
//      }
//    }
  }
}
