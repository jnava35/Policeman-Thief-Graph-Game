object Testing {

  def main(args: Array[String]): Unit = {
    // Load original and perturbed graphs from NetGraph files
    val originalGraph: Option[NetGraph] = NetGraph.load("NetGraph_21-11-23-23-05-10.ngs", "C:\\Users\\fatjj\\Documents\\(6) UIC Fall 2023\\CS 441 Fall 2023\\NetGameSim\\")
    val perturbedGraph: Option[NetGraph] = NetGraph.load("NetGraph_21-11-23-23-05-10.ngs.perturbed", "C:\\Users\\fatjj\\Documents\\(6) UIC Fall 2023\\CS 441 Fall 2023\\NetGameSim\\")

    (originalGraph, perturbedGraph) match {
      case (Some(og), Some(pg)) =>
        // Create a PolicemanThiefGame instance with the loaded graphs
        val gameGraph = new PolicemanThiefGame(og.toGameGraph, pg.toGameGraph)

        // Test 1: Register a Policeman and a Thief player
        gameGraph.registerPlayer("P1", "Policeman")
        gameGraph.registerPlayer("T1", "Thief")

        // Test 2: Move the Policeman player and query the perturbed graph
        gameGraph.movePlayer("P1")
        val result2 = gameGraph.queryPerturbedGraph("P1", Node(1, true))
        val expectedValue2 = true
        assert(result2 == expectedValue2)

        // Test 3: Move the Thief player towards valuable data and win
        gameGraph.movePlayer("T1")
        val result3 = gameGraph.queryPerturbedGraph("T1", Node(2, true))
        val expectedValue3 = true
        assert(result3 == expectedValue3)

        // Test 4: Move the Policeman player towards valuable data and lose
        gameGraph.movePlayer("P1")
        val result4 = gameGraph.queryPerturbedGraph("P1", Node(3, true))
        val expectedValue4 = true
        assert(result4 == expectedValue4)

        // Test 5: Move the Policeman player to a new node and query the perturbed graph
        gameGraph.movePlayer("P1")
        val result5 = gameGraph.queryPerturbedGraph("P1", Node(4, false))
        val expectedValue5 = false
        assert(result5 == expectedValue5)

        // Test 6: Move the Thief player to a new node and query the perturbed graph
        gameGraph.movePlayer("T1")
        val result6 = gameGraph.queryPerturbedGraph("T1", Node(5, false))
        val expectedValue6 = false
        assert(result6 == expectedValue6)

        // Test 7: Move both players to different nodes and query the perturbed graph
        gameGraph.movePlayer("P1")
        gameGraph.movePlayer("T1")
        val result7P = gameGraph.queryPerturbedGraph("P1", Node(5, false))
        val expectedValue7P = false
        assert(result7P == expectedValue7P)

        val result7T = gameGraph.queryPerturbedGraph("T1", Node(6, false))
        val expectedValue7T = false
        assert(result7T == expectedValue7T)

        // Test 8: Move the Policeman player towards valuable data and win
        gameGraph.movePlayer("P1")
        val result8 = gameGraph.queryPerturbedGraph("P1", Node(7, true))
        val expectedValue8 = true
        assert(result8 == expectedValue8)

        // Test 9: Move the Thief player towards valuable data and lose
        gameGraph.movePlayer("T1")
        val result9 = gameGraph.queryPerturbedGraph("T1", Node(8, true))
        val expectedValue9 = true
        assert(result9 == expectedValue9)

        // Test 10: Move both players to different nodes and query the perturbed graph
        gameGraph.movePlayer("P1")
        gameGraph.movePlayer("T1")
        val result10P = gameGraph.queryPerturbedGraph("P1", Node(9, false))
        val expectedValue10P = false
        assert(result10P == expectedValue10P)

        val result10T = gameGraph.queryPerturbedGraph("T1", Node(10, false))
        val expectedValue10T = false
        assert(result10T == expectedValue10T)

      case _ =>
        // Print an error message if there is an issue loading NetGraph files
        println("Error loading NetGraph files.")
    }
  }
}
