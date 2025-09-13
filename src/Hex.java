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
  private UnionFind uf;
  private int[] permutation;
  private int permIndex = 0;

  enum Player {
    NOONE, BLUE, RED
  }

  Player otherPlayer(Player p){
    if(p.equals(Player.BLUE)){
      return Player.RED;
    }
    else if(p.equals(Player.RED)){
      return Player.BLUE;
    }
    else{
      return Player.NOONE;
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
    initialisUf();
    initialisePermutation();
    
  }

  void initialisUf(){
      this.uf = new UnionFind((this.n+2)*(this.n+2));
      for(int i = 1;i<this.n;i++){
        this.uf.union(convert(i, 0), convert(i+1, 0));
        this.uf.union(convert(i, this.n + 1), convert(i+1, this.n+1));
      }
      for(int j = 1;j<this.n;j++){
        this.uf.union(convert(0, j), convert(0, j+1));
        this.uf.union(convert(this.n+1, j), convert(this.n+1, j+1));
      }
  }

  void initialisePermutation(){
      this.permutation = new int[(this.n+2)*(this.n+2)];
      for(int i = 0; i < this.permutation.length; i++){
          this.permutation[i] = i;
      }
      for(int i = 0; i < this.permutation.length; i++){
          int j = (int)(Math.random()*this.permutation.length);
          int temp = this.permutation[i];
          this.permutation[i] = this.permutation[j];
          this.permutation[j] = temp;
      }
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
      if(this.get(i, j).equals(Player.NOONE)&& this.winner().equals(Player.NOONE)){
        this.board[i][j]=this.currentPlayer();
        maj(i, j);
        this.currPlayer = this.otherPlayer(this.currentPlayer());
        return true;
      }

    }
    return false;
  }

  void maj(int i, int j){
    int c = convert(i, j);

    if(board[i][j-1].equals(currPlayer)){
      this.uf.union(convert(i,j-1), c);
    }
    if(board[i+1][j-1].equals(currPlayer)){
      this.uf.union(convert(i+1,j-1), c);
    }
    if(board[i-1][j].equals(currPlayer)){
      this.uf.union(convert(i-1,j), c);
    }
    if(board[i+1][j].equals(currPlayer)){
      this.uf.union(convert(i+1,j), c);
    }
    if(board[i-1][j+1].equals(currPlayer)){
      this.uf.union(convert(i-1,j+1), c);
    }
    if(board[i][j+1].equals(currPlayer)){
      this.uf.union(convert(i,j+1), c);
    }
  }

  // Renvoie le joueur avec le trait ou Player.NOONE si le jeu est terminé par
  // la victoire d'un joueur.
  Player currentPlayer() {
    if(this.winner().equals(Player.NOONE)){
      return currPlayer;
    }
    return Player.NOONE;
  }


  // Renvoie le joueur gagnant, ou Player.NOONE si aucun joueur n'est encore
  // gagnant
  Player winner() {
    if(uf.sameClass(1, convert(1, this.n+1))){
      return Player.RED;
    }
    else if(uf.sameClass(n+2, convert(this.n+1, 1))){
      return Player.BLUE;
    }
    else{
      return Player.NOONE;
    }
  }

  int convert(int i, int j){
    return i+(this.n+2)*j;
  }

  int label(int i, int j) {
    return uf.find(convert(i, j));
  }

  // Joue un coup aléatoire pour le joueur ayant le trait, met à jour l'état
  // du jeu comme un clic sur une case, et renvoie true si un coup a été joué
  // (false si la partie est terminée ou s'il n'existe plus de coup légal).
  boolean randomMove() {
    if(this.winner()!=Player.NOONE){
      return false;
    }
    while(true){
      int label = this.permutation[this.permIndex];
      int i = label % (this.n+2);
      int j = label / (this.n+2);
      this.permIndex++;
      this.permIndex%=this.permutation.length;
      if (this.click(i, j)) {
        return true;
      }
    }
  }

  // Joue un coup pour le joueur ayant le trait en s'appuyant sur une
  // simulation heuristique (playout) pour choisir ce coup. Met à jour l'état
  // du jeu comme un clic et renvoie true si un coup a été joué (false sinon).
  boolean heuristicMove() {
    int[] move = bestMove();
    if(move[0]!=-1 && move[1]!=-1){
      this.click(move[0], move[1]);
      return true;
    }
    return false;
  }

  int[] bestMove(){
    for(int i = 0; i<n+2; i++){
      for(int j = 0; j<n+2; j++){
        UnionFind ufCopy = (UnionFind) this.uf.clone();
        Hex copie = new Hex(this.n, this.board, this.currPlayer, ufCopy, this.permutation, this.permIndex);
        Player p = copie.currPlayer;
        if(copie.click(i, j)){
          if(copie.winner().equals(p)){
            return new int[]{i, j};
          }
          else{
            int[] move = copie.bestMove();
            copie.click(move[0], move[1]);
            return copie.bestMove();
          }
        }
      }
    }
    //no winning move
    return new int[]{-1, -1};

  }

  Hex(int n, Player[][] board, Player currPlayer, UnionFind uf, int[] permutation, int permIndex){
    this.n = n;
    this.board = new Player[n+2][n+2];
    for(int i = 0; i < n+2; i++){
      for(int j = 0; j < n+2; j++){
        this.board[i][j] = board[i][j];
      }
    }
    this.currPlayer = currPlayer;
    this.uf = uf;
    this.permutation = permutation;
    this.permIndex = permIndex;
  }




  public static void main(String[] args) {
    HexGUI.createAndShowGUI();
  }
}
