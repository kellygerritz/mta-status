package kelly.gerritz.mtastatus.util;

import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class NowUtil {

    public ZonedDateTime getNow() {
        return ZonedDateTime.now();
    }
}
