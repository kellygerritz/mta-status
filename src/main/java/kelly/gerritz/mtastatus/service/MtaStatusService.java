package kelly.gerritz.mtastatus.service;

import kelly.gerritz.mtastatus.clients.MtaClient;
import kelly.gerritz.mtastatus.util.TimeUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class MtaStatusService {
    // Approximate period between MTA updates
    static private final long WAIT_MILLISECOND = 30000L;

    // Json constants
    private static final String DELAYS_HEADER = "Delays";
    public static final String STATUS_DETAILS = "statusDetails";
    public static final String STATUS_SUMMARY = "statusSummary";
    public static final String ROUTE_DETAILS = "routeDetails";
    public static final String MODE = "mode";
    public static final String IN_SERVICE = "inService";
    public static final String SUBWAY = "subway";
    public static final String ROUTE = "route";

    private long upTime;

    private final MtaClient mtaClient;
    private final TimeUtil timeUtil;
    private final HashMap<String, StatusTime> statuses;

    @Autowired
    public MtaStatusService(MtaClient mtaClient, TimeUtil timeUtil) {
        this.mtaClient = mtaClient;
        this.timeUtil = timeUtil;
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
        if (mtaJson != null && !mtaJson.isEmpty()) {

            JSONObject baseJson = new JSONObject(mtaJson);

            JSONArray routeDetails = baseJson.getJSONArray(ROUTE_DETAILS);
            for (int i = 0; i < routeDetails.length(); i++) {
                JSONObject routeDetail = routeDetails.getJSONObject(i);
                String mode = routeDetail.getString(MODE);
                boolean inService = routeDetail.getBoolean(IN_SERVICE);

                if (mode.equalsIgnoreCase(SUBWAY) && inService) {
                    String route = routeDetail.getString(ROUTE).toUpperCase();
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

        if (routeDetail.has(STATUS_DETAILS)) {
            JSONArray statusDetails = routeDetail.getJSONArray(STATUS_DETAILS);
            for (int i = 0; i < statusDetails.length(); i++) {
                JSONObject statusDetail = statusDetails.getJSONObject(i);

                String statusSummary = statusDetail.getString(STATUS_SUMMARY);

                if (timeUtil.isCurrentEndDate(statusDetail) && statusSummary.equalsIgnoreCase(DELAYS_HEADER)) {
                    statusEnum = StatusEnum.Delayed;
                }
            }
        }

        return statusEnum;
    }
}
