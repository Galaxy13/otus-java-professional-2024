package com.galaxy13.crm.repository;

import com.galaxy13.crm.model.Address;
import com.galaxy13.crm.model.Client;
import com.galaxy13.crm.model.Phone;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClientResultSetExtractor implements ResultSetExtractor<List<Client>> {
    @Override
    public List<Client> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Client> clientList = new ArrayList<>();
        Client currentClient = null;
        while (rs.next()) {
            long clientId = rs.getLong("client_id");
            String clientName = rs.getString("client_name");
            long addressId = rs.getLong("address_id");
            String street = rs.getString("street");
            long phoneId = rs.getLong("phone_id");
            String number = rs.getString("phone_number");

            if (currentClient == null || currentClient.getId() != clientId) {
                if (currentClient != null) {
                    clientList.add(currentClient);
                }
                Address address = new Address(addressId, street);
                Phone phone = new Phone(phoneId, number);
                currentClient = new Client(clientId, clientName, address, Set.of(phone));
            } else {
                Phone phone = new Phone(phoneId, number);
                currentClient.getPhones().add(phone);
            }
        }
        if (currentClient != null) {
            clientList.add(currentClient);
        }
        return clientList;
    }
}
