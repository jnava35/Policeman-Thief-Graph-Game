# HW3 Police/Thief Game

NetID: jnava35@uic.edu | UIN: 660115946 | Repo for HW3 CS 441

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
(This moves the Police)
- `curl -X POST http://localhost:8080/move/P/2`
(This moves the Thief)
- `curl -X POST http://localhost:8080/move/T/2`
(This here can get info about the player. P or T)
- `curl -X POST http://localhost:8080/getInfo/P/2`

# Information about Sever Class


# Information about gamePoliceThief Class
