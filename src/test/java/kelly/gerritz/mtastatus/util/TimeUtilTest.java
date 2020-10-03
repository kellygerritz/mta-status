package kelly.gerritz.mtastatus.util;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TimeUtilTest {

    private NowUtil nowUtil;
    private TimeUtil subject;

    @BeforeEach
    public void beforeAll() {
        this.nowUtil = mock(NowUtil.class);
        this.subject = new TimeUtil(nowUtil);
    }

    @Test
    void isCurrentEndDate_whenEndDateisNull_returnsTrue() {
        JSONObject jsonObject = new JSONObject();
        boolean currentEndDate = subject.isCurrentEndDate(jsonObject);

        assertTrue(currentEndDate);
    }

    @Test
    void isCurrentEndDate_whenEndDateisAfterNow_returnsTrue() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("endDate", "2020-10-04T23:59:00-0400");

        when(nowUtil.getNow()).thenReturn(ZonedDateTime.of(2020, 10, 3, 0, 0, 0, 0, ZoneId.of("America/New_York")));
        boolean currentEndDate = subject.isCurrentEndDate(jsonObject);

        assertTrue(currentEndDate);
    }

    @Test
    void isCurrentEndDate_whenEndDateisBeforeNow_returnsFalse() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("endDate", "2020-10-03T23:59:00-0400");

        when(nowUtil.getNow()).thenReturn(ZonedDateTime.of(2020, 10, 4, 0, 0, 0, 0, ZoneId.of("America/New_York")));
        boolean currentEndDate = subject.isCurrentEndDate(jsonObject);

        assertFalse(currentEndDate);
    }
}