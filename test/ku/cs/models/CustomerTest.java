package ku.cs.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void getroomNum() {
        Customer customer = new Customer(101, "1234567891011", "nasom samrong", "Nonthaburi", "0212343213", "10000", 2000);
        int roomNum = customer.getRoomNum();
        assertEquals(roomNum, 101);
    }

    @Test
    void getIdCard() {
        Customer customer = new Customer(101, "1234567891011", "nasom samrong", "Nonthaburi", "0212343213", "10000", 2000);
        String IdCard = customer.getIdCard();
        assertEquals(IdCard, "1234567891011");
    }

    @Test
    void getName() {
        Customer customer = new Customer(101, "1234567891011", "nasom samrong", "Nonthaburi", "0212343213", "10000", 2000);
        String name = customer.getName();
        assertEquals(name, "nasom samrong");
    }

    @Test
    void getAddress() {
        Customer customer = new Customer(101, "1234567891011", "nasom samrong", "Nonthaburi", "0212343213", "10000", 2000);
        String address = customer.getAddress();
        assertEquals(address, "Nonthaburi");
    }

    @Test
    void getPhoneNum() {
        Customer customer = new Customer(101, "1234567891011", "nasom samrong", "Nonthaburi", "0212343213", "10000", 2000);
        String phoneNumber = customer.getPhoneNum();
        assertEquals(phoneNumber, "0212343213");
    }

    @Test
    void getRentalPeriod() {
        Customer customer = new Customer(101, "1234567891011", "nasom samrong", "Nonthaburi", "0212343213", "10000", 2000);
        String rentalPeriod= customer.getRentalPeriod();
        assertEquals(rentalPeriod, "10000");
    }

    @Test
    void getDeposit() {
        Customer customer = new Customer(101, "1234567891011", "nasom samrong", "Nonthaburi", "0212343213", "10000", 2000);
        float deposit = customer.getDeposit();
        assertEquals(deposit, 2000.00);
    }

    @Test
    void getOwed() {
        Customer customer = new Customer(101, "1234567891011", "nasom samrong", "Nonthaburi", "0212343213", "10000", 2000);
        float owed = customer.getRoomNum();
        assertEquals(owed, 101.0);
    }


}