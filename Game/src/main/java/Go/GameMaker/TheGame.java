package Go.GameMaker;

public class TheGame {

  public TheGame(){
    //TODO jakieś rzeczy tu zrobić, przemyśleć klasę typu jakie pola powinna miec
    //
  }

  public String makeMove(String move){
    //TODO jeśli tutaj przyjdzie string to pewnie postaci "idGracza, xPosition, yPosition"
    //TODO powinien zwracać coś typu "liczba, pionki[][].toString()"
    //w liczbie np: "1"=ruch udany, kolej na ruch drugiego gracza, "0"=powtórz ruch;

    return move;
  }
  public String whoseMove(){
    //TODO tutaj jest metoda pomocnicza dla GUI, która sprawdza czyj ruch
    //póki co nie wymyśliłem jak powiadomić wszystkich klientó o ruchu
    //więc będzie na razie coś typu: jeśli client oczekuje na ruch przeciwnika to sprawdza co chwilę
    //tą metodą czy już może zrobić ruch; jak coś wymyślisz ciekawszego to daj znać
    return "69420";
  }
  public String addPlayer(String playerID){
    //TODO server bedzie dodawał tutaj id nowych graczy - prosta metoda
    //TODO powinna chyba zwracać kolor gracza, to czy znaleziono mecz mozna dac pod whoseMove();
    //bo raczej zrobię tak, że będzie sprawdzać co sekundę czy już można zrobić ruch
    return "White";
  }
}
