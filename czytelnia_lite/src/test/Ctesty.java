package test;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import wyjatki.CmojWyjatek;

import czytelnia.Cbiblioteka;
import czytelnia.Cczytelnik;
import czytelnia.Ckalendarz;
import czytelnia.Cksiazka;
import czytelnia.Cwypozyczenie;

public class Ctesty {
	
	private static Cbiblioteka b = null;
	
	private static Cczytelnik c1 = null;
	private static Cczytelnik c2 = null;
	private static Cczytelnik c3 = null;
	private static Cczytelnik c4 = null;
	
	private static Cksiazka k1 = null;
	private static Cksiazka k2 = null;
	
	private static Cwypozyczenie w1 = null;

	@Before
	public void setUp() throws Exception {
		// obiekt biblioteka
		b = new Cbiblioteka();
		
		// obiekty czytelników
		c1 = new Cczytelnik("Roman","Pi³ka",new Ckalendarz(1984,Calendar.MAY,16) , new Ckalendarz(2011,Calendar.JULY,11) );
		c2 = new Cczytelnik("Bart³omiej","G¹bka",new Ckalendarz(1956,Calendar.DECEMBER,8) , new Ckalendarz(2000,Calendar.JANUARY,1) );
		c3 = new Cczytelnik("Micha³","Wo³odyjowski",new Ckalendarz(1650,Calendar.JUNE,30) , new Ckalendarz(1660,Calendar.MARCH,29) );
		c4 = new Cczytelnik("Gandalf","Szary",new Ckalendarz(1234,Calendar.AUGUST,13) , new Ckalendarz(1830,Calendar.MARCH,2) );
		
		// obiekty ksi¹¿ek
		k1 = new Cksiazka("Medaliony","Zofia","Na³kowska",112233);
		k2 = new Cksiazka("Flags of our fathers","James","Bradley",44456765);
		
		b.Czytelnicy.Dodaj(c1);
		b.Czytelnicy.Dodaj(c2);
		b.Czytelnicy.Dodaj( new Cczytelnik("Adam","Ma³ysz",new Ckalendarz(1970,Calendar.NOVEMBER,15), new Ckalendarz(1985,Calendar.APRIL,5)) );
		b.Czytelnicy.Dodaj(c3);
		b.Czytelnicy.Dodaj(c4);
		b.Czytelnicy.Dodaj( new Cczytelnik("Obi-Wan","Kenobi",new Ckalendarz(1990,Calendar.MAY,16), new Ckalendarz(1999,Calendar.OCTOBER,29)) );	
		
		b.Ksiazki.Dodaj(k1);		
		b.Ksiazki.Dodaj( new Cksiazka("Flayboys","James","Bradley",778997) );
		b.Ksiazki.Dodaj( new Cksiazka("Gringo w krainie dzikich plemion","Wojciech","Cejrowski",121212) );
		b.Ksiazki.Dodaj(k2);
		b.Ksiazki.Dodaj( new Cksiazka("W ksiê¿ycow¹ jasn¹ noc","William","Wharton",1432466) );
	}
	
	@After
	public void tearDown() throws Exception {
		//b.WykonajInsert("SHUTDOWN");
	}
	
	/*
	 * 
	 * 	TESTY dla klasy Cczytelnicy
	 * 
	 */

	@Test
	public void TestCzytelnicyDodaj() throws CmojWyjatek, SQLException {
	
		// powinno byæ 6 czytelnikow
		assertEquals(6,b.Czytelnicy.LiczbaCzytelnikow());		
	}
	
	@Test
	public void TestCzytelnicyUsun() throws CmojWyjatek, SQLException {
		
		b.Czytelnicy.Usun(c1);
		b.Czytelnicy.Usun((int)4);		
		
		// powinno byæ 4 czytelnikow po usuniêciu - wykorzystanie moetody ListaCzytelników do pobrania czytelników
		List<Cczytelnik> lista = b.Czytelnicy.ListaCzytelnikow();
		
		assertEquals(4,lista.size());		
	}
	
	@Test
	public void TestCzytelnicyZnajdzNumer() throws CmojWyjatek, SQLException {		
		Cczytelnik c = b.Czytelnicy.ZnajdzCzytelnika(5);

		assertSame("Kenobi",c.PobierzNazwisko());		
	}		

	@Test
	public void TestCzytelnicyZnajdzNieistniejacego() throws CmojWyjatek, SQLException {		
		// proba znalezenia nieistniej¹cego czytelnika
		Cczytelnik c = b.Czytelnicy.ZnajdzCzytelnika("Wojciech","Damer");

		assertNull(c);		
	}		
	
