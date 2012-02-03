package czytelnia;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import wyjatki.CmojWyjatek;

import baza.CbazaDanych;

public class Cksiazki extends CbazaDanych {
	private static final String tworzenieTabeli = "create table ksiazki (id integer generated always as identity, tytul varchar(60), autor_imie varchar(40), autor_nazwisko varchar(40), isbn integer not null, wypozyczona boolean default FALSE not null)";
	private List<Cksiazka> lista;
		
	public Cksiazki() throws SQLException {
		this.lista = new ArrayList<Cksiazka>();

		if (!this.CzyTabelaIstnieje(Etabele.ksiazki.toString()))
			this.StworzTabele(tworzenieTabeli);
		else
			this.WyczyscTabele(Etabele.ksiazki.toString());		
	}
	
	public Cksiazka Dodaj(Cksiazka k) throws SQLException {
		this.lista.add(k);
		this.WykonajInsert("insert into "+Etabele.ksiazki.toString()+" (tytul,autor_imie,autor_nazwisko,isbn) values ('"+k.PobierzTytul()+"','"+k.PobierzImieAutora()+"','"+k.PobierzNazwiskoAutora()+"',"+k.PobierzISBN()+")");
		return k;
	}
	
	public void Usun(Cksiazka k) throws SQLException {
		this.lista.remove(k);
		this.WykonajDelete("delete from "+Etabele.ksiazki.toString()+" where tytul = '"+k.PobierzTytul()+"'" );
	}
	
	public void Usun(int index) throws SQLException {
		this.Usun( this.lista.get(index) );
		this.lista.remove(index);
	}
	
	public void UsunWszystkie() throws SQLException {
		this.lista.clear();
		this.WyczyscTabele( Etabele.ksiazki.toString() );
	}
	
