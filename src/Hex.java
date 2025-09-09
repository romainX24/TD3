/* Jeu de Hex
   https://fr.wikipedia.org/wiki/Hex

   grille n*n

   cases jouables : (i,j) avec 1 <= i, j <= n

   bords bleus (gauche et droite) : i=0 ou i=n+1, 1 <= j <= n
   bords rouges (haut et bas) : 1 <= i <= n, j=0 ou j=n+1

   note : les quatre coins n'ont pas de couleur

   adjacence :      i,j-1   i+1,j-1

                 i-1,j    i,j   i+1,j

                    i-1,j+1    i,j+1

*/

public class Hex implements Cloneable {
  private Player[][] board;
  private Player currPlayer = Player.RED;
  private int n;
  private Player winner;

  enum Player {
    NOONE, BLUE, RED
  }

  Player otherPlayer(Player p){
    if(p.equals(Player.BLUE)){
      return Player.RED;
    }
    else{
      return Player.BLUE;
    }
  }


  // crée un plateau vide de taille n*n
  Hex(int n) {
    this.board = new Player[n+2][n+2];
    for(int i = 0; i<=n+1;i++){
      for(int j = 0; j<=n+1;j++){
        if(i==0||i==n+1){
          this.board[i][j]=Player.BLUE;
        }
        else if(j==0||j==n+1){
          this.board[i][j]=Player.RED;
        }
        else{
          this.board[i][j]=Player.NOONE;
        }    

      }
    }
    this.board[0][0]=Player.NOONE;
    this.board[n+1][n+1]=Player.NOONE;
    this.board[0][n+1]=Player.NOONE;
    this.board[n+1][0]=Player.NOONE;
    this.n=n;
    this.winner=Player.NOONE;

  }

  // renvoie la couleur de la case i,j
  Player get(int i, int j) {
    return this.board[i][j];
  }


  // Met à jour le plateau après que le joueur avec le trait joue la case (i, j).
  // Ne fait rien si le coup est illégal.
  // Renvoie true si et seulement si le coup est légal.
  boolean click(int i, int j) {
    if(1<=i && i<=n && 1<=j && j<=n){
      if(this.get(i, j).equals(Player.NOONE)){
        this.board[i][j]=this.currentPlayer();
        this.currPlayer = this.otherPlayer(this.currentPlayer());
        return true;
      }

    }
    return false;
  }

  // Renvoie le joueur avec le trait ou Player.NOONE si le jeu est terminé par
  // la victoire d'un joueur.
  Player currentPlayer() {
    if(this.winner.equals(Player.NOONE)){
      return currPlayer;
    }
    return this.winner;
  }


  // Renvoie le joueur gagnant, ou Player.NOONE si aucun joueur n'est encore
  // gagnant
  Player winner() {
    return Player.NOONE;
  }

  int label(int i, int j) {
    return 0;
  }

  // Joue un coup aléatoire pour le joueur ayant le trait, met à jour l'état
  // du jeu comme un clic sur une case, et renvoie true si un coup a été joué
  // (false si la partie est terminée ou s'il n'existe plus de coup légal).
  boolean randomMove() {
    return false;
  }

  // Joue un coup pour le joueur ayant le trait en s'appuyant sur une
  // simulation heuristique (playout) pour choisir ce coup. Met à jour l'état
  // du jeu comme un clic et renvoie true si un coup a été joué (false sinon).
  boolean heuristicMove() {
    return false;
  }



  public static void main(String[] args) {
    HexGUI.createAndShowGUI();
  }
}
