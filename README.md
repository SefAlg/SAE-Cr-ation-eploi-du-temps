# SAÉ 2.01 &nbsp;&nbsp;&nbsp; Sujet 4 : Shukan - Modélisation 

## Notre sujet

### Sujet 4 : Shukan - Modélisation

**Objectif** : Concevoir et réaliser une interface utilisateur pour modéliser le calendrier et les activités d'un semestre universitaire. Cette tâche est actuellement basée sur l'édition de scripts, qu'il faut remplacer par une IHM exploitable par un non-informaticien

## Prérequis

[Java JDK 22](https://www.oracle.com/fr/java/technologies/downloads/#java22)

Et pour verifier si votre version de Java :
```
java -version
```

## Instalation


Pour télécharger les ficher vous pouvez utiliser les commandes ci-dessous.

<br>
 
- Pour effectuer ces commandes, il est nécessaire d'avoir vos identifiants de connexion à votre compte. **gitlab.univ-lorraine.fr** :
```
git clone https://gitlab.univ-lorraine.fr/dubreui19u/sae-2.0_sujet-4_shukan-modelisation.git
```
<br>

### Après le téléchargement :

- Fait ces commandes si vous êtes sur un Linux :

```
cd sae-2.0_sujet-4_shukan-modelisation
javac -classpath "lib/jcalendar-1.4.jar:lib/Shukan.jar:." Sae21.java

```

<br>

- Ou fait ces commandes si vous êtes sur un Windows :

```
cd sae-2.0_sujet-4_shukan-modelisation
javac -classpath "lib\jcalendar-1.4.jar;lib\Shukan.jar;." Sae21.java
```


## Utilisation

Normalement avec la commande du `javac`, il y a de nouveaux fichers en plus et le plus important est le fichier **Sae21.class** qui est l'executable du programme.

<br>
 
- Pour utiliser l'executable, c'est avec cette commande sur Linux :

```
java -classpath "lib/jcalendar-1.4.jar:lib/Shukan.jar:." Sae21

```

- Ou cette commande si vous êtes sur un Windows :

```
java -classpath "lib\jcalendar-1.4.jar;lib\Shukan.jar;." Sae21

```


