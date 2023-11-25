import scala.util.Random
import org.slf4j.Logger
import NetGraphAlgebraDefs.NetGraph.logger


// This will represnet a node in the graph and from here we can see the
// valuable data the node contains as well as the confidenceScore to see
// if a player can determine the move
case class Node(id: Int, valuableData: Boolean, confidenceScore: Double)
// This is to see if ther is an edge between the nodes which will be used
// to see if a valid move can be made
case class Edge(from: Node, to: Node)
// Represens the graph and the nodes contained within the graph as well as the edges being connected
case class GameGraph(nodes: Set[Node], edges: Set[Edge])
// Represents a player in the policeman/ thief game with a name and the role policeman or thief
// as well as the current position represented by a node in the game graph
case class Player(name: String, role: String, position: Node)
//
// This is the main method for gamePoliceThief
object gamePoliceThief {
  def main(args: Array[String]): Unit = {


    // Load the original and perturbed graphs.
    val originalGraph: Option[NetGraph] = NetGraph.load("NetGraph_21-11-23-23-05-10.ngs", "C:\\Users\\fatjj\\Documents\\(6) UIC Fall 2023\\CS 441 Fall 2023\\NetGameSim\\")
    val perturbedGraph: Option[NetGraph] = NetGraph.load("NetGraph_21-11-23-23-05-10.ngs.perturbed", "C:\\Users\\fatjj\\Documents\\(6) UIC Fall 2023\\CS 441 Fall 2023\\NetGameSim\\")

    // Here we are CHecking if both of the graphs are successfully loaded.
    (originalGraph, perturbedGraph) match {
      case (Some(og), Some(pg)) =>
        // Setting up the game with the original and perturbed graphs.
        val gameGraph = new PolicemanThiefGame(og.toGameGraph, pg.toGameGraph)

        // Register a policeman and a thief player
        // which is then assigned to the player
        gameGraph.registerPlayer("P1", "Policeman")
        gameGraph.registerPlayer("T1", "Thief")

      case _ =>
        // Print an error message if there is an issue loading the NetGraph files.
        println("Error loading the original and perturbed files.")
    }
  }
}

class PolicemanThiefGame(originalGraph: GameGraph, perturbedGraph: GameGraph) {

  // Map which sotrs player information
  // using player name as key and player object as value such as their role
  var players: Map[String, Player] = Map()

  // So here we arre going to get randomly get a node from the graph so we can assign
  // to place the player so in other words the starting position
  private def getRandomNode(graph: GameGraph): Node = {
    Random.shuffle(graph.nodes).head
  }

  // Once we have a random node then we can place the player/ register the player
  def registerPlayer(name: String, role: String): Unit = {
    // Get a random node from the getRandomNode
    val randomNode = getRandomNode(originalGraph)
    // THen we create a new player and add to the map
    players += (name -> Player(name, role, randomNode))
    // THen we print out the info on where the user is
    println(s"Player $name (${role.charAt(0)}) has joined the game and is at node ${randomNode.id}.")
  }

  // Here we are to get a random connected node such as a neighbor node and the edge connecting them for player movement
  private def getRandomConnectedNode(from: Node): Option[(Node, Edge)] = {
    // Find successor nodes connected to the current node so the player can move too
    val successors: Array[Node] = originalGraph.edges.collect {
      case Edge(`from`, to) => to
    }.toArray
    // If there is not successsor then no move can be made
    if (successors.isEmpty) None
    else {
      // Select a random successor node and then gets the edge
      val randomSuccessor: Node = successors(Random.nextInt(successors.length))
      val edge: Edge = Edge(from, randomSuccessor)
      Some((randomSuccessor, edge))
    }
  }

  // This is method to move a player in the game
  def movePlayer(playerName: String, nodeId: Int): Unit = {
    // Get the player from the map
    // Then we get the curent node of the player
    players.get(playerName).foreach { player =>
      val currentNode = player.position

      // Check if the nodeId is valid in the graph
      originalGraph.nodes.find(_.id == nodeId) match {
        case Some(newNode) =>
          // Update the player's position
          players += (playerName -> player.copy(position = newNode))
          // Log and print the player's move
          logger.info(s"$playerName (${player.role}) moved to Node ${newNode.id}.")
          println(s"$playerName (${player.role}) moved to Node ${newNode.id}.")

          // Then we can list the neighbor nodes
          listNeighborNodes(playerName)
          // Check win/loss status
          printWinLossStatus(playerName)

        case None =>
          // Print a warning if the specified nodeId is not valid
          logger.warn(s"Invalid nodeId: $nodeId. No valid move for $playerName (${player.role}). Player is stuck.")
          println(s"Invalid nodeId: $nodeId. No valid move for $playerName (${player.role}). Player is stuck.")
      }
    }
  }



