package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static ru.netology.data.DataGenerator.*;

public class CardDeliveryTest {

    private final String date = generateDate(3);
    private final String secondDate = generateDate(15);
    private final String expiredDate = generateDate(-15);
    private final String city = generateCity();
    private final String phone = generatePhone();
    private final String name = generateName();


    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }
 
    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setupTest() {
        open("http://localhost:9999");
    }

    @Test
    void shouldOrderCardDelivery() {
        $("[placeholder='Город']").setValue(city);
        $("[placeholder='Дата встречи']").sendKeys(Keys.CONTROL + "a");
        $("[placeholder='Дата встречи']").sendKeys(Keys.DELETE);
        $("[placeholder='Дата встречи']").setValue(date);

        $("[name='name']").setValue(name);
        $("[name='phone']").setValue(phone);
        $("[class='checkbox__box']").click();
        $(withText("Запланировать")).click();

        String notificationContent;
        notificationContent = "Встреча успешно запланирована на " + date;
        $("[class='notification__content']").
                shouldBe(visible, Duration.ofSeconds(15)).shouldHave(exactText(notificationContent));
    }

    @Test
    void shouldOrderNewDateCardDelivery() {
        $("[placeholder='Город']").setValue(city);
        $("[placeholder='Дата встречи']").sendKeys(Keys.CONTROL + "a");
        $("[placeholder='Дата встречи']").sendKeys(Keys.DELETE);
        $("[placeholder='Дата встречи']").setValue(date);

        $("[name='name']").setValue(name);
        $("[name='phone']").setValue(phone);
        $("[class='checkbox__box']").click();
        $(withText("Запланировать")).click();

        String notificationContent;
        notificationContent = "Встреча успешно запланирована на " + date;
        $("[class='notification__content']").
                shouldBe(visible, Duration.ofSeconds(15)).shouldHave(exactText(notificationContent));

        $("[placeholder='Дата встречи']").sendKeys(Keys.CONTROL + "a");
        $("[placeholder='Дата встречи']").sendKeys(Keys.DELETE);
        $("[placeholder='Дата встречи']").setValue(secondDate);
        $(withText("Запланировать")).click();

        $$("button").find((exactText("Перепланировать"))).click();

        notificationContent = "Встреча успешно запланирована на " + secondDate;

        $("[class='notification__content']").
                shouldBe(visible, Duration.ofSeconds(15)).shouldHave(exactText(notificationContent));
    }

    @Test
    void shouldGetErrorFromDate() {
        $("[placeholder='Город']").setValue("Са");
        $(byText("Санкт-Петербург")).click();
        $("[placeholder='Дата встречи']").sendKeys(Keys.CONTROL + "a");
        $("[placeholder='Дата встречи']").sendKeys(Keys.DELETE);
        $("[placeholder='Дата встречи']").setValue(expiredDate);

        $("[name='name']").setValue(name);
        $("[name='phone']").setValue(phone);
        $("[class='checkbox__box']").click();
        $(withText("Запланировать")).click();

        $(withText("Заказ на выбранную дату невозможен")).
                shouldBe(visible, Duration.ofSeconds(15));

    }
}