	/*
	 * 
	 * 	TESTY dla klasy Cksiazki - czeœæ 1
	 * 
	 */

	@Test
	public void TestKsiazkiDodaj() throws CmojWyjatek, SQLException {

		
		assertEquals(5,b.Ksiazki.LiczbaKsiazek());
	}	
	
	@Test
	public void TestKsiazkiUsun() throws CmojWyjatek, SQLException {
		b.Ksiazki.Usun(2);
		
		assertEquals(4,b.Ksiazki.PobierzKsiazki().size());
	}		
	
	@Test
	public void TestKsiazkiZnajdzKsiazkiAutora() throws CmojWyjatek, SQLException {
		List<Cksiazka> lista = b.Ksiazki.PobierzKsiazkiAutora("James", "Bradley");
		
		assertTrue( lista.size() == 2);
	}		
	
	@Test
	public void TestKsiazkiPobierzNumer() throws CmojWyjatek, SQLException {		
		// nie ma ksi¹¿ki z indeksem 5 - metoda powinna zwróciæ null
		Cksiazka c = b.Ksiazki.PobierzKsiazke(5);
		
		assertNull( c );
	}	
	
	@Test
	public void TestKsiazkiPobierzTytul() throws CmojWyjatek, SQLException {		
		// nie ma ksi¹¿ki z indeksem 5 - metoda powinna zwróciæ null
		Cksiazka c = b.Ksiazki.PobierzKsiazke("Flayboys");
		
		assertEquals( "Flayboys", c.PobierzTytul() );
	}	
	
	@Test
	public void TestKsiazkiPobierzKsiazka() throws CmojWyjatek, SQLException {		
		// nie ma ksi¹¿ki z indeksem 5 - metoda powinna zwróciæ null
		Cksiazka c = b.Ksiazki.PobierzKsiazke(k1);
		
		assertNotNull( c );
	}	
	
	/*
	 * 
	 * 	TESTY dla klasy Cbiblioteka
	 * 
	 */	
	@Test
	public void TestBibliotekaWypozyczenie() throws CmojWyjatek, SQLException {		
		Cksiazka k = b.WypozyczenieKsiazki(c1, k2);
		assertEquals(k.PobierzISBN(),k2.PobierzISBN());
	}	
	
	
	@Test
	public void TestBibliotekaWypozyczenieKsiazka() throws CmojWyjatek, SQLException {		
		Cksiazka k = b.WypozyczenieKsiazki(c1, k2);
		assertTrue( k.CzyWypozyczona() );
	}		
	
	
	@Test
	public void TestBibliotekaWypozyczenieKsiazka2() throws CmojWyjatek, SQLException {		
		b.WypozyczenieKsiazki(c1, k2);
		b.WypozyczenieKsiazki(b.Czytelnicy.ZnajdzCzytelnika("Obi-Wan", "Kenobi"), b.Ksiazki.PobierzKsiazke("Flayboys"));
		
		assertTrue(2==b.Ksiazki.PobierzWypozyczoneKsiazki().size());
	}
	
	
	
	@Test
	public void TestBibliotekaWypozyczeniePozyczonej() throws CmojWyjatek, SQLException {		
		b.WypozyczenieKsiazki(c1, k2);
		b.WypozyczenieKsiazki(b.Czytelnicy.ZnajdzCzytelnika("Obi-Wan", "Kenobi"),b.Ksiazki.PobierzKsiazke("W ksiê¿ycow¹ jasn¹ noc"));
		
		// wypozyczenie ju¿ po¿yczonej ksi¹¿ki koñczy siê niepowodzeniem
		assertNull( b.WypozyczenieKsiazki(b.Czytelnicy.ZnajdzCzytelnika("Micha³", "Wo³odyjowski"),k2) );
	}	
	
	@Test
	public void TestBibliotekaZwrot() throws CmojWyjatek, SQLException {		
		b.WypozyczenieKsiazki(c1, k2);
		b.WypozyczenieKsiazki(b.Czytelnicy.ZnajdzCzytelnika("Obi-Wan", "Kenobi"),k1);
		b.WypozyczenieKsiazki(b.Czytelnicy.ZnajdzCzytelnika(2),b.Ksiazki.PobierzKsiazke(1));
		
		b.ZwrotKsiazki(c1, k2);

		// bylo 5 ksiazek, trzy wypozyczono i jedna zwrocono, wiec pozostalo wolnych = 3
		assertEquals( 3 ,b.Ksiazki.PobierzWolneKsiazki().size() );
	}	
	
	
}
