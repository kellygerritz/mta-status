package kelly.gerritz.mtastatus.util;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TimeUtil {
    private static final String END_DATE = "endDate";

    private static final DateTimeFormatter MTA_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssx");

    private final NowUtil nowUtil;

    @Autowired
    public TimeUtil(NowUtil nowUtil) {
        this.nowUtil = nowUtil;
    }

    public boolean isCurrentEndDate(JSONObject statusDetail) {
        boolean isCurrent = statusDetail.isNull(END_DATE);

        if (!isCurrent) {
            String string = statusDetail.getString(END_DATE);
            ZonedDateTime parse = ZonedDateTime.parse(string, MTA_TIME_FORMATTER);
            isCurrent = parse.isAfter(nowUtil.getNow());
        }

        return isCurrent;
    }
}
