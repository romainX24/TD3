public class Test {

  static void header(String title) {
    System.out.println("\n=== " + title + " ===");
  }

  static int countCells(Hex h, Hex.Player p, int n) {
    int c = 0;
    for (int i = 1; i <= n; i++)
      for (int j = 1; j <= n; j++)
        if (h.get(i, j) == p)
          c++;
    return c;
  }

  public static void main(String[] args) {
    int n = 5;

    header("Initial board state");
    Hex h = new Hex(n);
    System.out.println("Current player: " + h.currentPlayer());
    System.out.println("Winner: " + h.winner());
    System.out.println("Borders: BLUE at (0,1)=" + h.get(0, 1) + ", RED at (1,0)=" + h.get(1, 0));

    header("Illegal moves (borders/out of range)");
    System.out.println("click(0,1) -> " + h.click(0, 1));
    System.out.println("click(6,6) -> " + h.click(6, 6));

    header("Basic legal play + alternation");
    System.out.println("click(3,3) -> " + h.click(3, 3) + ", now at (3,3)=" + h.get(3, 3));
    System.out.println("Current player: " + h.currentPlayer());
    System.out.println("click(3,3) again -> " + h.click(3, 3));

    header("Random move");
    System.out.println("randomMove() -> " + h.randomMove());
    System.out.println("Counts RED=" + countCells(h, Hex.Player.RED, n) + ", BLUE=" + countCells(h, Hex.Player.BLUE, n));

    header("Heuristic move on fresh board");
    Hex g = new Hex(n);
    System.out.println("heuristicMove() -> " + g.heuristicMove());
    System.out.println("Counts RED=" + countCells(g, Hex.Player.RED, n) + ", BLUE=" + countCells(g, Hex.Player.BLUE, n));

    header("Random vs Heuristic (auto-play)");
    Hex a = new Hex(7);
    while (a.winner() == Hex.Player.NOONE) {
      if (a.currentPlayer() == Hex.Player.RED) {
        if (!a.randomMove()) break;
      } else {
        if (!a.heuristicMove()) break;
      }
    }
    System.out.println("Winner on 7x7: " + a.winner());

    header("Heuristic vs Heuristic (auto-play)");
    Hex b = new Hex(6);
    while (b.winner() == Hex.Player.NOONE) {
      if (!b.heuristicMove()) break;
    }
    System.out.println("Winner on 6x6: " + b.winner());

    System.out.println("\nAll tests done.");
  }
}

