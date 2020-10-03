package kelly.gerritz.mtastatus.service;

import kelly.gerritz.mtastatus.clients.MtaClient;
import kelly.gerritz.mtastatus.util.TimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MtaStatusServiceTest {

    private MtaClient mtaClient;
    private TimeUtil timeUtil;
    private MtaStatusService subject;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        this.mtaClient = mock(MtaClient.class);
        this.timeUtil = mock(TimeUtil.class);
        when(timeUtil.isCurrentEndDate(any())).thenReturn(true);
        this.subject = new MtaStatusService(mtaClient, timeUtil);
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void updateMtaStatus_whenServiceRecovers_printsLineIsNowRecoveredAndSetsStatus() {
        when(mtaClient.fetchMtaInfo()).thenReturn("{\n" +
                "  \"lastUpdated\": \"2020-10-03T14:56:42-0400\",\n" +
                "  \"routeDetails\": [{" +
                "      \"route\": \"B\",\n" +
                "      \"color\": \"6CBE45\",\n" +
                "      \"mode\": \"subway\",\n" +
                "      \"agency\": \"MTA NYCT\",\n" +
                "      \"routeId\": \"MTA NYCT_M14D+\",\n" +
                "      \"inService\": true,\n" +
                "      \"routeType\": 3,\n" +
                "      \"statusDetails\": [{\n" +
                "          \"id\": \"MTA NYCT_267718\",\n" +
                "          \"statusSummary\": \"Delays\",\n" +
                "          \"priority\": 11,\n" +
                "          \"direction\": \"1\",\n" +
                "          \"creationDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"startDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"endDate\": null\n" +
                "        }]}]}");

        subject.updateMtaStatuses();
        assertEquals(StatusEnum.Delayed, subject.getStatuses().get("B").getStatusEnum());

        Mockito.reset(mtaClient);

        when(mtaClient.fetchMtaInfo()).thenReturn("{\n" +
                "  \"lastUpdated\": \"2020-10-03T14:56:42-0400\",\n" +
                "  \"routeDetails\": [{" +
                "      \"route\": \"B\",\n" +
                "      \"color\": \"6CBE45\",\n" +
                "      \"mode\": \"subway\",\n" +
                "      \"agency\": \"MTA NYCT\",\n" +
                "      \"routeId\": \"MTA NYCT_M14D+\",\n" +
                "      \"inService\": true,\n" +
                "      \"routeType\": 3,\n" +
                "      \"statusDetails\": [{\n" +
                "          \"id\": \"MTA NYCT_267718\",\n" +
                "          \"statusSummary\": \"No Delays\",\n" +
                "          \"priority\": 11,\n" +
                "          \"direction\": \"1\",\n" +
                "          \"creationDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"startDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"endDate\": null\n" +
                "        }]}]}");
        subject.updateMtaStatuses();

        assertEquals(StatusEnum.NotDelayed, subject.getStatuses().get("B").getStatusEnum());
        assertEquals("Line B is now recovered\n", outContent.toString());
    }

    @Test
    public void updateMtaStatus_whenServiceGetsDelayed_printsLineIsDelayed() {
        when(mtaClient.fetchMtaInfo()).thenReturn("{\n" +
                "  \"lastUpdated\": \"2020-10-03T14:56:42-0400\",\n" +
                "  \"routeDetails\": [{" +
                "      \"route\": \"B\",\n" +
                "      \"color\": \"6CBE45\",\n" +
                "      \"mode\": \"subway\",\n" +
                "      \"agency\": \"MTA NYCT\",\n" +
                "      \"routeId\": \"MTA NYCT_M14D+\",\n" +
                "      \"inService\": true,\n" +
                "      \"routeType\": 3,\n" +
                "      \"statusDetails\": [{\n" +
                "          \"id\": \"MTA NYCT_267718\",\n" +
                "          \"statusSummary\": \"No Delays\",\n" +
                "          \"priority\": 11,\n" +
                "          \"direction\": \"1\",\n" +
                "          \"creationDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"startDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"endDate\": null\n" +
                "        }]}]}");
        subject.updateMtaStatuses();

        Mockito.reset(mtaClient);

        when(mtaClient.fetchMtaInfo()).thenReturn("{\n" +
                "  \"lastUpdated\": \"2020-10-03T14:56:42-0400\",\n" +
                "  \"routeDetails\": [{" +
                "      \"route\": \"B\",\n" +
                "      \"color\": \"6CBE45\",\n" +
                "      \"mode\": \"subway\",\n" +
                "      \"agency\": \"MTA NYCT\",\n" +
                "      \"routeId\": \"MTA NYCT_M14D+\",\n" +
                "      \"inService\": true,\n" +
                "      \"routeType\": 3,\n" +
                "      \"statusDetails\": [{\n" +
                "          \"id\": \"MTA NYCT_267718\",\n" +
                "          \"statusSummary\": \"Delays\",\n" +
                "          \"priority\": 11,\n" +
                "          \"direction\": \"1\",\n" +
                "          \"creationDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"startDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"endDate\": null\n" +
                "        }]}]}");

        subject.updateMtaStatuses();

        assertEquals("Line B is experiencing delays\n", outContent.toString());
    }

    @Test
    public void updateMtaStatus_whenGivenNonSubwayOrInactiveLine_ignoresRoutes() {
        when(mtaClient.fetchMtaInfo()).thenReturn("{\n" +
                "  \"lastUpdated\": \"2020-10-03T14:56:42-0400\",\n" +
                "  \"routeDetails\": [{" +
                "    \n" +
                "      \"route\": \"B\",\n" +
                "      \"color\": \"6CBE45\",\n" +
                "      \"mode\": \"subway\",\n" +
                "      \"agency\": \"MTA NYCT\",\n" +
                "      \"routeId\": \"MTA NYCT_M14D+\",\n" +
                "      \"inService\": false,\n" +
                "      \"routeType\": 3,\n" +
                "      \"statusDetails\": [{\n" +
                "          \"id\": \"MTA NYCT_267718\",\n" +
                "          \"statusSummary\": \"No Delays\",\n" +
                "          \"priority\": 11,\n" +
                "          \"direction\": \"1\",\n" +
                "          \"creationDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"startDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"endDate\": null\n" +
                "        }]},\n" +
                "    {      \n" +
                "      \"route\": \"C\",\n" +
                "      \"color\": \"6CBE45\",\n" +
                "      \"mode\": \"Bus\",\n" +
                "      \"agency\": \"MTA NYCT\",\n" +
                "      \"routeId\": \"MTA NYCT_M14D+\",\n" +
                "      \"inService\": true,\n" +
                "      \"routeType\": 3,\n" +
                "      \"statusDetails\": [{\n" +
                "        \"id\": \"MTA NYCT_267718\",\n" +
                "        \"statusSummary\": \"No Delays\",\n" +
                "        \"priority\": 11,\n" +
                "        \"direction\": \"1\",\n" +
                "        \"creationDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "        \"startDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "        \"endDate\": null\n" +
                "      }]}]}");
        subject.updateMtaStatuses();

        assertTrue(subject.getStatuses().isEmpty());
    }

    @Test
    public void updateMtaStatus_whenGivenNoStatusDetails_setsNonDelayed() {
        when(mtaClient.fetchMtaInfo()).thenReturn("{\n" +
                "  \"lastUpdated\": \"2020-10-03T14:56:42-0400\",\n" +
                "  \"routeDetails\": [{" +
                "    \n" +
                "      \"route\": \"B\",\n" +
                "      \"color\": \"6CBE45\",\n" +
                "      \"mode\": \"subway\",\n" +
                "      \"agency\": \"MTA NYCT\",\n" +
                "      \"routeId\": \"MTA NYCT_M14D+\",\n" +
                "      \"inService\": true,\n" +
                "      \"routeType\": 3\n" +
                "        }]}");
        subject.updateMtaStatuses();

        assertEquals(StatusEnum.NotDelayed, subject.getStatuses().get("B").getStatusEnum());
    }

    @Test
    void updateMtaStatuses_whenCalled_updatesUpTime() {
        subject.updateMtaStatuses();

        assertEquals(0, subject.getUptime());

        subject.updateMtaStatuses();

        assertEquals(30000, subject.getUptime());
    }

    @Test
    public void updateMtaStatus_whenServiceStaysDelayed_tracksDownTime() {
        when(mtaClient.fetchMtaInfo()).thenReturn("{\n" +
                "  \"lastUpdated\": \"2020-10-03T14:56:42-0400\",\n" +
                "  \"routeDetails\": [{" +
                "      \"route\": \"B\",\n" +
                "      \"color\": \"6CBE45\",\n" +
                "      \"mode\": \"subway\",\n" +
                "      \"agency\": \"MTA NYCT\",\n" +
                "      \"routeId\": \"MTA NYCT_M14D+\",\n" +
                "      \"inService\": true,\n" +
                "      \"routeType\": 3,\n" +
                "      \"statusDetails\": [{\n" +
                "          \"id\": \"MTA NYCT_267718\",\n" +
                "          \"statusSummary\": \"Delays\",\n" +
                "          \"priority\": 11,\n" +
                "          \"direction\": \"1\",\n" +
                "          \"creationDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"startDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"endDate\": null\n" +
                "        }]}]}");

        subject.updateMtaStatuses();
        subject.updateMtaStatuses();

        Mockito.reset(mtaClient);

        when(mtaClient.fetchMtaInfo()).thenReturn("{\n" +
                "  \"lastUpdated\": \"2020-10-03T14:56:42-0400\",\n" +
                "  \"routeDetails\": [{" +
                "      \"route\": \"B\",\n" +
                "      \"color\": \"6CBE45\",\n" +
                "      \"mode\": \"subway\",\n" +
                "      \"agency\": \"MTA NYCT\",\n" +
                "      \"routeId\": \"MTA NYCT_M14D+\",\n" +
                "      \"inService\": true,\n" +
                "      \"routeType\": 3,\n" +
                "      \"statusDetails\": [{\n" +
                "          \"id\": \"MTA NYCT_267718\",\n" +
                "          \"statusSummary\": \"No Delays\",\n" +
                "          \"priority\": 11,\n" +
                "          \"direction\": \"1\",\n" +
                "          \"creationDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"startDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"endDate\": null\n" +
                "        }]}]}");
        subject.updateMtaStatuses();

        StatusTime actual = subject.getStatuses().get("B");

        assertEquals(30000, actual.getDelayedDuration());
    }

    @Test
    void updateMtaStatuses_whenTimeOfDelayIsNotCurrent_ignoresStatus() {
        when(mtaClient.fetchMtaInfo()).thenReturn("{\n" +
                "  \"lastUpdated\": \"2020-10-03T14:56:42-0400\",\n" +
                "  \"routeDetails\": [{" +
                "      \"route\": \"B\",\n" +
                "      \"color\": \"6CBE45\",\n" +
                "      \"mode\": \"subway\",\n" +
                "      \"agency\": \"MTA NYCT\",\n" +
                "      \"routeId\": \"MTA NYCT_M14D+\",\n" +
                "      \"inService\": true,\n" +
                "      \"routeType\": 3,\n" +
                "      \"statusDetails\": [{\n" +
                "          \"id\": \"MTA NYCT_267718\",\n" +
                "          \"statusSummary\": \"Delays\",\n" +
                "          \"priority\": 11,\n" +
                "          \"direction\": \"1\",\n" +
                "          \"creationDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"startDate\": \"2020-10-03T00:00:00-0400\",\n" +
                "          \"endDate\": null\n" +
                "        }]}]}");

        Mockito.reset(timeUtil);
        when(timeUtil.isCurrentEndDate(any())).thenReturn(false);

        subject.updateMtaStatuses();

        assertEquals(StatusEnum.NotDelayed, subject.getStatuses().get("B").getStatusEnum());
    }

    @Test
    void updateMtaStatuses_whenNoResponse_doesNothing() {
        when(mtaClient.fetchMtaInfo()).thenReturn("");
        subject.updateMtaStatuses();

        assertTrue(subject.getStatuses().isEmpty());
    }

    @Test
    void updateMtaStatuses_whenNullResponse_doesNothing() {
        when(mtaClient.fetchMtaInfo()).thenReturn(null);
        subject.updateMtaStatuses();

        assertTrue(subject.getStatuses().isEmpty());
    }
}