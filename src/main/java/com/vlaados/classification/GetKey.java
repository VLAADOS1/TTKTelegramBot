package com.vlaados.classification;

public class GetKey {
    public static String getkey(int number) {
        switch (number) {
            case 1:
                return "appmax";
            case 2:
                return "appmosh";
            case 3:
                return "apphonest";
            case 4:
                return "appkasper";
            case 5:
                return "appip";
            case 6:
                return "appmen";
            case 7:
                return "approuter";
            case 8:
                return "null";
            default:
                return "error";
        }
    }
}
