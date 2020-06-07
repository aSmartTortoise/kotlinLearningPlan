package com.example.equality;

public class Main {
    public static void main(String[] args) {
        Person personA = new Person("John", 25);
        Object someOne = personA;
        Person personB = (Person) someOne;
        if (someOne instanceof Person) {
            System.out.println(((Person) someOne).getName());
        }
    }

}
