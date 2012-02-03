package czytelnia;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import baza.CbazaDanych;


public class Cbiblioteka extends CbazaDanych {
	public Cczytelnicy Czytelnicy;
	public Cksiazki Ksiazki;
	private Set<Cwypozyczenie> lista;
	
	public Cbiblioteka() throws SQLException {
		this.lista = new HashSet<Cwypozyczenie>();
		this.Czytelnicy = new Cczytelnicy();
		this.Ksiazki = new Cksiazki();
	
		// tabela wypozyczenia
		if (!this.CzyTabelaIstnieje( Etabele.wypozyczenia.toString() ) )
			this.StworzTabele("create table wypozyczenia (id integer generated always as identity, id_ksiazka integer not null, id_czytelnik integer not null, data_wypozyczenia date not null, data_zwrotu date)");
		else
			this.WyczyscTabele( Etabele.wypozyczenia.toString() );		
	}

	/*
	 *  wypo�yczenie ksi��ki z biblioteki
	 */
	public Cksiazka WypozyczenieKsiazki(Cczytelnik c,Cksiazka k) {
		Cksiazka e = null;

		try {
			e = this.Ksiazki.PobierzKsiazke(k);
			if ( !e.CzyWypozyczona() ) {			
				this.lista.add( new Cwypozyczenie(c,e) );
				
				// zapisanie do bazy danych
				Ckalendarz dzis = new Ckalendarz();
				this.WykonajInsert("insert into "+Etabele.wypozyczenia.toString()+" (id_ksiazka,id_czytelnik,data_wypozyczenia)" +
						"values " +
						"((select id from "+Etabele.ksiazki.toString()+" where isbn="+e.PobierzISBN()+")," +
						"(select id from "+Etabele.czytelnicy.toString()+" where imie='"+c.PobierzImie()+"' and nazwisko='"+c.PobierzNazwisko()+"'),'"+dzis.PobierzDate() +"')");
				this.Ksiazki.WypozyczKsiazke(e);

				k.Wypozycz();
				System.out.println("Czytelnik: "+c+" wypo�yczy� ksi��k�: "+e);
			}
			else {
				System.out.println("Ksi��ka "+e+" jest ju� wypo�yczona!");
				e = null;
			}
		}
		catch(Exception ex) {
			System.out.println( ex.getMessage() );
		}		
		
		return e;
	}
	
	/*
	 * zwr�cenie ksi��ki przez czytelnika
	 */
	public boolean ZwrotKsiazki(Cczytelnik c, Cksiazka e) throws SQLException  {
		Cwypozyczenie w = null;
		boolean out = false;
		Ckalendarz kal = new Ckalendarz();
		/*
		 * wersja z obiektami
		 * 
		Iterator<Cwypozyczenie> it = this.lista.iterator();
		while(it.hasNext()) {
			w = it.next();
			if ( w.PobierzCzytelnik().equals(c) && w.PobierzKsiazka().equals(e) ) {				
				w.Zwrot();
				Znaleziono = true;
				break;
			}
		}
		*/
		
		/*
		 *  wersja z baz� danych
		 */
		Iterator<Cwypozyczenie> it = this.lista.iterator();
		while(it.hasNext()) {
			w = it.next();
			if ( w.PobierzCzytelnik().PobierzImie()==c.PobierzImie() && w.PobierzCzytelnik().PobierzNazwisko()==c.PobierzNazwisko() && w.PobierzKsiazka().PobierzISBN()==e.PobierzISBN()) {
				this.WykonajInsert("update "+Etabele.wypozyczenia.toString()+" set data_zwrotu='"+kal.PobierzDate()+"' " +
						"where " +
						"id_czytelnik=(select id from "+Etabele.czytelnicy.toString()+" where imie='"+c.PobierzImie()+"' and nazwisko='"+c.PobierzNazwisko()+"') and " +
						"id_ksiazka=(select id from "+Etabele.ksiazki.toString()+" where isbn="+e.PobierzISBN()+")");
				
				this.Ksiazki.OddajKsiazke(e);
				out = true;
				break;
			}
		}
		
		return out;

	}
	
	public String PobierzWypozyczenia() throws SQLException {
		String s = "Wypo�yczenia: \n";
		int ile = 0;
		
		ResultSet rs = this.WykonajSelect("select concat(c.imie,' ',c.nazwisko) czyt, concat(k.tytul,' (',k.autor_imie,' ',k.autor_nazwisko,')')  ks,data_wypozyczenia,data_zwrotu " +
				"from " +
				"wypozyczenia w, czytelnicy c, ksiazki k where w.id_czytelnik=c.id and w.id_ksiazka=k.id and w.data_zwrotu is null order by w.data_wypozyczenia asc");
		while (rs.next()) {
			s = s + rs.getString("czyt") +" - "+rs.getString("ks") + ", wypo�yczenie: " +rs.getString("data_wypozyczenia")+ "\n";
			ile++;
		}
		if (ile>0)	
			return s;
		else
			return "Nie ma �adnych wypo�ycze�!";
	}
	
	public String StanBiblioteki() throws SQLException {
		String s = "Stan biblioteki: \n\n";
		
		// czytelnicy
		s = s + this.Czytelnicy +"\n\n";
		
		// ksiazki
		s = s +this.Ksiazki +"\n\n";
		
		// wypozyczenia
		s = s + this.PobierzWypozyczenia() + "\n\n";
		
		return s;
	}
	
	public String toString() {
		try {
			return this.StanBiblioteki();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return "B��d po��czenia z baz� danych! "+e.getMessage();
		}
	}	
	
}
