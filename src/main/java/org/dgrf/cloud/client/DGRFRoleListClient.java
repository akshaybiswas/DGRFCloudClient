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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.dgrf.cloud.dto.RoleDTO;
import org.dgrf.cloud.response.DGRFResponseCode;

/**
 *
 * @author dgrfv
 */
public class DGRFRoleListClient {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = DGRFCloudConstants.BASE_URL;

    public RoleDTO getRoleList(RoleDTO roleDTO) {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        ObjectMapper objectMapper = new ObjectMapper();
        String roleDTOJSON = null;
        String respRoleDTOJSON;
        webTarget = client.target(BASE_URI).path("rolelist");
        WebTarget resource = webTarget;
        try {
            roleDTOJSON = objectMapper.writeValueAsString(roleDTO);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(DataConnClient.class.getName()).log(Level.SEVERE, null, ex);
            roleDTO.setResponseCode(DGRFResponseCode.JSON_FORMAT_PROBLEM);
            return roleDTO;
        }
        Invocation.Builder ib = resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON);
        Response response = ib.post(javax.ws.rs.client.Entity.entity(roleDTOJSON, javax.ws.rs.core.MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            Logger.getLogger(DGRFRoleListClient.class.getName()).log(Level.SEVERE, "Service connection response"+Integer.toString(response.getStatus()));
            roleDTO.setResponseCode(DGRFResponseCode.SERVICE_CONNECTION_FAILURE);
            return roleDTO;
        }
        respRoleDTOJSON = response.readEntity(String.class);

        try {
            roleDTO = objectMapper.readValue(respRoleDTOJSON, RoleDTO.class);
        } catch (IOException ex) {
            Logger.getLogger(DataConnClient.class.getName()).log(Level.SEVERE, null, ex);
            roleDTO.setResponseCode(DGRFResponseCode.JSON_FORMAT_PROBLEM);
            return roleDTO;
        }
        return roleDTO;

    }
}
