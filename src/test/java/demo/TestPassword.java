package demo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

public class TestPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("zaq1XSW2"));

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.stream().forEach(System.out::println);
    }
}
