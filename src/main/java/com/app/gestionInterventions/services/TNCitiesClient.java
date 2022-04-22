package com.app.gestionInterventions.services;

import com.app.tnlocations.wsdl.GetAllStatesRequest;
import com.app.tnlocations.wsdl.GetStateRequest;
import com.app.tnlocations.wsdl.Tnresponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
@Service
public class TNCitiesClient extends WebServiceGatewaySupport {



    public Tnresponse getStates()
    {
        Tnresponse tnresponse = (Tnresponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://localhost:8081/ws/cities",new GetAllStatesRequest(),
                        new SoapActionCallback("http://spring.io/guides/gs-producing-web-service/getAllStatesRequest"));

        System.out.println(tnresponse);
        return tnresponse;
    }
    public Tnresponse getCitiesByState(String state)
    {
        GetStateRequest getStateRequest= new GetStateRequest();
        getStateRequest.setState(state);
        Tnresponse tnresponse = (Tnresponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://localhost:8081/ws/cities",getStateRequest,
                        new SoapActionCallback(
                                "http://spring.io/guides/gs-producing-web-service/getState"));

        System.out.println(tnresponse);
        return tnresponse;
    }


}
