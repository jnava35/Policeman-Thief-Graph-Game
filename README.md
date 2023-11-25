# HW3 Police/Thief Game

NetID: jnava35@uic.edu | UIN: 660115946 | Repo for HW3 CS 441

More Information and Details on Project:
[HW3 Assignment](https://github.com/0x1DOCD00D/CS441_Fall2023/blob/main/Homework3.md)

# Setting up Project
1. Download repo from git
2. Open project in intelliJ 
3. Then we have to import the netmodelsim.jar
5. To do the above open intelij and go to `file -> Project Structure -> Modules -> Dependencies -> Module SDK -> + -> JAR or Directories`
6. Then select nemodelsim.jar
7. Next click `Apply -> OK`  
8. Next once every thing is setup run `sbt clean compile`

# Running the Project
1. Confirm the steps from setting up project is completed
2. Run the `Main.scala` by clicking the green arrow next to `def main`
3. Once ran the code will produce files we will need to run our program
4. Locate where the files are produced and stored
5. We need a `NetGraph_21-11-23-23-05-10.ngs` and a `NetGraph_21-11-23-23-05-10.ngs.perturbed`
6. Once we have obtained the files we need to add them to our classes and change the directory output
7. Go to server class and locate where we are loading in the graphs for `originalGraph` and `perturbedGraph`
8. Change the file to the file produced in main and change the path of where it was produced. Make sure file for `originalGraph` is .ngs and `perturbedGraph` is .ngs.perturbed
9. Go to gamePoliceThif class and repeat the same process as steps(8)
10. Once the appropriate files are setup we can run the program

# Playing Police/Thief Game
Background on what the game actually is about:
The Policeman/Thief graph game where playersm acting as policemen or thieves can go around and navigate through a graph of nodes and edges. Thieves aim to collect valuable data in specific nodes, while policemen attempt to catch the thieves by moving around the graph.

TO start playing run the `server.class` this should then invoke the `gamePoliceThief.class`
Once we have this the program will then assign a P/T characters on a random node on the graph where there next you can start playing and moving by doing a series a command

(This restarts the game)
- `curl -X POST http://localhost:8080/restart`
 
(This starts the game)
- `curl -X POST http://localhost:8080/startGame`

(This moves the Police, 2 is a place holder)
- `curl -X POST http://localhost:8080/move/P/2`

(This moves the Thief, 2 is a place holder)
- `curl -X POST http://localhost:8080/move/T/2`

(This here can get info about the player. P or T, 2 is a place holder)
- `curl -X POST http://localhost:8080/getInfo/P/2`

# Information about Sever Class

(Key Variables and Definitions)
1. originalGraph: Option[NetGraph] load the .ngs
2. perturbedGraph: Option[NetGraph] load the .ngs.perturbed
3. gameActor: ActorRef - Actor responsible for handling game logic.
4. route: Route - Akka HTTP route defining the REST API for the PolicemanThiefGameServer.
5. og: NetGraph - For original graph
6. pg: NetGraph - For perturbed graph

(Classes)
1. RestartGame - Message request to restart the game.
2. MoveRequest(playerName: String, nodeId: Int) - Request to move the player to a specified node.
3. QueryRequest(playerName: String, node: Node) - Request for information about a node in the perturbed graph.
4. Node(id: Int, valuableData: Boolean) - Represents a node in the game graph with an ID and valuable data indicator.
5. Edge(from: Node, to: Node) - Represents an edge between two nodes in the game graph.
6. GameGraph(nodes: Set[Node], edges: Set[Edge]) - Represents the game graph with nodes and edges.
7. Player(name: String, role: String, position: Node) - Represents a player in the game with a name, role, and position.

# Information about gamePoliceThief Class

(Key Variables and Definitions)
1. originalGraph: Option[NetGraph] load the .ngs
2. perturbedGraph: Option[NetGraph] load the .ngs.perturbed
3. players: Map[String, Player] - Map storing player information with player name as the key and player object as the value.
5. confidenceScore: Double - Confidence score associated with a node in the perturbed graph for player to make descision.

(Case classes)
1. Node(id: Int, valuableData: Boolean, confidenceScore: Double) - Represents a node in the game graph with an ID, valuable data indicator, and confidence score.
2. Edge(from: Node, to: Node) - Represents an edge between two nodes in the game graph.
3. GameGraph(nodes: Set[Node], edges: Set[Edge]) - Represents the game graph with nodes and edges.
4. Player(name: String, role: String, position: Node) - Represents a player in the game with a name, role, and position.

(Methods and Functions)
1. getRandomNode(graph: GameGraph): Node - Returns a random node from the graph.
2. registerPlayer(name: String, role: String): Unit - Registers a player with a random starting position.
3. getRandomConnectedNode(from: Node): Option[(Node, Edge)] - Returns a random connected node and the connecting edge for player movement.
4. isValidMove(from: Node, to: Node, graph: GameGraph): Boolean - Checks if a move from one node to another is valid.
5. movePlayer(playerName: String, nodeId: Int): Unit - Moves a player to a specified node if the move is valid.
6. listNeighborNodes(playerName: String): Unit - Lists neighbor nodes for a player.
7. getInfoNode(playerName: String, node: Node): Unit - Retrieves information about a specific node in the perturbed graph.
8. getPlayerInfo(playerName: String): Option[Player] - Retrieves information about a specific player.
9. printWinLossStatus(playerName: String): Unit - Prints win/loss status for a player based on their current position and conditions.

(Main Object: gamePoliceThief)1
1. Main entry point for the PolicemanThiefGame application.
2. Loads original and perturbed graphs from NetGraph files.
3. Initializes and sets up the PolicemanThiefGame with the loaded graphs.
4. Registers a policeman and a thief player.

