package umonaz.omnibus;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import umonaz.omnibus.utils.KstTime;

@SpringBootApplication
@EnableAsync
public class OmnibusApplication {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone(KstTime.ZONE));
    }

    public static void main(String[] args) {
        SpringApplication.run(OmnibusApplication.class, args);
    }
}
