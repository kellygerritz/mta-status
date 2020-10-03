package kelly.gerritz.mtastatus.service;

import kelly.gerritz.mtastatus.clients.MtaClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Service
public class MtaStatusService {
    // Approximate period between MTA updates
    static private final long WAIT_MILLISECOND = 30000L;
    private static final String DELAYS_HEADER = "Delays";
    private static final String END_DATE = "endDate";
    // 2020-10-03T12:50:26-0400
    private static final DateTimeFormatter MTA_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssx");
    private long upTime;


    private final MtaClient mtaClient;
    private final HashMap<String, StatusTime> statuses;

    @Autowired
    public MtaStatusService(MtaClient mtaClient) {
        this.mtaClient = mtaClient;
        this.upTime = -WAIT_MILLISECOND;
        this.statuses = new HashMap<>();
    }

    public HashMap<String, StatusTime> getStatuses() {
        return statuses;
    }

    public Long getUptime() {
        return upTime;
    }

    @Scheduled(fixedDelay = WAIT_MILLISECOND)
    public void updateMtaStatuses() {
        upTime = upTime + WAIT_MILLISECOND;
        String mtaJson = mtaClient.fetchMtaInfo();
        JSONObject baseJson = new JSONObject(mtaJson);

        JSONArray routeDetails = baseJson.getJSONArray("routeDetails");
        for (int i = 0; i < routeDetails.length(); i++) {
            JSONObject routeDetail = routeDetails.getJSONObject(i);
            String mode = routeDetail.getString("mode");
            Boolean inService = routeDetail.getBoolean("inService");

            if (mode.equalsIgnoreCase("subway") && inService) {
                String route = routeDetail.getString("route").toUpperCase();
                StatusEnum currentStatus = getStatusEnum(routeDetail);

                StatusTime statusTime = statuses.get(route);
                Long duration = 0L;
                if (statusTime != null) {
                    StatusEnum oldStatus = statusTime.getStatusEnum();
                    printStatus(currentStatus, oldStatus, route);
                    duration = statusTime.getDelayedDuration();
                    if (currentStatus == StatusEnum.Delayed && oldStatus == StatusEnum.Delayed) {
                        duration = duration + WAIT_MILLISECOND;
                    }
                }

                statuses.put(route, new StatusTime(currentStatus, duration));
            }
        }

    }

    private void printStatus(StatusEnum currentStatus, StatusEnum oldStatus, String route) {
        if (oldStatus != currentStatus) {
            if (currentStatus == StatusEnum.Delayed) {
                System.out.printf("Line %s is experiencing delays\n", route);
            } else {
                System.out.printf("Line %s is now recovered\n", route);
            }
        }
    }

    private StatusEnum getStatusEnum(JSONObject routeDetail) {
        StatusEnum statusEnum = StatusEnum.NotDelayed;

        if (routeDetail.has("statusDetails")) {
            JSONArray statusDetails = routeDetail.getJSONArray("statusDetails");
            for (int i = 0; i < statusDetails.length(); i++) {
                JSONObject statusDetail = statusDetails.getJSONObject(i);

                String statusSummary = statusDetail.getString("statusSummary");

                if ( isCurrent(statusDetail) && statusSummary.equalsIgnoreCase(DELAYS_HEADER)) {
                    statusEnum = StatusEnum.Delayed;
                }
            }
        }

        return statusEnum;
    }

    private boolean isCurrent(JSONObject statusDetail) {
        boolean isCurrent = statusDetail.isNull(END_DATE);

        if (!isCurrent) {
            String string = statusDetail.getString(END_DATE);
            ZonedDateTime parse = ZonedDateTime.parse(string, MTA_TIME_FORMATTER);
            parse.isAfter(ZonedDateTime.now());
        }

        return isCurrent;
    }
}
