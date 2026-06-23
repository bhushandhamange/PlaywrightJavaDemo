package com.example.com.domain;

import net.datafaker.Faker;

import java.text.SimpleDateFormat;

public record User(
        String first_name,
        String last_name,
        Address address,
        String phone,
        String dob,
        String password,
        String email
) {
    public User withPassword(String password) {
        return new User(first_name, last_name, address, phone, dob, password, email);
    }

    public User withFirstName(String first_name) {
        return new User(first_name, last_name, address, phone, dob, password, email);
    }

    public record Address(
            String street,
            String house_number,
            String city,
            String state,
            String country,
            String postal_code
    ) {

        public static Address randomAddress(){

            Faker faker = new Faker();

            return new Address(
                    faker.address().streetName(),
                    faker.address().buildingNumber(),
                    faker.address().city(),
                    faker.address().state(),
                    faker.address().country(),
                    faker.address().zipCode()
            );
        }
    }

    public static User randomUser(){
        Faker faker = new Faker();

        return new User(
                faker.name().firstName(),
                faker.name().lastName(),
                Address.randomAddress(),
                faker.phoneNumber().cellPhone(),
                "1970-01-01",
                "Az123!&xyz",
                faker.internet().emailAddress()
        );
    }


}