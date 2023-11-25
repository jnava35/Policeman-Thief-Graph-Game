import akka.actor.{Actor, ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

import scala.io.StdIn

// Define case classes for HTTP requests below are the classes.

// This is a message request so we can restarts thhe game
case class RestartGame()
// Making a requiest to move the player
case class MoveRequest(playerName: String, nodeId: Int)

// Message to request information about a node in the perturbed graph
case class QueryRequest(playerName: String, node: Node)

// This will represnet a node in the graph and from here we can see the
// valuable data the node contains
case class Node(id: Int, valuableData: Boolean)

// This is to see if ther is an edge between the nodes which will be used
// to see if a valid move can be made
case class Edge(from: Node, to: Node)

// Represens the graph and the nodes contained within the graph as well as the edges being connected
case class GameGraph(nodes: Set[Node], edges: Set[Edge])
// Represents a player in the policeman/ thief game with a name and the role policeman or thief
// as well as the current position represented by a node in the game graph
case class Player(name: String, role: String, position: Node)


// Actor for handling game logic
class PolicemanThiefGameActor(originalGraph: GameGraph, perturbedGraph: GameGraph) extends Actor {
  private var game: PolicemanThiefGame = _

  // Initialize the logger
  private val logger = Logging(context.system, this)

  override def receive: Receive = {
    // Here we are starting the game of police and thief
    case RestartGame =>
      game = new PolicemanThiefGame(originalGraph, perturbedGraph)
      sender() ! "Game restarted."
    // Here we are getting the request for the user to make a move
    case moveRequest: MoveRequest =>
      val response = game.movePlayer(movePlayer.playerName, Node(movePlayer.nodeId, valuableData = false))
      sender() ! response
    // Here we can get
    case getInfo: QueryRequest =>
      val response = game.getInfoNode(getInfoNode.playerName, getInfoNode.node)
      sender() ! response
  }
}

// Object for running the HTTP server
object PolicemanThiefGameServer extends App {
  implicit val system: ActorSystem = ActorSystem("PolicemanThiefGame")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(5.seconds)

  // Load original and perturbed graphs from NetGraph files
  val originalGraph: Option[NetGraph] = NetGraph.load("NetGraph_21-11-23-23-05-10.ngs", "C:\\Users\\fatjj\\Documents\\(6) UIC Fall 2023\\CS 441 Fall 2023\\NetGameSim\\")
  val perturbedGraph: Option[NetGraph] = NetGraph.load("NetGraph_21-11-23-23-05-10.ngs.perturbed", "C:\\Users\\fatjj\\Documents\\(6) UIC Fall 2023\\CS 441 Fall 2023\\NetGameSim\\")

  // Here we have to Check if both graphs are successfully loaded
  (originalGraph, perturbedGraph) match {
    case (Some(og), Some(pg)) =>
      // Create a PolicemanThiefGameActor instance with the loaded graphs
      val gameActor = system.actorOf(Props(new PolicemanThiefGameActor(og.toGameGraph, pg.toGameGraph)))

      val route = {
        // curl -X POST http://localhost:8080/restart

          path("restart") {
          post {
            completeWithStatus((gameActor ? RestartGame).mapTo[String])
          }
        } ~
          // curl -X POST http://localhost:8080/startGame
          path("startGame") {
            post {
              complete("Game started.")
            }
          } ~
      }
      // Example:
      // curl -X POST http://localhost:8080/move/P/2
      // where P is police and 2 is node

      // curl -X POST http://localhost:8080/move/T/2
      // where T is thief and 2 is node

      path("move" / Segment / IntNumber) { (playerName, nodeId) =>
        post {
          val movePlayer = movePlayer(playerName, nodeId)
          completeWithStatus((gameActor ? moveRequest).mapTo[String])
        }
      } ~
        // curl -X POST http://localhost:8080/getInfo/P/2
        path("getInfo") {
          post {
            entity(as[QueryRequest]) { queryRequest =>
              val playerName = getInfoNode.playerName
              val node = getInfoNode.node
              // Call the getInfoNode method here
              game.getInfoNode(playerName, node)
              complete("getting info.")
            }
          }
        }

      // Start the HTTP server
      Http().newServerAt("localhost", 8080).bind(route)
      // Print a message indicating the server is online
      logger.info("Server online at http://localhost:8080/\nPress Enter to stop...")
      // Print a message indicating the server is online
      println(s"Server online at http://localhost:8080/\nPress Enter to stop...")

      StdIn.readLine()
      system.terminate()

    case _ =>
      // Print an error message if there is an issue loading NetGraph files
      println("Error loading NetGraph files.")
  }
}
