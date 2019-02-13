/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.cloud.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.dgrf.cloud.dto.UserAuthDTO;
import org.dgrf.cloud.response.DGRFResponseCode;
import org.dgrf.cloud.response.DGRFResponseMessage;

/**
 *
 * @author dgrf-iv
 */
public class UserAuthClient {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = DGRFCloudConstants.BASE_URL;

    public UserAuthClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("userauth");

    }

    public UserAuthDTO authenticateUser(UserAuthDTO userAuthDTO) {
        WebTarget resource = webTarget;
        ObjectMapper objectMapper = new ObjectMapper();
        String userAuthDTOJSON;
        try {
            userAuthDTOJSON = objectMapper.writeValueAsString(userAuthDTO);
            
        } catch (JsonProcessingException ex) {
            Logger.getLogger(UserAuthClient.class.getName()).log(Level.SEVERE, null, ex);
            userAuthDTO.setResponseCode(DGRFResponseCode.JSON_FORMAT_PROBLEM);
            return userAuthDTO;
        }
        Invocation.Builder ib = resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON);
        UserAuthDTO respUserAuthDTO;
        try {
            Response response = ib.post(javax.ws.rs.client.Entity.entity(userAuthDTOJSON, javax.ws.rs.core.MediaType.APPLICATION_JSON));
            if (response.getStatus() != 200) {
                respUserAuthDTO = userAuthDTO;
                respUserAuthDTO.setResponseCode(DGRFResponseCode.SERVICE_CONNECTION_FAILURE);
                
                return respUserAuthDTO;
            }
            String respUserAuthDTOJSON = response.readEntity(String.class);
            
            try {
                respUserAuthDTO = objectMapper.readValue(respUserAuthDTOJSON, UserAuthDTO.class);
            } catch (IOException ex) {
                respUserAuthDTO = userAuthDTO;
                Logger.getLogger(UserAuthClient.class.getName()).log(Level.SEVERE, null, ex);
                respUserAuthDTO.setResponseCode(DGRFResponseCode.JSON_FORMAT_PROBLEM);
                return respUserAuthDTO;
            }
            return respUserAuthDTO;
        } catch (ProcessingException pe) {
            respUserAuthDTO = userAuthDTO;
            respUserAuthDTO.setResponseCode(DGRFResponseCode.SERVICE_CONNECTION_FAILURE);
            return respUserAuthDTO;
        }
    }

    public void close() {
        client.close();
    }
}
