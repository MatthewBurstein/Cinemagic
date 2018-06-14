package org.softwire.training.cinemagic.integration;

import com.google.common.base.Function;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.softwire.training.cinemagic.models.Cinema;
import org.softwire.training.cinemagic.models.Film;
import org.softwire.training.cinemagic.models.Screen;
import org.softwire.training.cinemagic.models.Showing;
import org.softwire.training.cinemagic.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class BookingTest {
    @Rule
    public final WebDriverLoggingRule webDriverLoggingRule = new WebDriverLoggingRule();

    @LocalServerPort
    protected Integer port;

    @Autowired
    protected WebDriver driver;

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private ScreenService screenService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private ShowingService showingService;

    @Test
    public void testTitle() {
        navigateToBookingPage();
        assertThat("Cinemagic", equalTo(driver.getTitle()));
    }

    @Test
    public void testMainlineBookingFlow() {
        String cinemaName = "Test Cinema";
        String screenName = "Test Screen";
        String filmName = "Test Film";
        createTestData(cinemaName, screenName, filmName);

        navigateToBookingPage();
        waitForElement(By.className("cinema-select"));

        // Select the cinema
        WebElement cinemaWidget = driver.findElement(By.className("cinema-select"));
        Select select = new Select(cinemaWidget.findElement(By.tagName("select")));
        select.selectByVisibleText(cinemaName);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        waitForElement(By.className("film-time-select"));

        driver.findElement(By.className("film-selection-button")).click();
        waitForElement(By.className("seat-select"));

        driver.findElement(By.xpath("//button[text()='U']")).click();

        System.out.println(driver.findElement(By.tagName("body")).getText());
        driver.findElement(By.id("seat-select-book-button")).click();
        waitForElement(By.className("booking-success"));
        WebElement element = driver.findElement(By.className("booking-success"));
        assertThat("Confirms booking", element.getText().contains("Booking Successful!"));
    }

    private void createTestData(String cinemaName, String screenName, String filmName) {
        Cinema cinema = createTestCinema(cinemaName);
        Screen screen = createTestScreen(screenName, cinema);
        Film film = createTestFilm(filmName);
        createTestShowing(screen, film);
    }

    private Cinema createTestCinema(String cinemaName) {
        Cinema cinema = new Cinema();
        cinema.setName(cinemaName);
        cinemaService.create(cinema);
        return cinema;
    }

    private Screen createTestScreen(String screenName, Cinema cinema) {
        Screen screen = new Screen();
        screen.setName(screenName);
        screen.setRows(10);
        screen.setRowWidth(10);
        screen.setCinema(cinema);
        screenService.create(screen);
        return screen;
    }

    private Film createTestFilm(String filmName) {
        Film film = new Film();
        film.setName(filmName);
        film.setLengthMinutes(130);
        filmService.create(film);
        return film;
    }

    private Showing createTestShowing(Screen screen, Film film) {
        Instant time = Instant.now().minusSeconds(86000);
        Showing showing = new Showing();
        showing.setScreen(screen);
        showing.setFilm(film);
        showing.setTime(time);
        showingService.create(showing);
        return showing;
    }

    private void navigateToBookingPage() {
        driver.get("http://localhost:" + port + "/booking");
    }

    @SuppressWarnings("ConstantConditions")
    private WebElement waitForElement(By locator) {
        return shortWait().until((Function<? super WebDriver, WebElement>) webDriver -> webDriver.findElement(locator));
    }

    private WebDriverWait shortWait() {
        return new WebDriverWait(driver, 10, 100);
    }
}