  // Use this to list neighbor nodes for a player
  def listNeighborNodes(playerName: String): Unit = {
    // Retrieve the player from the map
    players.get(playerName).foreach { player =>
      // Find neighbor nodes based on the player's position
      val neighbors = originalGraph.edges.filter(_.from == player.position).map(_.to)

      // Log and print the neighbor nodes
      logger.info(s"Neighbor nodes for $playerName (${player.role}): ${neighbors.mkString(", ")}")
      println(s"Neighbor nodes for $playerName (${player.role}): ${neighbors.mkString(", ")}")
    }
  }


  // Next here we are going to call this function to make sure the player is making
  // a valid move by checking this the node they are on (from) to the next node (to)
  // and see if it's true or false
  private def isValidMove(from: Node, to: Node, graph: GameGraph): Boolean = {
    // Check if the edge connecting the current node to the target node exists in the game graph.
    val isValid = graph.edges.contains(Edge(from, to))
    if (isValid) {
      // Valid move
      println(s"Move from Node ${from.id} to Node ${to.id} is valid.")
      logger.info(s"Move from Node ${from.id} to Node ${to.id} by ${playerName} is valid.")
    } else {
      // Invalid move
      println(s"Move from Node ${from.id} to Node ${to.id} is invalid. No valid edge.")
      logger.warn(s"Move from Node ${from.id} to Node ${to.id} by ${playerName} is invalid. No valid edge.")
    }
    // Return result
    isValid
  }

  // Method to get information about a specific node in the perturbed graph
  def getInfoNode(playerName: String, node: Node): Unit = {
    // Retrieve the player from the map
    players.get(playerName).foreach { player =>
      // Here we can get the the confidence score of the node in the perturbed graph
      //
      val confidenceScore = perturbedGraph.nodes.find(_.id == node.id) match {
        case Some(foundNode) => foundNode.confidenceScore
        case None =>
          // Log and print a warning if the no
          logger.warn(s"Node ${node.id} not found in the perturbed graph.")
          println(s"Node ${node.id} not found in the perturbed graph.")
      }

      // Log and print the result of the query
      logger.info(s"$playerName (${player.role}) retrieved information for Node ${node.id} in the perturbed graph. Confidence score: $confidenceScore")
      println(s"$playerName (${player.role}) retrieved information for Node ${node.id} in the perturbed graph. Confidence score: $confidenceScore")
    }
  }

  // Retrieve information about a specific player by their name
  // this is useful for getting info about the user player
  // information sueh as role policeman or thief and current position in the game graph.
  def getPlayerInfo(playerName: String): Option[Player] = players.get(playerName)


  // Method to print win/loss status for a player
  def printWinLossStatus(playerName: String): Unit = {
    // Retrieve the player from the map
    players.get(playerName).foreach { player =>
      // Here we check if the the current node the player is in contains valuable data
      if (player.position.valuableData) {
        // DIsplaying we have won
        logger.info(s"$playerName (${player.role}) has won the game!")
        println(s"$playerName (${player.role}) has won the game!")
      }
      else if (originalGraph.nodes.contains(player.position)) {
        // Check if both the pocliveman and the thief are in the same node
        if (players.exists { case (_, otherPlayer) => otherPlayer.position == player.position && otherPlayer.role != player.role }) {
          // If the policeman catches thief
          logger.info(s"The Policeman has caught the Thief. $playerName (${player.role}) has won the game!")
          println(s"The Policeman has caught the Thief. $playerName (${player.role}) has won the game!")
        } else {
          // Losing condition
          logger.info(s"$playerName (${player.role}) has lost the game!")
          println(s"$playerName (${player.role}) has lost the game!")
        }
      }
    }
  }

}
