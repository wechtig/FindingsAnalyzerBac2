# FindingsAnalyzerBac2
__Year:__ 2020  

## Ausgangssituation und Idee
Um die Code Qualität in Projekten zu steigern, können Hilfen wie die Statische Code Analyse eingesetzt werden. Diese fokussiert aber eine Verbesserung der Fehler und nicht eine langfristige Verbesserung der Entwicklerinnen oder Entwickler. 
Daher werden die Ergebnisse der Statischen Code Analyse in eine Datenbank importiert und dort langfristig gespeichert. In der Weboberfläche können dadurch genauere und verschiedene Präsentationen der Fehler erstellt werden. 

## Übersicht
Das Projekt besteht aus drei Anwendungen:
* Importer: Maven-Plugin um die Ergebnisse der Statischen Code Analyse in der Datenbank zu speichern: https://github.com/wechtig/FindingsAnalyzerDBImporter
* FindingsAnalyzerBac2: Weboberfläche und Backend für die Analyse und Anzeige
* FindingsApp: Android-App für die Anzeige: https://github.com/wechtig/FindingsAppB2

## Ziele
* Individuelle Angabe der verwendeten Plugins für die Statische Code Analyse 
* Erstellung einer einfachen und übersichtliche Präsentation der Daten
* Implementierung einer individuelle Analyse in der Weboberfläche um die Fähigkeiten zu verbessern
* Bereitstellen von verschiedenen Präsentationsarten: Charts, Tabellen, Report, Mobile Applikation
* Anbinden einer Versionsverwaltung um genauere Hilfestellungen zu liefern
* Unterstützung von Teams

## Installation
__Vorraussetzungen__  
1. Apache Maven: https://maven.apache.org/download.cgi  
2. MongoDB-Datenbank
    
__Setup__  
1. Den Sourcecode als .zip oder über GIT herunterladen
2. Collections in der MongoDB Datenbank anlegen 
3. Einsatz von Statischen Code Analyse-Plugins und des Importer-Plugins im zu analysierten Code. Dadurch werden die Daten in die Datenbank importiert
    Beispiele Statischen Code Analyse-Plugins: https://maven.apache.org/plugins/maven-checkstyle-plugin/, https://maven.apache.org/plugins/maven-pmd-plugin/
4. Um die Sicherheit im Projekt zu gewährleisten, muss ein Administrator (Entwickler) für jedes Projekt einen eigenen Team-Administrator in der Datenbank anlegen. Diese Team-Administrator kann andere registrierte Teammitglieder einladen.

__Starten__
1. FindingsAnalyzerBac2 starten: \src\main\java\findingsAnalyzer\FindingsAnalyzerBac2Application.java oder in der Console: mvn spring-boot:run
2. In der Weboberfläche kann nun nach Projekt und Datumsbereich gefiltert und die Information angezeigt werden.  

## Screenshots
![Tabelle-Bild][table]
![Chart-Bild][chart]
## Architektur
![Architektur-Bild][architecture]
## Technologien
* Spring Boot - Java Applikationen
* MongoDB - NoSQL Datenbank
* Hibernate - Java OR-Mapper
* Maven - Build Tool und Entwicklung der Plugins 
* HTML/JS/CSS - Benutzeroberfläche
* GIT - Sourcecodeverwaltung
* Spring Security - User Verwaltung
## Ergebnisse und Fazit
Die Ergebnisse der Implementierung, die Evaluierung anhand der Testpersonen und die Vergleiche zu herkömmlichen Lösungen zeigen, dass die Ergebnisse der StatischenCode Analyse auf verschiedene Weise aufbereitet und angezeigt werden können, um den Code der Entwicklerinnen und Entwickler nachhaltig zu verbessern. Teamfunktionalitäten wie Kommentare, Git-Integration, Erstellung von Teams unterstützen die Applikation. Die Github-Integration ermöglicht eine genauere Anzeige für die Entwicklerin oder den Entwickler. 
## Weitere Möglichkeiten
* Einladen der Projektmitglieder mit Mails
* Fokussierung auf bestimmte Code Qualität-Faktoren um die Weboberfläche genauer und hilfreicher zu gestalten 
* Gitanalyse auf Code-Snippets ausweiten (Aktuell Gitanalyse für Klassen) 
* Mögliche Übersetzung der Applikation in mehrere Sprachen.
* Automatische Auswertung der Logs (z.B.: Logstash)
* Implementierung eines Punktesystems, bei Lösen von Fehlern oder Bugs um die Motivation und die Code Qualität zu steigern

[architecture]: https://github.com/wechtig/FindingsAnalyzerBac2/blob/master/architecture.PNG "Architektur"
[table]: https://github.com/wechtig/FindingsAnalyzerBac2/blob/master/table.PNG "Architektur"
[chart]: https://github.com/wechtig/FindingsAnalyzerBac2/blob/master/chart.PNG "Architektur"





