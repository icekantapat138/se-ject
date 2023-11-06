package ku.cs.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceTest {

    @Test
    void getRoomNum() {
        Invoice invoice = new Invoice(101, 20116112022L, LocalDate.parse("2023-11-06"), 10000, 110000, 1);
        int roomNum = invoice.getRoomNum();
        assertEquals(roomNum, 101);
    }

    @Test
    void getRoomNumStr() {
        Invoice invoice = new Invoice(101, 20116112022L, LocalDate.parse("2023-11-06"), 10000, 110000, 1);
        String roommNumStr = invoice.getRoomNumStr();
        assertEquals(roommNumStr, "101");
    }

    @Test
    void getInvoiceNum() {
        Invoice invoice = new Invoice(101, 20116112022L, LocalDate.parse("2023-11-06"), 10000, 110000, 1);
        long invoiceNum = invoice.getInvoiceNum();
        assertEquals(invoiceNum,20116112022L);
    }

    @Test
    void getInvoiceNumStr() {
        Invoice invoice = new Invoice(101, 20116112022L, LocalDate.parse("2023-11-06"), 10000, 110000, 1);
        String invoiceNumStr = invoice.getInvoiceNumStr();
        assertEquals(invoiceNumStr, "20116112022");
    }

    @Test
    void getInvoiceDate() {
        Invoice invoice = new Invoice(101, 20116112022L, LocalDate.parse("2023-11-06"), 10000, 110000, 1);
        LocalDate invoiceDate = invoice.getInvoiceDate();
        assertEquals(invoiceDate, LocalDate.parse("2023-11-06"));
    }

    @Test
    void getTotalPrice() {
        Invoice invoice = new Invoice(101, 20116112022L, LocalDate.parse("2023-11-06"), 10000, 110000, 1);
        float totalPrice = invoice.getTotalPrice();
        assertEquals(totalPrice, 10000.00);
    }

    @Test
    void getPaidStatus() {
        Invoice invoice = new Invoice(101, 20116112022L, LocalDate.parse("2023-11-06"), 10000, 110000, 1);
        int paidStatus = invoice.getPaidStatus();
        assertEquals(paidStatus, 1);
    }
}