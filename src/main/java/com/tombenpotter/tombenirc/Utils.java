package com.tombenpotter.tombenirc;

public class Utils {

    public static String getNumbersInString(String message) {
        String result = "";
        int i = 0;

        for (char c : message.toCharArray()) {
            switch (c) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (i < 3) {
                        result += c;
                    }
                    i++;
                    break;

                default:
                    ;
            }
        }

        return result;
    }
}
