package com.dso34bt.jobportal.utilities;

import java.net.URL;

public final class Validate {
    public static boolean isUrlValid(String url)
    {
        /* Try creating a valid URL */
        try {
            new URL(url).toURI();
            return true;
        }
        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }
}
