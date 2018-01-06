/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.luxtech.movie.client.common;

import java.net.MalformedURLException;
import java.net.URL;

public class ValidateUtils {

    public static boolean validateUrl(String url) {

        try {
            new URL(url);
            return true;

        } catch (MalformedURLException ex) {
            return false;
        }

    }
}
