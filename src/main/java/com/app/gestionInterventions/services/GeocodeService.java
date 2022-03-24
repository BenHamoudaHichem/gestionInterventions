package com.app.gestionInterventions.services;

import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.additional.Location;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class GeocodeService {
    private static final String URL_Web_Service="https://geloky.com/api/geo/geocode?";

    private static final String API_Key="y0pZ5MO8SJJ5s54Q2X7rf1CtT5GnrUTY";
    private static final String Format ="geloky";
    private RestTemplate restTemplate = new RestTemplate();

    public boolean validateAddress(Address address)
    {
        return true;
    }
    public Location fromCity(Address address) {
        return Arrays.asList(this.restTemplate.getForEntity(this.generateUrl(address.getAddressZone()),Location[].class).getBody()).get(0);
    }
    public Object romCity(Address address) {
        return this.restTemplate.getForEntity(this.generateUrl(address.getAddressZone()),Object.class).getBody();
    }
    private String generateUrl(String input)
    {
        return URL_Web_Service+"address="+input+"&key="+API_Key+"&format="+Format;
    }


}
