package com.excilys.formation.exos.mapper;

import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Mapper from String to List of HashMap<String, String>
 */
public class StringToHashMapList {

    /**
     * Split each element separated by ':' into two elements
     * The transformation is shown by the following example : {a:b, c:d} -> {{a, b}, {c, d}}
     * @param messages the array containing the strings
     * @return the double-dimension array containing the splitted datas
     */
    private static String[][] splitIntoArray(String[] messages) {
        String[][] msg = new String[messages.length][2];
        for (int i = 0; i < messages.length; i++) {
            String[] info = messages[i].split(":");
            if (info.length == 2) {
                msg[i][0] = info[0];
                msg[i][1] = info[1];
            }
        }
        return msg;
    }

    /**
     * Convert a String of size 2 into a HashMap
     * @param msg the String of size 2
     * @param key1 the first key
     * @param key2 the second key
     * @return the result HashMap
     */
    private static HashMap<String, String> stringArrayToHashMap(String[] msg, String key1, String key2) {
        HashMap<String, String> element = new HashMap<>();
        element.put(key1, msg[0]);
        element.put(key2, msg[1]);
        return  element;
    }

    /**
     * Convert the server String into a List of HashMap
     * @param s the String containing the messages separated by semi-colon
     * @param key1 the first key for user name association
     * @param key2 the second key for message association
     * @return the result List containing the HashMaps of the messages
     */
    public static List<HashMap<String, String>> convert(String s, String key1, String key2) {
        String[] split = s.split(";");
        String[][] messages = splitIntoArray(split);
        List<HashMap<String, String>> result = new ArrayList<>();
        for (String[] message : messages) {
            HashMap<String, String> element = stringArrayToHashMap(message, key1, key2);
            result.add(element);
        }
        return result;
    }
}
