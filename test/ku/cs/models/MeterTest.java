package ku.cs.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
class MeterTest {

    @Test
    void getRoomNum() {
        Meter meter = new Meter(201, LocalDate.parse("2023-11-06"), "0021", "00100", 0035, 00020, 20, 15, 2700,3000);
        int roomNum = meter.getRoomNum();
        assertEquals(roomNum, 201);
    }

    @Test
    void getMeterDate() {
        Meter meter = new Meter(201, LocalDate.parse("2023-11-06"), "0021", "00100", 0035, 00020, 20, 15, 2700,3000);
        LocalDate meterDate = meter.getMeterDate();
        assertEquals(meterDate, LocalDate.parse("2023-11-06"));
    }

    @Test
    void getWaterMeter() {
        Meter meter = new Meter(201, LocalDate.parse("2023-11-06"), "0021", "00100", 0035, 00020, 20, 15, 2700,3000);
        String waterMeter = meter.getWaterMeter();
        assertEquals(waterMeter, "0021");
    }

    @Test
    void getElectricMeter() {
        Meter meter = new Meter(201, LocalDate.parse("2023-11-06"), "0021", "00100", 0035, 00020, 20, 15, 2700,3000);
        String elecMeter = meter.getElectMeter();
        assertEquals(elecMeter, "00100");
    }

    @Test
    void getWaterUnitPrice() {
        Meter meter = new Meter(201, LocalDate.parse("2023-11-06"), "0021", "00100", 0035, 00020, 20, 15, 2700,3000);
        int waterunitprice = meter.getWaterUnitPrice();
        assertEquals(waterunitprice, 20);
    }

    @Test
    void getElectUnitPrice() {
        Meter meter = new Meter(201, LocalDate.parse("2023-11-06"), "0021", "00100", 0035, 00020, 20, 15, 2700,3000);
        int electricunitprice = meter.getElectUnitPrice();
        assertEquals(electricunitprice, 15);
    }

    @Test
    void getWaterPrice() {
        Meter meter = new Meter(201, LocalDate.parse("2023-11-06"), "0021", "00100", 0035, 00020, 20, 15, 2700,3000);
        float waterprice = meter.getWaterPrice();
        assertEquals(waterprice, 2700.00);
    }

    @Test
    void getElectPrice() {
        Meter meter = new Meter(201, LocalDate.parse("2023-11-06"), "0021", "00100", 0035, 00020, 20, 15, 2700,3000);
        float electricprice = meter.getElectPrice();
        assertEquals(electricprice, 3000.00);
    }

    @Test
    void getWaterUnit() {
        Meter meter = new Meter(201, LocalDate.parse("2023-11-06"), "0021", "00100", 0035, 00020, 20, 15, 2700,3000);
        int waterunit = meter.getWaterUnit();
        assertEquals(waterunit, 29);
    }

    @Test
    void getElectUnit() {
        Meter meter = new Meter(201, LocalDate.parse("2023-11-06"), "0021", "00100", 0035, 00020, 20, 15, 2700,3000);
        int electricunit = meter.getElectUnit();
        assertEquals(electricunit, 16);
    }
}