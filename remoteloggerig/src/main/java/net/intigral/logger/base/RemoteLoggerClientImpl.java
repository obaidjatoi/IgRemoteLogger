package net.intigral.logger.base;

import net.intigral.core.http.agent.TransportAgentBase;
import net.intigral.core.http.agent.TransportAgentFactory;
import net.intigral.core.http.request.APIRequestID;
import net.intigral.core.http.request.NetworkError;
import net.intigral.core.http.request.NetworkRequest;
import net.intigral.core.http.request.NetworkResponse;
import net.intigral.core.http.request.ResponseObserver;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Simon Gerges on 9/22/16.
 * <p>
 */

public class RemoteLoggerClientImpl implements RemoteLoggerClient {

    private HashMap<String, String> staticFlushHeaders;
    private String serverURL;

    ///////////////////////////////////////////////////////////////////////////
    // Singleton
    ///////////////////////////////////////////////////////////////////////////
    private static RemoteLoggerClientImpl instance;

    public static RemoteLoggerClientImpl getInstance() {

        if (instance == null)
            instance = new RemoteLoggerClientImpl();
        return instance;
    }

    private RemoteLoggerClientImpl() {
        super();
        staticFlushHeaders = new HashMap<>();
    }

    @Override
    public void flushLogs(List<String> logRecords, final RemoteLoggerClient.LogsFlushListener flushListener) {

        NetworkRequest networkRequest = new NetworkRequest(RemoteLoggerRequests.FLUSH_LOGS);
        networkRequest.setHttpMethod(NetworkRequest.HttpMethod.POST);
        networkRequest.setRequestUrl(serverURL);
        networkRequest.setHttpHeaders(staticFlushHeaders);
        networkRequest.setPayLoad(prepareBody(logRecords).getBytes());

        TransportAgentBase transportAgent = TransportAgentFactory.getTransportAgent();
        transportAgent.send(networkRequest, new ResponseObserver() {
            @Override
            public void onResponse(APIRequestID reqId, NetworkResponse responseMsg) {

                flushListener.onFlushFinished(true);
            }

            @Override
            public void onFail(APIRequestID reqId, NetworkError networkError) {

                flushListener.onFlushFinished(false);
            }
        });
    }

    @Override
    public void addHeader(String headerKey, String headerValue) {

        staticFlushHeaders.put(headerKey, headerValue);
    }

    @Override
    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private Members
    ///////////////////////////////////////////////////////////////////////////

    private String prepareBody(List<String> logRecords) {
        StringBuilder sb = new StringBuilder();
        for (String singleRecord : logRecords) {
            sb.append(singleRecord);
            sb.append("$");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    private enum RemoteLoggerRequests implements APIRequestID {
        FLUSH_LOGS;
    }
}
