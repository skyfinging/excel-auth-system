package demo.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AesUtilTest {
    @Test
    public void test(){
        String abc = "123456abc";
        String password = AESUtil.encode(abc);
        System.out.println(password);
        String result = AESUtil.decode(password);
        assertEquals("123456abc", result);
    }
}