	public Cksiazka PobierzKsiazke(int index) {
		try {
			return this.lista.get(index);
		}
		catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public Cksiazka PobierzKsiazke(Cksiazka k) throws SQLException, CmojWyjatek {
		return this.PobierzKsiazke( k.PobierzTytul() );
	}
	
	public Cksiazka PobierzKsiazke(String tytul) throws SQLException, CmojWyjatek {
		Iterator<Cksiazka> it = this.lista.iterator();
		Cksiazka out = null;
		
		/*
		 * wersja z obiektami
		 * 		 
		while (it.hasNext()) {
			Cksiazka c = it.next();
			if (c.PobierzTytul().equalsIgnoreCase(tytul)) {
				out = c;
				break;
			}
		}
		*/
		ResultSet rs = this.WykonajSelect("select * from "+Etabele.ksiazki.toString()+" where tytul='"+tytul+"'");
		while(rs.next()) {		
			out = new Cksiazka(rs.getString("tytul"), rs.getString("autor_imie"), rs.getString("autor_nazwisko"),rs.getInt("isbn") );
			if (rs.getBoolean("wypozyczona"))
				out.Wypozycz();
		}

		if (out == null)
			throw new CmojWyjatek("Nie znaleziono w bazie ksi��ki o tytule '"+tytul+"'!");
		
		return out;
	}
	
	public List<Cksiazka> PobierzKsiazkiAutora(String imie, String nazwisko) throws SQLException, CmojWyjatek {
		//Iterator<Cksiazka> it = this.lista.iterator();
		List<Cksiazka> out = new ArrayList<Cksiazka>();		
		
		/*
		 *  wersja z obiektami
		while (it.hasNext()) {
			Cksiazka c = it.next();
			if (c.PobierzImieAutora().equalsIgnoreCase(imie) && c.PobierzNazwiskoAutora().equalsIgnoreCase(nazwisko)) {
				out.add(c);
			}
		}
		*/
		
		/*
		 *  wersja z baz� danych
		 */
		ResultSet rs = this.WykonajSelect("select * from "+Etabele.ksiazki.toString()+" where autor_imie='"+imie+"' and autor_nazwisko='"+nazwisko+"'");
		while(rs.next()) 			
			out.add( new Cksiazka(rs.getString("tytul"), rs.getString("autor_imie"), rs.getString("autor_nazwisko"),rs.getInt("isbn"),rs.getBoolean("wypozyczona") ) );
		
		return out;
	}
	
	public List<Cksiazka> PobierzWypozyczoneKsiazki() throws SQLException, CmojWyjatek {
		List<Cksiazka> out = new ArrayList<Cksiazka>();		
		
		/*
		 *  wersja z baz� danych
		 */
		ResultSet rs = this.WykonajSelect("select * from "+Etabele.ksiazki.toString()+" where wypozyczona=true");
		while(rs.next()) 			
			out.add( new Cksiazka(rs.getString("tytul"), rs.getString("autor_imie"), rs.getString("autor_nazwisko"),rs.getInt("isbn"),rs.getBoolean("wypozyczona") ) );
		
		return out;
	}
	
	public List<Cksiazka> PobierzWolneKsiazki() throws SQLException, CmojWyjatek {
		List<Cksiazka> out = new ArrayList<Cksiazka>();				
		
		/*
		 *  wersja z baz� danych
		 */
		ResultSet rs = this.WykonajSelect("select * from "+Etabele.ksiazki.toString()+" where wypozyczona=false");
		while(rs.next()) 			
			out.add( new Cksiazka(rs.getString("tytul"), rs.getString("autor_imie"), rs.getString("autor_nazwisko"),rs.getInt("isbn"),rs.getBoolean("wypozyczona") ) );
		
		return out;
	}
	
	public List<Cksiazka> PobierzKsiazki() throws SQLException, CmojWyjatek {
		List<Cksiazka> out = new ArrayList<Cksiazka>();			
		/*
		 *  wersja z baz� danych
		 */
		ResultSet rs = this.WykonajSelect("select * from "+Etabele.ksiazki.toString());
		while(rs.next()) 			
			out.add( new Cksiazka(rs.getString("tytul"), rs.getString("autor_imie"), rs.getString("autor_nazwisko"),rs.getInt("isbn"),rs.getBoolean("wypozyczona") ) );
		
		return out;
	}	
	
	public int LiczbaKsiazek() throws SQLException {
		int out = 0;
		ResultSet rs = this.WykonajSelect("select count(id) liczba from "+Etabele.ksiazki.toString());
		
		if (rs.next()) 			
			out = rs.getInt("liczba");
		
		return out;	
	}
	
	public String OpisKsiazek() throws SQLException {
		String s = "Ksia�ki w bibliotece: \n";
		
		
		/*	pobranie danych z pami�ci
		 * 
		Iterator<Cksiazka> it = this.lista.iterator();
		Cksiazka k = null;
		int index = 1;
		int LiczbaKsiazek = 0;
		
		while(it.hasNext()) {
			k = it.next();
			s = s + "[" + index + "] "+k+"\n\n";
			LiczbaKsiazek++;
			index++;
		}
		
		s = s + "\n\nCa�kowita liczba ksi��ek: "+LiczbaKsiazek;
		*/
		
		ResultSet rs = this.WykonajSelect("select * from "+Etabele.ksiazki.toString());
		while(rs.next()) {
			s = s + "[" + rs.getInt("id") + "] "+ rs.getString("tytul")+" - "+rs.getString("autor_nazwisko") +", " + rs.getString("autor_imie");
			if ( !rs.getBoolean("wypozyczona") )
				s = s + " - ksi��ka dost�pna!\n";
			else
				s = s + " - ksi��ka wypo�yczona!\n";		
		}
		
		return s;
	}
	
	public String toString() {
		try {
			return this.OpisKsiazek();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return "B��d po��czenia z baz� danych! "+e.getMessage();
		}
	}
	
	public boolean OddajKsiazke(Cksiazka k) throws SQLException {
		if (k.CzyWypozyczona()) {
			k.Oddaj();
			this.WykonajInsert("update "+Etabele.ksiazki.toString()+" set wypozyczona=false where isbn="+k.PobierzISBN());
			return true;
		}
		else
			return false;
	}
	
	public boolean WypozyczKsiazke(Cksiazka k) throws SQLException {
		if (!k.CzyWypozyczona()) {
			k.Wypozycz();
			this.WykonajInsert("update "+Etabele.ksiazki.toString()+" set wypozyczona=true where isbn="+k.PobierzISBN());
			return true;
		}
		else
			return false;
	}
	

}
