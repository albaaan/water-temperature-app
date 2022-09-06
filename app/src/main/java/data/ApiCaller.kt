package data

import models.Base

//Klasse som kaller paa API-et fra interfacet ApiService:
class ApiCaller : ApiService{
    suspend fun call() : Base?{
        return testKall()
    }
}