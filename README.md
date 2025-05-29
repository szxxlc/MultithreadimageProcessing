
# Multithread Image Processing App

## Wstęp

Projekt zawiera aplikację do przetwarzania obrazów z wykorzystaniem technologii **JavaFX** i **wielowątkowości**. Aplikacja umożliwia użytkownikowi wykonywanie różnych operacji na obrazkach takich jak:
- Negatyw,
- Progowanie,
- Konturowanie,
- Obrót (lewo/prawo),
- Skalowanie.

Zadanie zostało przygotowane w ramach laboratorium z przedmiotu **Platformy Programistyczne .NET i Java** na **Politechnice Wrocławskiej**.

---

## Opis funkcjonalności

Aplikacja umożliwia:

- Wczytywanie obrazów w formacie JPG,
- Wykonywanie operacji na obrazach takich jak:
  - Generowanie negatywu,
  - Progowanie z możliwością ustawienia progu,
  - Konturowanie obrazu,
  - Obrót o 90° w prawo lub lewo,
  - Skalowanie obrazu,
- Zapis przetworzonego obrazu do pliku z możliwością nadania nazwy,
- Obsługę aplikacji przy użyciu wielu wątków (maksymalnie 4 wątki na operację),
- Logowanie operacji i błędów do pliku tekstowego (logs.txt),
- Rejestrację otwarcia i zamknięcia aplikacji.

---

## Struktura projektu

Projekt zawiera następujące klasy i pliki:

### **HelloApplication.java**
Klasa aplikacji JavaFX, uruchamiająca interfejs użytkownika i odpowiadająca za logowanie otwarcia/zamykania aplikacji.

### **HelloController.java**
Klasa kontrolera odpowiedzialna za obsługę interfejsu użytkownika, wykonywanie operacji na obrazach, obsługę wczytywania i zapisywania plików.

### **ImageProcessor.java**
Klasa zawierająca metody do przetwarzania obrazów (negatyw, progowanie, konturowanie, obrót, skalowanie).

### **MultithreadProcessing.java**
Klasa zarządzająca równoległym przetwarzaniem obrazów za pomocą maksymalnie 4 wątków.

### **AppLogger.java**
Klasa służąca do logowania informacji, ostrzeżeń oraz błędów do pliku `logs.txt`.

---

## Testowanie i logi

Aplikacja zapisuje logi do pliku `logs.txt`, w tym:
- Uruchomienie i zamknięcie aplikacji,
- Wykonanie operacji na obrazach,
- Błędy związane z wczytywaniem lub zapisywaniem plików.

Logi są zapisywane w formacie:

```
2023-08-15 12:45:30 [INFO]: Aplikacja została otwarta
2023-08-15 12:46:00 [ERROR]: Nie udało się zapisać pliku example.jpg
```

---

## Technologie

- **Java 23**: Wersja Javy, na której działa aplikacja.
- **Maven**: Narzędzie do zarządzania projektem i zależnościami
- **JavaFX**: Technologia do tworzenia interfejsu graficznego aplikacji.
- **Multithreading**: Wykorzystanie wielowątkowości za pomocą `ExecutorService` do operacji na obrazach.
