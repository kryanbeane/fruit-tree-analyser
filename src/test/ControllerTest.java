package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sample.Controller;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ControllerTest {
    Controller controller;
    @BeforeEach
    void setUp() {
        controller = new Controller();
    }

    @Test
    public void compareHueShouldReturnTrueIfFirstHueMinusSecondHueIsLessThanDifference() {
        assertTrue(controller.compareHue(5, 1, 5));
    }
    @Test
    public void compareSaturationShouldReturnTrueIfFirstHueMinusSecondHueIsLessThanDifference() {
        assertTrue(controller.compareSaturation(5, 1, 5));
    }
    @Test
    public void compareBrightnessShouldReturnTrueIfFirstHueMinusSecondHueIsLessThanDifference() {
        assertTrue(controller.compareBrightness(5, 1, 5));
    }
}