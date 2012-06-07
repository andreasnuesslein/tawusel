package models

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.TestServer
import play.api.test.FakeApplication
import net.sf.ehcache.search.expression.EqualTo


class LocationModelTest extends Specification {
  
  "Location model" should {
    running(TestServer(9000)) {
       val testLocation = Location.create("myLocation",1,"ABStr. 123") 
        "create a location" in{
          running(FakeApplication()) {
            
        	//check if it was created correctly
        	
        	//try to create a new location
        	//check if it was created correctly
        	testLocation.town_id must equalTo(1)
        	testLocation.name must equalTo("myLocation")
        	testLocation.address must equalTo("ABStr. 123")
          }
        }
        
    
        "retrieve all locations" in {
          running(FakeApplication()) {
        	Location.findAll.size must >(0)
          }
        }
    
        "retrieve a location by id" in {
           val testLocation = Location(12, 3, "Audi TE", "Tor 9, 85055 Ingolstadt" )
          running(FakeApplication()) {
        	val cmpLocation = Location.findById(testLocation.id) 
        	testLocation must equalTo(cmpLocation)
          }
        }
    
        "delete an existing location" in{
          running(FakeApplication()) {
        	Location.delete(testLocation.id) must equalTo(true)
          }
        }
        
        "delete a non-existing location" in {
          running(FakeApplication()) {
        	Location.delete(200) must equalTo(false)
          }
        }
        
        "retrieve a location name by id" in {
          running(FakeApplication()) {
        	Location.getName(1) must equalTo("Carmeq Berlin")
        	Location.getName(2) must equalTo("Carmeq Wolfsburg, Autovision")
        	Location.getName(10) must equalTo("Volkswagen Prüfgelände Ehra")
          }
        }

    }
  }
  
}
