## -------------------------------------------------------------------
# jhotdraw projet
# Génie logiciel JHotDraw
## Authors:Gamal Daoud Youssouf
## Groupe: 1
## Partie 2 : Amélioration du projet
## ---------------------------------------------------------------------

## Enseignante : Madame   Imen Sayar  (imen.sayar@univ-lille.fr)
1
`Lien du projet :  https://github.com/wumpz/jhotdraw

## Objectifs de ce projet :
Étudier la qualité logicielle, Amélioration du projet.

## Introduction
Après avoir analysé la qualité logicielle de JHotDraw (jhotdraw-core) dans la première partie, ce rapport présente les améliorations apportées au projet. Les modifications, classées par difficulté, visent à renforcer la cohérence et la maintenabilité du code en se concentrant sur quelques classes clés. Chaque changement est documenté : situation initiale, modification réalisée également amélioration de readme.


## 6 Petites modifications:
Ces modifications visaient à nettoyer le code, corriger des ambiguïtés et des bugs mineurs évidents pour améliorer la lisibilité globale.

- les modifications apportées à la classe SlantedLiner et les raisons de ces changements :

# 1 Ajout de la constante DEFAULT_SLANT_SIZE
Ce qui a été modifié : remplacer le nombre magique 20 dans le constructeur par défaut par la constante DEFAULT_SLANT_SIZE.
Raison : Améliorer la lisibilité et la maintenabilité du code en donnant un nom explicite à cette valeur par défaut. Cela permet de comprendre immédiatement sa signification et facilite une éventuelle modification future.


# 2 Ajout des constantes PATH_SIZE_TWO_FIGURES et PATH_SIZE_SAME_FIGURE
Ce qui a été modifié : Remplacer les nombres magiques 4 et 5 par ces constantes dans les boucles while qui ajustent le nombre de nœuds du BezierPath.
Raison : Clarifier et cela rend le code auto-documenté et réduit les risques d'erreur si ces valeurs doivent être modifiées.

# 3 Ajout de constantes pour les indices de nœuds
Ce qui a été modifié : Remplacer les indices littéraux 0, 1, 2, 3 et la position d'insertion 1 par les constantes :

- START_NODE (0)
- FIRST_INTERMEDIATE_NODE (1)
- SECOND_INTERMEDIATE_NODE (2)
- THIRD_INTERMEDIATE_NODE (3)
- INSERT_INDEX (1)

Raison :rendre le code plus explicite egalement cela améliore la compréhension du code et évite d'utiliser des nombres "magiques" qui n'ont pas de signification évidente.

Ce qui a été modifié aussi : Dans le else (deux figures différentes), les boucles while utilisaient encore les nombres 4 et - Elles ont été remplacées par PATH_SIZE_TWO_FIGURES et INSERT_INDEX.
Raison : Assurer la cohérence avec le reste du code et éliminer tous les nombres magiques résiduels. Cela garantit que toute modification future de ces valeurs se fera via les constantes.

https://github.com/gamal-daoud/jhotdraw/blob/develop/jhotdraw-core/src/main/java/org/jhotdraw/draw/liner/SlantedLiner.java










## 7 Moyennes modifications





## 8 Grandes modifications