package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import java.sql.Date
import com.codahale.jerkson.Json

object JSON extends Controller {

  def authenticateByApp(email:String, hashedPassword:String) = Action {
    val user = User.authenticateWithHashedPassword(email, hashedPassword)
    val json = Json.generate(user)
    Ok(json).as("application/json")
  }
  
  def getActiveToursByApp(email: String) = Action {
    val user = User.findByEmail(email).get
    val activeTours = Tour.findAllForUser(user.id)
    val json = Json.generate(activeTours)
    Ok(json).as("application/json")
  }
  
  def getTourTemplatesByApp(email:String) = Action {
    val user = User.findByEmail(email).get
    val tourTemplates = Tour.findTemplatesForUser(user.id)
    val json = Json.generate(tourTemplates)
    Ok(json).as("application/json")
  }
  
  def getAvailableToursByApp(email:String) = Action {
    val user = User.findByEmail(email).get
    val availableTours = Tour.findAll(user.id)
    val json = Json.generate(availableTours)
    Ok(json).as("application/json")
  }
    
}